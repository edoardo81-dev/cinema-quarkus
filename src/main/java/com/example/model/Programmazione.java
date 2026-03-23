package com.example.model;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
    name = "programmazioni",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sala_fascia", columnNames = {"sala_id", "fascia_oraria_id"}),
        @UniqueConstraint(name = "uk_film_fascia", columnNames = {"film_id", "fascia_oraria_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Programmazione extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "fascia_oraria_id", nullable = false)
    private FasciaOraria fasciaOraria;

    @NotNull
    @Column(name = "orario_inizio", nullable = false)
    private LocalTime orarioInizio;

    @NotNull
    @Column(name = "data_inizio_programmazione", nullable = false)
    private LocalDate dataInizioProgrammazione;

    @NotNull
    @Column(name = "data_fine_programmazione", nullable = false)
    private LocalDate dataFineProgrammazione;
}