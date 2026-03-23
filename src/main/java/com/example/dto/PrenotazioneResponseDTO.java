package com.example.dto;

import com.example.model.StatoPrenotazione;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioneResponseDTO {

    private Long id;
    private String nomeCliente;
    private String emailCliente;
    private Integer numeroPosti;
    private String dataSpettacolo;
    private LocalDateTime createdAt;
    private String codicePrenotazione;
    private StatoPrenotazione stato;

    private Long programmazioneId;
    private Long filmId;
    private String titoloFilm;
    private Long salaId;
    private String nomeSala;
    private Integer capienzaSala;
    private Long fasciaOrariaId;
    private String nomeFasciaOraria;
    private LocalTime orarioInizio;
    private LocalTime orarioFine;
    

    private BigDecimal totalePrezzo;
}