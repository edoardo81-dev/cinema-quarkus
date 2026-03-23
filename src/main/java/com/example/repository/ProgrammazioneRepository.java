package com.example.repository;

import com.example.model.Programmazione;
import com.example.model.query.QProgrammazione;
import io.ebean.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProgrammazioneRepository {

    private static final String FILM = "film";
    private static final String SALA = "sala";
    private static final String FASCIA_ORARIA = "fasciaOraria";
    private static final int INTERVALLO_TECNICO_MINUTI = 10;
    private static final int TEMPO_RICAMBIO_SALA_MINUTI = 15;
    private static final int TEMPO_EXTRA_SALA_MINUTI =
            INTERVALLO_TECNICO_MINUTI + TEMPO_RICAMBIO_SALA_MINUTI;

    private final Database database;

    @Inject
    public ProgrammazioneRepository(Database database) {
        this.database = database;
    }

    public List<Programmazione> findAll() {
        return new QProgrammazione(database)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .orderBy().orarioInizio.asc()
                .findList();
    }

    public Optional<Programmazione> findById(Long id) {
        Programmazione programmazione = new QProgrammazione(database)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .id.equalTo(id)
                .findOne();

        return Optional.ofNullable(programmazione);
    }

    public List<Programmazione> findBySalaId(Long salaId) {
        return new QProgrammazione(database)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .sala.id.equalTo(salaId)
                .orderBy().orarioInizio.asc()
                .findList();
    }

    public List<Programmazione> findByFilmId(Long filmId) {
        return new QProgrammazione(database)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .film.id.equalTo(filmId)
                .orderBy().orarioInizio.asc()
                .findList();
    }

    public List<Programmazione> findByFasciaOrariaId(Long fasciaOrariaId) {
        return new QProgrammazione(database)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .fasciaOraria.id.equalTo(fasciaOrariaId)
                .orderBy().orarioInizio.asc()
                .findList();
    }

    public List<Programmazione> findBySalaIdAndIdNot(Long salaId, Long id) {
        return new QProgrammazione(database)
                .fetch(FILM)
                .fetch(SALA)
                .fetch(FASCIA_ORARIA)
                .sala.id.equalTo(salaId)
                .id.notEqualTo(id)
                .orderBy().orarioInizio.asc()
                .findList();
    }

    public Programmazione save(Programmazione programmazione) {
        database.save(programmazione);
        return programmazione;
    }

    public void delete(Programmazione programmazione) {
        database.delete(programmazione);
    }

    public boolean existsByFilmId(Long filmId) {
        return new QProgrammazione(database)
                .film.id.equalTo(filmId)
                .findCount() > 0;
    }

    public boolean existsBySalaId(Long salaId) {
        return new QProgrammazione(database)
                .sala.id.equalTo(salaId)
                .findCount() > 0;
    }

    public boolean existsByFasciaOrariaId(Long fasciaOrariaId) {
        return new QProgrammazione(database)
                .fasciaOraria.id.equalTo(fasciaOrariaId)
                .findCount() > 0;
    }

    public boolean existsBySalaAndFasciaOraria(Long salaId, Long fasciaOrariaId) {
        return new QProgrammazione(database)
                .sala.id.equalTo(salaId)
                .fasciaOraria.id.equalTo(fasciaOrariaId)
                .findCount() > 0;
    }

    public boolean existsByFilmAndFasciaOraria(Long filmId, Long fasciaOrariaId) {
        return new QProgrammazione(database)
                .film.id.equalTo(filmId)
                .fasciaOraria.id.equalTo(fasciaOrariaId)
                .findCount() > 0;
    }

    public boolean existsBySalaAndFasciaOrariaAndIdNot(Long salaId, Long fasciaOrariaId, Long id) {
        return new QProgrammazione(database)
                .sala.id.equalTo(salaId)
                .fasciaOraria.id.equalTo(fasciaOrariaId)
                .id.notEqualTo(id)
                .findCount() > 0;
    }

    public boolean existsByFilmAndFasciaOrariaAndIdNot(Long filmId, Long fasciaOrariaId, Long id) {
        return new QProgrammazione(database)
                .film.id.equalTo(filmId)
                .fasciaOraria.id.equalTo(fasciaOrariaId)
                .id.notEqualTo(id)
                .findCount() > 0;
    }

    public boolean existsOverlappingProgrammazioneInSala(Long salaId, LocalTime nuovoInizio, LocalTime nuovoFine) {
        List<Programmazione> programmazioniSala = findBySalaId(salaId);

        return programmazioniSala.stream().anyMatch(programmazione -> {
            LocalTime inizioEsistente = programmazione.getOrarioInizio();
            LocalTime fineEsistente = calculateOrarioFine(programmazione);

            return nuovoInizio.isBefore(fineEsistente) && nuovoFine.isAfter(inizioEsistente);
        });
    }

    public boolean existsOverlappingProgrammazioneInSalaAndIdNot(Long salaId,
                                                                 LocalTime nuovoInizio,
                                                                 LocalTime nuovoFine,
                                                                 Long id) {
        List<Programmazione> programmazioniSala = findBySalaIdAndIdNot(salaId, id);

        return programmazioniSala.stream().anyMatch(programmazione -> {
            LocalTime inizioEsistente = programmazione.getOrarioInizio();
            LocalTime fineEsistente = calculateOrarioFine(programmazione);

            return nuovoInizio.isBefore(fineEsistente) && nuovoFine.isAfter(inizioEsistente);
        });
    }

    private LocalTime calculateOrarioFine(Programmazione programmazione) {
        return programmazione.getOrarioInizio()
                .plusMinutes(programmazione.getFilm().getDurataMinuti())
                .plusMinutes(TEMPO_EXTRA_SALA_MINUTI);
    }

    
}