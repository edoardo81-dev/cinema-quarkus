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

@Entity
@Table(name = "films")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Film extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, length = 150)
    private String titolo;

    @NotNull
    @Column(nullable = false, length = 100)
    private String genere;

    @NotNull
    @Column(nullable = false)
    private Integer anno;

    @NotNull
    @Column(name = "durata_minuti", nullable = false)
    private Integer durataMinuti;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}