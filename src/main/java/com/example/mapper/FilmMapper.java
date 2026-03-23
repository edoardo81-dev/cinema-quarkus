package com.example.mapper;

import com.example.dto.FilmRequestDTO;
import com.example.dto.FilmResponseDTO;
import com.example.model.Film;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FilmMapper {

    public Film toEntity(FilmRequestDTO dto) {
        Film film = new Film();
        film.setTitolo(dto.getTitolo());
        film.setGenere(dto.getGenere());
        film.setAnno(dto.getAnno());
        film.setDurataMinuti(dto.getDurataMinuti());
        film.setImageUrl(dto.getImageUrl());
        return film;
    }

    public FilmResponseDTO toResponseDTO(Film film) {
        return new FilmResponseDTO(
                film.getId(),
                film.getTitolo(),
                film.getGenere(),
                film.getAnno(),
                film.getDurataMinuti(),
                film.getImageUrl()
        );
    }
}