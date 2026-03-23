package com.example.service;

import com.example.dto.SalaRequestDTO;
import com.example.dto.SalaResponseDTO;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.SalaMapper;
import com.example.model.Sala;
import com.example.repository.ProgrammazioneRepository;
import com.example.repository.SalaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SalaService {

    private static final String SALA_ID_PREFIX = "Sala con id ";
    private static final String NOT_FOUND_SUFFIX = " non trovata";

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final ProgrammazioneRepository programmazioneRepository;

    @Inject
    public SalaService(SalaRepository salaRepository,
                       SalaMapper salaMapper,
                       ProgrammazioneRepository programmazioneRepository) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.programmazioneRepository = programmazioneRepository;
    }

    public List<SalaResponseDTO> getAllSale() {
        return salaRepository.findAll()
                .stream()
                .map(salaMapper::toResponseDTO)
                .toList();
    }

    public SalaResponseDTO getSalaById(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SALA_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        return salaMapper.toResponseDTO(sala);
    }

    public SalaResponseDTO createSala(SalaRequestDTO dto) {
        if (salaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ConflictException("Esiste già una sala con nome '" + dto.getNome() + "'");
        }

        Sala sala = salaMapper.toEntity(dto);
        Sala savedSala = salaRepository.save(sala);
        return salaMapper.toResponseDTO(savedSala);
    }

    public SalaResponseDTO updateSala(Long id, SalaRequestDTO dto) {
        Sala existingSala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SALA_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        if (salaRepository.existsByNomeIgnoreCaseAndIdNot(dto.getNome(), id)) {
            throw new ConflictException("Esiste già una sala con nome '" + dto.getNome() + "'");
        }

        existingSala.setNome(dto.getNome());
        existingSala.setCapienza(dto.getCapienza());

        Sala updatedSala = salaRepository.save(existingSala);
        return salaMapper.toResponseDTO(updatedSala);
    }

    public void deleteSala(Long id) {
        Sala existingSala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SALA_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        if (programmazioneRepository.existsBySalaId(id)) {
            throw new ConflictException("Impossibile eliminare la sala perché è presente in una programmazione");
        }

        salaRepository.delete(existingSala);
    }
}