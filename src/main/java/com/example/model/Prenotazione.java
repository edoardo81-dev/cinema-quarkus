package com.example.model;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prenotazioni")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prenotazione extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nome_cliente", nullable = false, length = 100)
    private String nomeCliente;

    @NotNull
    @Column(name = "email_cliente", nullable = false, length = 150)
    private String emailCliente;

    @NotNull
    @Column(name = "numero_posti", nullable = false)
    private Integer numeroPosti;

    @NotNull
    @Column(name = "data_spettacolo", nullable = false)
    private LocalDate dataSpettacolo;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "codice_prenotazione", nullable = false, unique = true, length = 50)
    private String codicePrenotazione;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoPrenotazione stato;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "programmazione_id", nullable = false)
    private Programmazione programmazione;
}