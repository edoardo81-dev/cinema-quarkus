package com.example.resource;

import com.example.service.PrenotazioneEventBroadcaster;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/api/public/events/prenotazioni")
public class PrenotazioneEventsResource {

    private final PrenotazioneEventBroadcaster prenotazioneEventBroadcaster;

    @Inject
    public PrenotazioneEventsResource(PrenotazioneEventBroadcaster prenotazioneEventBroadcaster) {
        this.prenotazioneEventBroadcaster = prenotazioneEventBroadcaster;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void stream(@Context SseEventSink sink, @Context Sse sse) {
        prenotazioneEventBroadcaster.register(sink, sse);
    }
}