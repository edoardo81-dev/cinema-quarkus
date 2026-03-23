package com.example.mapper;

import com.example.dto.SalaRequestDTO;
import com.example.dto.SalaResponseDTO;
import com.example.model.Sala;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SalaMapper {

    public Sala toEntity(SalaRequestDTO dto) {
        Sala sala = new Sala();
        sala.setNome(dto.getNome());
        sala.setCapienza(dto.getCapienza());
        return sala;
    }

    public SalaResponseDTO toResponseDTO(Sala sala) {
        return new SalaResponseDTO(
                sala.getId(),
                sala.getNome(),
                sala.getCapienza()
        );
    }
}