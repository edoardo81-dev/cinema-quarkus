package com.example.repository;

import com.example.model.Prenotazione;
import com.example.model.StatoPrenotazione;
import com.example.model.query.QPrenotazione;
import io.ebean.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PrenotazioneRepository {

    private static final String PROGRAMMAZIONE = "programmazione";
    private static final String FILM = "programmazione.film";
    private static final String SALA = "programmazione.sala";
    private static final String FASCIA_ORARIA = "programmazione.fasciaOraria";
    private static final String PROGRAMMAZIONE_ID = "programmazione.id";

    private final Database database;

    @Inject
    public PrenotazioneRepository(Database database) {
        this.database = database;
    }

    public List<Prenotazione> findAll() {
        return new QPrenotazione(database)
                .fetch(PROGRAMMAZIONE)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .orderBy().createdAt.desc()
                .findList();
    }

    public Optional<Prenotazione> findById(Long id) {
        Prenotazione prenotazione = new QPrenotazione(database)
                .fetch(PROGRAMMAZIONE)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA).id.equalTo(id)
                .findOne();
        return Optional.ofNullable(prenotazione);
    }

    public List<Prenotazione> findByProgrammazioneId(Long programmazioneId) {
        return database.find(Prenotazione.class)
                .fetch(PROGRAMMAZIONE)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .where()
                .eq(PROGRAMMAZIONE_ID, programmazioneId)
                .orderBy("createdAt desc")
                .findList();
    }

    public int sumPostiAttiviByProgrammazioneId(Long programmazioneId) {
        List<Prenotazione> prenotazioni = database.find(Prenotazione.class)
                .where()
                .eq(PROGRAMMAZIONE_ID, programmazioneId)
                .eq("stato", StatoPrenotazione.ATTIVA)
                .findList();

        return prenotazioni.stream()
                .map(Prenotazione::getNumeroPosti)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public Prenotazione save(Prenotazione prenotazione) {
        database.save(prenotazione);
        return prenotazione;
    }

    public void delete(Prenotazione prenotazione) {
        database.delete(prenotazione);
    }

    public boolean existsByProgrammazioneId(Long programmazioneId) {
        return database.find(Prenotazione.class)
                .where()
                .eq(PROGRAMMAZIONE_ID, programmazioneId)
                .findCount() > 0;
    }
}