package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioneRequestDTO {

    @NotBlank(message = "Il nome cliente è obbligatorio")
    @Size(max = 100, message = "Il nome cliente non può superare 100 caratteri")
    private String nomeCliente;

    @NotBlank(message = "L'email cliente è obbligatoria")
    @Email(message = "L'email cliente non è valida")
    @Size(max = 150, message = "L'email cliente non può superare 150 caratteri")
    private String emailCliente;

    @NotNull(message = "Il numero posti è obbligatorio")
    @Min(value = 1, message = "Il numero posti deve essere maggiore di 0")
    private Integer numeroPosti;

    @NotNull(message = "La data dello spettacolo è obbligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataSpettacolo;

    @NotNull(message = "L'id della programmazione è obbligatorio")
    private Long programmazioneId;
}