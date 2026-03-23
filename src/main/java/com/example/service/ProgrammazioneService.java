package com.example.service;

import com.example.dto.ProgrammazioneRequestDTO;
import com.example.dto.ProgrammazioneResponseDTO;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.ProgrammazioneMapper;
import com.example.model.FasciaOraria;
import com.example.model.Film;
import com.example.model.Programmazione;
import com.example.model.Sala;
import com.example.repository.FasciaOrariaRepository;
import com.example.repository.FilmRepository;
import com.example.repository.PrenotazioneRepository;
import com.example.repository.ProgrammazioneRepository;
import com.example.repository.SalaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class ProgrammazioneService {

    private static final String PROGRAMMAZIONE_ID_PREFIX = "Programmazione con id ";
    private static final String FILM_ID_PREFIX = "Film con id ";
    private static final String SALA_ID_PREFIX = "Sala con id ";
    private static final String FASCIA_ORARIA_ID_PREFIX = "Fascia oraria con id ";
    private static final String NOT_FOUND_SUFFIX = " non trovata";
    private static final String FILM_NOT_FOUND_SUFFIX = " non trovato";

    private static final int INTERVALLO_TECNICO_MINUTI = 10;
    private static final int TEMPO_RICAMBIO_SALA_MINUTI = 15;
    private static final int TEMPO_EXTRA_SALA_MINUTI =
            INTERVALLO_TECNICO_MINUTI + TEMPO_RICAMBIO_SALA_MINUTI;

    private final ProgrammazioneRepository programmazioneRepository;
    private final FilmRepository filmRepository;
    private final SalaRepository salaRepository;
    private final FasciaOrariaRepository fasciaOrariaRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final ProgrammazioneMapper programmazioneMapper;

    @Inject
public ProgrammazioneService(ProgrammazioneRepository programmazioneRepository,
                             FilmRepository filmRepository,
                             SalaRepository salaRepository,
                             FasciaOrariaRepository fasciaOrariaRepository,
                             PrenotazioneRepository prenotazioneRepository,
                             ProgrammazioneMapper programmazioneMapper) {
    this.programmazioneRepository = programmazioneRepository;
    this.filmRepository = filmRepository;
    this.salaRepository = salaRepository;
    this.fasciaOrariaRepository = fasciaOrariaRepository;
    this.prenotazioneRepository = prenotazioneRepository;
    this.programmazioneMapper = programmazioneMapper;
}

    public List<ProgrammazioneResponseDTO> getAllProgrammazioni() {
        return programmazioneRepository.findAll()
                .stream()
                .map(this::toProgrammazioneResponseDTO)
                .toList();
    }

    public ProgrammazioneResponseDTO getProgrammazioneById(Long id) {
        Programmazione programmazione = programmazioneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROGRAMMAZIONE_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        return toProgrammazioneResponseDTO(programmazione);
    }

    public List<ProgrammazioneResponseDTO> getProgrammazioniBySalaId(Long salaId) {
        salaRepository.findById(salaId)
                .orElseThrow(() -> new ResourceNotFoundException(SALA_ID_PREFIX + salaId + NOT_FOUND_SUFFIX));

        return programmazioneRepository.findBySalaId(salaId)
                .stream()
                .map(this::toProgrammazioneResponseDTO)
                .toList();
    }

    public List<ProgrammazioneResponseDTO> getProgrammazioniByFilmId(Long filmId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException(FILM_ID_PREFIX + filmId + FILM_NOT_FOUND_SUFFIX));

        return programmazioneRepository.findByFilmId(filmId)
                .stream()
                .map(this::toProgrammazioneResponseDTO)
                .toList();
    }

    public List<ProgrammazioneResponseDTO> getProgrammazioniByFasciaOrariaId(Long fasciaOrariaId) {
        fasciaOrariaRepository.findById(fasciaOrariaId)
                .orElseThrow(() -> new ResourceNotFoundException(FASCIA_ORARIA_ID_PREFIX + fasciaOrariaId + NOT_FOUND_SUFFIX));

        return programmazioneRepository.findByFasciaOrariaId(fasciaOrariaId)
                .stream()
                .map(this::toProgrammazioneResponseDTO)
                .toList();
    }

    public ProgrammazioneResponseDTO createProgrammazione(ProgrammazioneRequestDTO dto) {
        validateDateRange(dto);

        Film film = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException(FILM_ID_PREFIX + dto.getFilmId() + FILM_NOT_FOUND_SUFFIX));

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException(SALA_ID_PREFIX + dto.getSalaId() + NOT_FOUND_SUFFIX));

        FasciaOraria fasciaOraria = fasciaOrariaRepository.findById(dto.getFasciaOrariaId())
                .orElseThrow(() -> new ResourceNotFoundException(FASCIA_ORARIA_ID_PREFIX + dto.getFasciaOrariaId() + NOT_FOUND_SUFFIX));

        if (programmazioneRepository.existsBySalaAndFasciaOraria(dto.getSalaId(), dto.getFasciaOrariaId())) {
            throw new ConflictException("Esiste già una programmazione per la sala selezionata nella fascia oraria selezionata");
        }

        if (programmazioneRepository.existsByFilmAndFasciaOraria(dto.getFilmId(), dto.getFasciaOrariaId())) {
            throw new ConflictException("Esiste già una programmazione per il film selezionato nella fascia oraria selezionata");
        }

        LocalTime nuovoOrarioFine = calculateOrarioFine(dto.getOrarioInizio(), film.getDurataMinuti());

        if (programmazioneRepository.existsOverlappingProgrammazioneInSala(
                dto.getSalaId(),
                dto.getOrarioInizio(),
                nuovoOrarioFine
        )) {
            throw new ConflictException("Esiste già una programmazione sovrapposta nella sala selezionata");
        }

        Programmazione programmazione = programmazioneMapper.toEntity(
                film,
                sala,
                fasciaOraria,
                dto.getOrarioInizio(),
                dto.getDataInizioProgrammazione(),
                dto.getDataFineProgrammazione()
        );

        Programmazione savedProgrammazione = programmazioneRepository.save(programmazione);
        return toProgrammazioneResponseDTO(savedProgrammazione);
    }

    public ProgrammazioneResponseDTO updateProgrammazione(Long id, ProgrammazioneRequestDTO dto) {
        validateDateRange(dto);

        Programmazione existingProgrammazione = programmazioneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROGRAMMAZIONE_ID_PREFIX + id + NOT_FOUND_SUFFIX));

        Film film = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException(FILM_ID_PREFIX + dto.getFilmId() + FILM_NOT_FOUND_SUFFIX));

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException(SALA_ID_PREFIX + dto.getSalaId() + NOT_FOUND_SUFFIX));

        FasciaOraria fasciaOraria = fasciaOrariaRepository.findById(dto.getFasciaOrariaId())
                .orElseThrow(() -> new ResourceNotFoundException(FASCIA_ORARIA_ID_PREFIX + dto.getFasciaOrariaId() + NOT_FOUND_SUFFIX));

        if (programmazioneRepository.existsBySalaAndFasciaOrariaAndIdNot(dto.getSalaId(), dto.getFasciaOrariaId(), id)) {
            throw new ConflictException("Esiste già una programmazione per la sala selezionata nella fascia oraria selezionata");
        }

        if (programmazioneRepository.existsByFilmAndFasciaOrariaAndIdNot(dto.getFilmId(), dto.getFasciaOrariaId(), id)) {
            throw new ConflictException("Esiste già una programmazione per il film selezionato nella fascia oraria selezionata");
        }

        LocalTime nuovoOrarioFine = calculateOrarioFine(dto.getOrarioInizio(), film.getDurataMinuti());

        if (programmazioneRepository.existsOverlappingProgrammazioneInSalaAndIdNot(
                dto.getSalaId(),
                dto.getOrarioInizio(),
                nuovoOrarioFine,
                id
        )) {
            throw new ConflictException("Esiste già una programmazione sovrapposta nella sala selezionata");
        }

        existingProgrammazione.setFilm(film);
        existingProgrammazione.setSala(sala);
        existingProgrammazione.setFasciaOraria(fasciaOraria);
        existingProgrammazione.setOrarioInizio(dto.getOrarioInizio());
        existingProgrammazione.setDataInizioProgrammazione(dto.getDataInizioProgrammazione());
        existingProgrammazione.setDataFineProgrammazione(dto.getDataFineProgrammazione());

        Programmazione updatedProgrammazione = programmazioneRepository.save(existingProgrammazione);
        return toProgrammazioneResponseDTO(updatedProgrammazione);
    }

    public void deleteProgrammazione(Long id) {
    Programmazione existingProgrammazione = programmazioneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(PROGRAMMAZIONE_ID_PREFIX + id + NOT_FOUND_SUFFIX));

    if (prenotazioneRepository.existsByProgrammazioneId(id)) {
        throw new ConflictException("Impossibile eliminare la programmazione perché è presente almeno una prenotazione");
    }

    programmazioneRepository.delete(existingProgrammazione);
}

    private void validateDateRange(ProgrammazioneRequestDTO dto) {
        if (dto.getDataFineProgrammazione().isBefore(dto.getDataInizioProgrammazione())) {
            throw new ConflictException("La data di fine programmazione non può essere precedente alla data di inizio programmazione");
        }
    }

    private LocalTime calculateOrarioFine(LocalTime orarioInizio, Integer durataMinuti) {
        return orarioInizio
                .plusMinutes(durataMinuti)
                .plusMinutes(TEMPO_EXTRA_SALA_MINUTI);
    }

    private ProgrammazioneResponseDTO toProgrammazioneResponseDTO(Programmazione programmazione) {
        int postiOccupati = prenotazioneRepository.sumPostiAttiviByProgrammazioneId(programmazione.getId());
        int postiDisponibili = programmazione.getSala().getCapienza() - postiOccupati;
        return programmazioneMapper.toResponseDTO(programmazione, postiDisponibili);
    }
}