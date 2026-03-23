package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammazioneResponseDTO {

    private Long id;

    private Long filmId;
    private String titoloFilm;
    private String genereFilm;
    private Integer annoFilm;
    private Integer durataMinuti;
    private String imageUrl;

    private Long salaId;
    private String nomeSala;
    private Integer capienzaSala;

    private Long fasciaOrariaId;
    private String nomeFasciaOraria;
    private BigDecimal prezzo;

    private LocalTime orarioInizio;
    private LocalTime orarioFine;

    private String dataInizioProgrammazione;
    private String dataFineProgrammazione;

    private Integer postiDisponibili;
}