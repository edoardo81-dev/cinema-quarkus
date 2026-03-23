package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmResponseDTO {

    private Long id;
    private String titolo;
    private String genere;
    private Integer anno;
    private Integer durataMinuti;
    private String imageUrl;
}