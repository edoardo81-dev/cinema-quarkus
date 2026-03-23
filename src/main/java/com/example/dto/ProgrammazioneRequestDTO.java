package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammazioneRequestDTO {

    @NotNull(message = "L'id del film è obbligatorio")
    private Long filmId;

    @NotNull(message = "L'id della sala è obbligatorio")
    private Long salaId;

    @NotNull(message = "L'id della fascia oraria è obbligatorio")
    private Long fasciaOrariaId;

    @NotNull(message = "L'orario di inizio è obbligatorio")
    private LocalTime orarioInizio;

    @NotNull(message = "La data di inizio programmazione è obbligatoria")
    private LocalDate dataInizioProgrammazione;

    @NotNull(message = "La data di fine programmazione è obbligatoria")
    private LocalDate dataFineProgrammazione;
}