package com.example.repository;

import com.example.model.FasciaOraria;
import com.example.model.query.QFasciaOraria;
import io.ebean.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class FasciaOrariaRepository {

    private final Database database;

    @Inject
    public FasciaOrariaRepository(Database database) {
        this.database = database;
    }

    public List<FasciaOraria> findAll() {
        return new QFasciaOraria(database)
                .orderBy().nome.asc()
                .findList();
    }

    public Optional<FasciaOraria> findById(Long id) {
        FasciaOraria fasciaOraria = new QFasciaOraria(database)
                .id.equalTo(id)
                .findOne();
        return Optional.ofNullable(fasciaOraria);
    }

    public FasciaOraria save(FasciaOraria fasciaOraria) {
        database.save(fasciaOraria);
        return fasciaOraria;
    }

    public void delete(FasciaOraria fasciaOraria) {
        database.delete(fasciaOraria);
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        String normalizedNome = normalize(nome);

        return findAll()
                .stream()
                .map(FasciaOraria::getNome)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(existingNome -> existingNome.equals(normalizedNome));
    }

    public boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id) {
        String normalizedNome = normalize(nome);

        return findAll()
                .stream()
                .filter(fasciaOraria -> fasciaOraria.getId() != null && !fasciaOraria.getId().equals(id))
                .map(FasciaOraria::getNome)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(existingNome -> existingNome.equals(normalizedNome));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}