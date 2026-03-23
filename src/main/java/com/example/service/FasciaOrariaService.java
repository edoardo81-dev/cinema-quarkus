package com.example.service;

import com.example.dto.FasciaOrariaRequestDTO;
import com.example.dto.FasciaOrariaResponseDTO;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.FasciaOrariaMapper;
import com.example.model.FasciaOraria;
import com.example.repository.FasciaOrariaRepository;
import com.example.repository.ProgrammazioneRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FasciaOrariaService {

    private static final String FASCIA_ORARIA_ID_PREFIX = "Fascia oraria con id ";
    private static final String NOT_FOUND_SUFFIX = " non trovata";

    private final FasciaOrariaRepository fasciaOrariaRepository;
    private final FasciaOrariaMapper fasciaOrariaMapper;
    private final ProgrammazioneRepository programmazioneRepository;

    @Inject
    public FasciaOrariaService(FasciaOrariaRepository fasciaOrariaRepository,
                               FasciaOrariaMapper fasciaOrariaMapper,
                               ProgrammazioneRepository programmazioneRepository) {
        this.fasciaOrariaRepository = fasciaOrariaRepository;
        this.fasciaOrariaMapper = fasciaOrariaMapper;
        this.programmazioneRepository = programmazioneRepository;
    }

    public List<FasciaOrariaResponseDTO> getAllFasceOrarie() {
        return fasciaOrariaRepository.findAll()
                .stream()
                .map(fasciaOrariaMapper::toResponseDTO)
                .toList();
    }

    public FasciaOrariaResponseDTO getFasciaOrariaById(Long id) {
        FasciaOraria fasciaOraria = fasciaOrariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FASCIA_ORARIA_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        return fasciaOrariaMapper.toResponseDTO(fasciaOraria);
    }

    public FasciaOrariaResponseDTO createFasciaOraria(FasciaOrariaRequestDTO dto) {
        if (fasciaOrariaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ConflictException("Esiste già una fascia oraria con nome '" + dto.getNome() + "'");
        }

        FasciaOraria fasciaOraria = fasciaOrariaMapper.toEntity(dto);
        FasciaOraria savedFasciaOraria = fasciaOrariaRepository.save(fasciaOraria);
        return fasciaOrariaMapper.toResponseDTO(savedFasciaOraria);
    }

    public FasciaOrariaResponseDTO updateFasciaOraria(Long id, FasciaOrariaRequestDTO dto) {
        FasciaOraria existingFasciaOraria = fasciaOrariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FASCIA_ORARIA_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        if (fasciaOrariaRepository.existsByNomeIgnoreCaseAndIdNot(dto.getNome(), id)) {
            throw new ConflictException("Esiste già una fascia oraria con nome '" + dto.getNome() + "'");
        }

        existingFasciaOraria.setNome(dto.getNome());
        existingFasciaOraria.setPrezzo(dto.getPrezzo());

        FasciaOraria updatedFasciaOraria = fasciaOrariaRepository.save(existingFasciaOraria);
        return fasciaOrariaMapper.toResponseDTO(updatedFasciaOraria);
    }

    public void deleteFasciaOraria(Long id) {
        FasciaOraria existingFasciaOraria = fasciaOrariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FASCIA_ORARIA_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        if (programmazioneRepository.existsByFasciaOrariaId(id)) {
            throw new ConflictException("Impossibile eliminare la fascia oraria perché è presente in una programmazione");
        }

        fasciaOrariaRepository.delete(existingFasciaOraria);
    }
}