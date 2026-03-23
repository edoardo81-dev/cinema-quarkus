package com.example.service;

import com.example.dto.PrenotazioneEventDTO;
import com.example.dto.PrenotazioneRequestDTO;
import com.example.dto.PrenotazioneResponseDTO;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.PrenotazioneMapper;
import com.example.model.Prenotazione;
import com.example.model.Programmazione;
import com.example.model.StatoPrenotazione;
import com.example.repository.PrenotazioneRepository;
import com.example.repository.ProgrammazioneRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
public class PrenotazioneService {

    private static final String PRENOTAZIONE_ID_PREFIX = "Prenotazione con id ";
    private static final String PROGRAMMAZIONE_ID_PREFIX = "Programmazione con id ";
    private static final String NOT_FOUND_SUFFIX = " non trovata";

    private final PrenotazioneRepository prenotazioneRepository;
    private final ProgrammazioneRepository programmazioneRepository;
    private final PrenotazioneMapper prenotazioneMapper;
    private final PrenotazioneEventBroadcaster prenotazioneEventBroadcaster;
    private final PrenotazioneLockManager prenotazioneLockManager;

    @Inject
    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository,
            ProgrammazioneRepository programmazioneRepository,
            PrenotazioneMapper prenotazioneMapper,
            PrenotazioneEventBroadcaster prenotazioneEventBroadcaster,
            PrenotazioneLockManager prenotazioneLockManager) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.programmazioneRepository = programmazioneRepository;
        this.prenotazioneMapper = prenotazioneMapper;
        this.prenotazioneEventBroadcaster = prenotazioneEventBroadcaster;
        this.prenotazioneLockManager = prenotazioneLockManager;
    }

    public List<PrenotazioneResponseDTO> getAllPrenotazioni() {
        return prenotazioneRepository.findAll()
                .stream()
                .map(this::toPrenotazioneResponseDTO)
                .toList();
    }

    public PrenotazioneResponseDTO getPrenotazioneById(Long id) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRENOTAZIONE_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        return toPrenotazioneResponseDTO(prenotazione);
    }

    public List<PrenotazioneResponseDTO> getPrenotazioniByProgrammazioneId(Long programmazioneId) {
        programmazioneRepository.findById(programmazioneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PROGRAMMAZIONE_ID_PREFIX + programmazioneId + NOT_FOUND_SUFFIX));

        return prenotazioneRepository.findByProgrammazioneId(programmazioneId)
                .stream()
                .map(this::toPrenotazioneResponseDTO)
                .toList();
    }

    public PrenotazioneResponseDTO createPrenotazione(PrenotazioneRequestDTO dto) {
        ReentrantLock lock = prenotazioneLockManager.getLock(dto.getProgrammazioneId());
        lock.lock();

        try {
            Programmazione programmazione = programmazioneRepository.findById(dto.getProgrammazioneId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            PROGRAMMAZIONE_ID_PREFIX + dto.getProgrammazioneId() + NOT_FOUND_SUFFIX));

            validateDataSpettacolo(programmazione, dto);

            int postiOccupati = prenotazioneRepository.sumPostiAttiviByProgrammazioneId(dto.getProgrammazioneId());
            int postiDisponibili = programmazione.getSala().getCapienza() - postiOccupati;

            if (dto.getNumeroPosti() > postiDisponibili) {
                throw new ConflictException("Posti insufficienti per la programmazione selezionata");
            }

            Prenotazione prenotazione = new Prenotazione();
            prenotazione.setNomeCliente(dto.getNomeCliente());
            prenotazione.setEmailCliente(dto.getEmailCliente());
            prenotazione.setNumeroPosti(dto.getNumeroPosti());
            prenotazione.setDataSpettacolo(dto.getDataSpettacolo());
            prenotazione.setCreatedAt(LocalDateTime.now());
            prenotazione.setCodicePrenotazione(generateCodicePrenotazione());
            prenotazione.setStato(StatoPrenotazione.ATTIVA);
            prenotazione.setProgrammazione(programmazione);

            Prenotazione savedPrenotazione = prenotazioneRepository.save(prenotazione);

            int postiDisponibiliResidui = postiDisponibili - dto.getNumeroPosti();

            prenotazioneEventBroadcaster.broadcast(
                    new PrenotazioneEventDTO(
                            "PRENOTAZIONE_CREATA",
                            savedPrenotazione.getId(),
                            programmazione.getId(),
                            postiDisponibiliResidui));

            BigDecimal totalePrezzo = calculateTotalePrezzo(savedPrenotazione);
            return prenotazioneMapper.toResponseDTO(savedPrenotazione, totalePrezzo);
        } finally {
            lock.unlock();
        }
    }

    public PrenotazioneResponseDTO annullaPrenotazione(Long id) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRENOTAZIONE_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        prenotazione.setStato(StatoPrenotazione.ANNULLATA);
        Prenotazione updatedPrenotazione = prenotazioneRepository.save(prenotazione);

        int postiOccupati = prenotazioneRepository
                .sumPostiAttiviByProgrammazioneId(prenotazione.getProgrammazione().getId());
        int postiDisponibiliResidui = prenotazione.getProgrammazione().getSala().getCapienza() - postiOccupati;

        prenotazioneEventBroadcaster.broadcast(
                new PrenotazioneEventDTO(
                        "PRENOTAZIONE_ANNULLATA",
                        updatedPrenotazione.getId(),
                        prenotazione.getProgrammazione().getId(),
                        postiDisponibiliResidui));

        BigDecimal totalePrezzo = calculateTotalePrezzo(updatedPrenotazione);
        return prenotazioneMapper.toResponseDTO(updatedPrenotazione, totalePrezzo);
    }

    public void deletePrenotazione(Long id) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRENOTAZIONE_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        Long programmazioneId = prenotazione.getProgrammazione().getId();
        Integer capienzaSala = prenotazione.getProgrammazione().getSala().getCapienza();

        prenotazioneRepository.delete(prenotazione);

        int postiOccupati = prenotazioneRepository.sumPostiAttiviByProgrammazioneId(programmazioneId);
        int postiDisponibiliResidui = capienzaSala - postiOccupati;

        prenotazioneEventBroadcaster.broadcast(
                new PrenotazioneEventDTO(
                        "PRENOTAZIONE_ELIMINATA",
                        id,
                        programmazioneId,
                        postiDisponibiliResidui));
    }

    private String generateCodicePrenotazione() {
        return "PRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculateTotalePrezzo(Prenotazione prenotazione) {
        BigDecimal prezzoUnitario = prenotazione.getProgrammazione().getFasciaOraria().getPrezzo();
        return prezzoUnitario.multiply(BigDecimal.valueOf(prenotazione.getNumeroPosti()));
    }

    private PrenotazioneResponseDTO toPrenotazioneResponseDTO(Prenotazione prenotazione) {
        BigDecimal totalePrezzo = calculateTotalePrezzo(prenotazione);
        return prenotazioneMapper.toResponseDTO(prenotazione, totalePrezzo);
    }

    private void validateDataSpettacolo(Programmazione programmazione, PrenotazioneRequestDTO dto) {
        if (dto.getDataSpettacolo().isBefore(programmazione.getDataInizioProgrammazione())
                || dto.getDataSpettacolo().isAfter(programmazione.getDataFineProgrammazione())) {
            throw new ConflictException(
                    "La data dello spettacolo non rientra nel periodo della programmazione selezionata");
        }
    }
}