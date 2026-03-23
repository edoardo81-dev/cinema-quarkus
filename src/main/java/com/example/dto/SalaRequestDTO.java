package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaRequestDTO {

    @NotBlank(message = "Il nome della sala è obbligatorio")
    @Size(max = 100, message = "Il nome della sala non può superare 100 caratteri")
    private String nome;

    @NotNull(message = "La capienza è obbligatoria")
    @Min(value = 1, message = "La capienza deve essere maggiore di 0")
    private Integer capienza;
}