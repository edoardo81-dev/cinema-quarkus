package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioneEventDTO {
    private String type;
    private Long prenotazioneId;
    private Long programmazioneId;
    private Integer postiDisponibiliResidui;
}