package com.example.dto;

import jakarta.validation.constraints.Max;
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
public class FilmRequestDTO {

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(max = 150, message = "Il titolo non può superare 150 caratteri")
    private String titolo;

    @NotBlank(message = "Il genere è obbligatorio")
    @Size(max = 100, message = "Il genere non può superare 100 caratteri")
    private String genere;

    @NotNull(message = "L'anno è obbligatorio")
    @Min(value = 1888, message = "Controlla bene la data inserita")
    @Max(value = 2100, message = "L'anno non può essere maggiore di 2100")
    private Integer anno;

    @NotNull(message = "La durata è obbligatoria")
    @Min(value = 1, message = "La durata deve essere maggiore di 0")
    @Max(value = 500, message = "La durata non può essere maggiore di 500 minuti")
    private Integer durataMinuti;

    @Size(max = 500, message = "L'imageUrl non può superare 500 caratteri")
    private String imageUrl;
}