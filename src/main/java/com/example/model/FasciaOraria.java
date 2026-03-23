package com.example.model;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "fasce_orarie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FasciaOraria extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;
}