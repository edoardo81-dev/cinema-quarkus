package com.example.mapper;

import com.example.dto.FasciaOrariaRequestDTO;
import com.example.dto.FasciaOrariaResponseDTO;
import com.example.model.FasciaOraria;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FasciaOrariaMapper {

    public FasciaOraria toEntity(FasciaOrariaRequestDTO dto) {
        FasciaOraria fasciaOraria = new FasciaOraria();
        fasciaOraria.setNome(dto.getNome());
        fasciaOraria.setPrezzo(dto.getPrezzo());
        return fasciaOraria;
    }

    public FasciaOrariaResponseDTO toResponseDTO(FasciaOraria fasciaOraria) {
        return new FasciaOrariaResponseDTO(
                fasciaOraria.getId(),
                fasciaOraria.getNome(),
                fasciaOraria.getPrezzo()
        );
    }
}