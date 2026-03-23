package com.example.service;

import com.example.dto.PrenotazioneEventDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class PrenotazioneEventBroadcaster {

    private final CopyOnWriteArrayList<Subscription> subscriptions = new CopyOnWriteArrayList<>();

    public void register(SseEventSink sink, Sse sse) {
        Subscription subscription = new Subscription(sink, sse);
        subscriptions.add(subscription);

        OutboundSseEvent connectedEvent = sse.newEventBuilder()
                .name("connected")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, "connected")
                .build();

        sink.send(connectedEvent);
    }

    public void broadcast(PrenotazioneEventDTO payload) {
        subscriptions.removeIf(subscription -> subscription.sink().isClosed());

        for (Subscription subscription : subscriptions) {
            try {
                OutboundSseEvent event = subscription.sse().newEventBuilder()
                        .name("prenotazione-update")
                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
                        .data(PrenotazioneEventDTO.class, payload)
                        .build();

                subscription.sink().send(event);
            } catch (Exception e) {
                subscriptions.remove(subscription);
            }
        }
    }

    private record Subscription(SseEventSink sink, Sse sse) {
    }
}