package com.example.repository;

import com.example.model.Film;
import com.example.model.query.QFilm;
import io.ebean.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class FilmRepository {

    private final Database database;

    @Inject
    public FilmRepository(Database database) {
        this.database = database;
    }

    public List<Film> findAll() {
        return new QFilm(database)
                .orderBy().titolo.asc()
                .findList();
    }

    public Optional<Film> findById(Long id) {
        Film film = new QFilm(database)
                .id.equalTo(id)
                .findOne();
        return Optional.ofNullable(film);
    }

    public Film save(Film film) {
        database.save(film);
        return film;
    }

    public void delete(Film film) {
        database.delete(film);
    }

    public boolean existsByTitoloIgnoreCase(String titolo) {
        String normalizedTitolo = normalize(titolo);

        return findAll()
                .stream()
                .map(Film::getTitolo)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(existingTitolo -> existingTitolo.equals(normalizedTitolo));
    }

    public boolean existsByTitoloIgnoreCaseAndIdNot(String titolo, Long id) {
        String normalizedTitolo = normalize(titolo);

        return findAll()
                .stream()
                .filter(film -> film.getId() != null && !film.getId().equals(id))
                .map(Film::getTitolo)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(existingTitolo -> existingTitolo.equals(normalizedTitolo));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}