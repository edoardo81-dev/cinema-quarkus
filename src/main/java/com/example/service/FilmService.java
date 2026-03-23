package com.example.service;

import com.example.dto.FilmRequestDTO;
import com.example.dto.FilmResponseDTO;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.FilmMapper;
import com.example.model.Film;
import com.example.repository.FilmRepository;
import com.example.repository.ProgrammazioneRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FilmService {

    private static final String FILM_ID_PREFIX = "Film con id ";
    private static final String NOT_FOUND_SUFFIX = " non trovato";

    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;
    private final ProgrammazioneRepository programmazioneRepository;

    @Inject
    public FilmService(FilmRepository filmRepository,
                       FilmMapper filmMapper,
                       ProgrammazioneRepository programmazioneRepository) {
        this.filmRepository = filmRepository;
        this.filmMapper = filmMapper;
        this.programmazioneRepository = programmazioneRepository;
    }

    public List<FilmResponseDTO> getAllFilms() {
        return filmRepository.findAll()
                .stream()
                .map(filmMapper::toResponseDTO)
                .toList();
    }

    public FilmResponseDTO getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FILM_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        return filmMapper.toResponseDTO(film);
    }

    public FilmResponseDTO createFilm(FilmRequestDTO dto) {
        if (filmRepository.existsByTitoloIgnoreCase(dto.getTitolo())) {
            throw new ConflictException("Esiste già un film con titolo '" + dto.getTitolo() + "'");
        }

        Film film = filmMapper.toEntity(dto);
        Film savedFilm = filmRepository.save(film);
        return filmMapper.toResponseDTO(savedFilm);
    }

    public FilmResponseDTO updateFilm(Long id, FilmRequestDTO dto) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FILM_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        if (filmRepository.existsByTitoloIgnoreCaseAndIdNot(dto.getTitolo(), id)) {
            throw new ConflictException("Esiste già un film con titolo '" + dto.getTitolo() + "'");
        }

        existingFilm.setTitolo(dto.getTitolo());
        existingFilm.setGenere(dto.getGenere());
        existingFilm.setAnno(dto.getAnno());
        existingFilm.setDurataMinuti(dto.getDurataMinuti());
        existingFilm.setImageUrl(dto.getImageUrl());

        Film updatedFilm = filmRepository.save(existingFilm);
        return filmMapper.toResponseDTO(updatedFilm);
    }

    public void deleteFilm(Long id) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FILM_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        if (programmazioneRepository.existsByFilmId(id)) {
            throw new ConflictException("Impossibile eliminare il film perché è presente in una programmazione");
        }

        filmRepository.delete(existingFilm);
    }
}