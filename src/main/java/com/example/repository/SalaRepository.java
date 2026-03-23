package com.example.repository;

import com.example.model.Sala;
import com.example.model.query.QSala;
import io.ebean.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class SalaRepository {

    private final Database database;

    @Inject
    public SalaRepository(Database database) {
        this.database = database;
    }

    public List<Sala> findAll() {
        return new QSala(database)
                .orderBy().nome.asc()
                .findList();
    }

    public Optional<Sala> findById(Long id) {
        Sala sala = new QSala(database)
                .id.equalTo(id)
                .findOne();
        return Optional.ofNullable(sala);
    }

    public Sala save(Sala sala) {
        database.save(sala);
        return sala;
    }

    public void delete(Sala sala) {
        database.delete(sala);
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        String normalizedNome = normalize(nome);

        return findAll()
                .stream()
                .map(Sala::getNome)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(existingNome -> existingNome.equals(normalizedNome));
    }

    public boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id) {
        String normalizedNome = normalize(nome);

        return findAll()
                .stream()
                .filter(sala -> sala.getId() != null && !sala.getId().equals(id))
                .map(Sala::getNome)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(existingNome -> existingNome.equals(normalizedNome));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}