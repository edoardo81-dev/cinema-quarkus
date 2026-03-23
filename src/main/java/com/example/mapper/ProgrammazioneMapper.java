package com.example.mapper;

import com.example.dto.ProgrammazioneResponseDTO;
import com.example.model.FasciaOraria;
import com.example.model.Film;
import com.example.model.Programmazione;
import com.example.model.Sala;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class ProgrammazioneMapper {

    private static final int INTERVALLO_TECNICO_MINUTI = 10;
    private static final int TEMPO_RICAMBIO_SALA_MINUTI = 15;
    private static final int TEMPO_EXTRA_SALA_MINUTI =
            INTERVALLO_TECNICO_MINUTI + TEMPO_RICAMBIO_SALA_MINUTI;

    private static final DateTimeFormatter ITALIAN_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Programmazione toEntity(Film film,
                                   Sala sala,
                                   FasciaOraria fasciaOraria,
                                   LocalTime orarioInizio,
                                   LocalDate dataInizioProgrammazione,
                                   LocalDate dataFineProgrammazione) {
        Programmazione programmazione = new Programmazione();
        programmazione.setFilm(film);
        programmazione.setSala(sala);
        programmazione.setFasciaOraria(fasciaOraria);
        programmazione.setOrarioInizio(orarioInizio);
        programmazione.setDataInizioProgrammazione(dataInizioProgrammazione);
        programmazione.setDataFineProgrammazione(dataFineProgrammazione);
        return programmazione;
    }

    public ProgrammazioneResponseDTO toResponseDTO(Programmazione programmazione, Integer postiDisponibili) {
        Film film = programmazione.getFilm();
        Sala sala = programmazione.getSala();
        FasciaOraria fasciaOraria = programmazione.getFasciaOraria();

        LocalTime orarioFine = programmazione.getOrarioInizio()
                .plusMinutes(film.getDurataMinuti())
                .plusMinutes(TEMPO_EXTRA_SALA_MINUTI);

        return new ProgrammazioneResponseDTO(
                programmazione.getId(),

                film.getId(),
                film.getTitolo(),
                film.getGenere(),
                film.getAnno(),
                film.getDurataMinuti(),
                film.getImageUrl(),

                sala.getId(),
                sala.getNome(),
                sala.getCapienza(),

                fasciaOraria.getId(),
                fasciaOraria.getNome(),
                fasciaOraria.getPrezzo(),

                programmazione.getOrarioInizio(),
                orarioFine,

                formatItalianDate(programmazione.getDataInizioProgrammazione()),
                formatItalianDate(programmazione.getDataFineProgrammazione()),

                postiDisponibili
        );
    }

    private String formatItalianDate(LocalDate date) {
        return date == null ? null : date.format(ITALIAN_DATE_FORMATTER);
    }
}