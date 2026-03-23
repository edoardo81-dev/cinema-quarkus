package com.example.mapper;

import com.example.dto.PrenotazioneResponseDTO;
import com.example.model.Prenotazione;
import com.example.model.Programmazione;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class PrenotazioneMapper {

        private static final int INTERVALLO_TECNICO_MINUTI = 10;
        private static final int TEMPO_RICAMBIO_SALA_MINUTI = 15;
        private static final int TEMPO_EXTRA_SALA_MINUTI = INTERVALLO_TECNICO_MINUTI + TEMPO_RICAMBIO_SALA_MINUTI;

        private static final DateTimeFormatter ITALIAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        public PrenotazioneResponseDTO toResponseDTO(Prenotazione prenotazione,
                        BigDecimal totalePrezzo) {
                Programmazione programmazione = prenotazione.getProgrammazione();

                LocalTime orarioFine = programmazione.getOrarioInizio()
                                .plusMinutes(programmazione.getFilm().getDurataMinuti())
                                .plusMinutes(TEMPO_EXTRA_SALA_MINUTI);

                return new PrenotazioneResponseDTO(
                                prenotazione.getId(),
                                prenotazione.getNomeCliente(),
                                prenotazione.getEmailCliente(),
                                prenotazione.getNumeroPosti(),
                                formatItalianDate(prenotazione.getDataSpettacolo()),
                                prenotazione.getCreatedAt(),
                                prenotazione.getCodicePrenotazione(),
                                prenotazione.getStato(),

                                programmazione.getId(),
                                programmazione.getFilm().getId(),
                                programmazione.getFilm().getTitolo(),
                                programmazione.getSala().getId(),
                                programmazione.getSala().getNome(),
                                programmazione.getSala().getCapienza(),
                                programmazione.getFasciaOraria().getId(),
                                programmazione.getFasciaOraria().getNome(),
                                programmazione.getOrarioInizio(),
                                orarioFine,

                                totalePrezzo);
        }

        private String formatItalianDate(LocalDate date) {
                return date == null ? null : date.format(ITALIAN_DATE_FORMATTER);
        }
}