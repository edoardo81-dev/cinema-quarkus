package com.example.config;

import com.example.model.AdminUser;
import com.example.model.FasciaOraria;
import com.example.model.Film;
import com.example.model.Programmazione;
import com.example.model.Sala;
import com.example.repository.AdminUserRepository;
import io.ebean.Database;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@ApplicationScoped
public class DataInitializer {

    private static final String GENERE_THRILLER = "Thriller";
    private static final String GENERE_COMMEDIA = "Commedia";
    private static final String GENERE_DRAMMATICO = "Drammatico";
    private static final String GENERE_FANTASCIENZA = "Fantascienza";
    private static final String GENERE_HORROR = "Horror";

    private final Database database;
    private final AdminUserRepository adminUserRepository;

    @Inject
    public DataInitializer(Database database, AdminUserRepository adminUserRepository) {
        this.database = database;
        this.adminUserRepository = adminUserRepository;
    }

    public void onStart(@Observes StartupEvent event) {
        initSale();
        initFasceOrarie();
        initFilm();
        initAdminUser();
        initProgrammazioni();
    }

    private void initSale() {
        if (database.find(Sala.class).findCount() == 0) {
            Sala sala1 = new Sala();
            sala1.setNome("Sala 1");
            sala1.setCapienza(120);
            database.save(sala1);

            Sala sala2 = new Sala();
            sala2.setNome("Sala 2");
            sala2.setCapienza(100);
            database.save(sala2);

            Sala sala3 = new Sala();
            sala3.setNome("Sala 3");
            sala3.setCapienza(80);
            database.save(sala3);
        }
    }

    private void initFasceOrarie() {
        if (database.find(FasciaOraria.class).findCount() == 0) {
            FasciaOraria pomeriggio = new FasciaOraria();
            pomeriggio.setNome("POMERIGGIO");
            pomeriggio.setPrezzo(new BigDecimal("6.50"));
            database.save(pomeriggio);

            FasciaOraria sera = new FasciaOraria();
            sera.setNome("SERA");
            sera.setPrezzo(new BigDecimal("8.00"));
            database.save(sera);

            FasciaOraria notte = new FasciaOraria();
            notte.setNome("NOTTE");
            notte.setPrezzo(new BigDecimal("9.50"));
            database.save(notte);
        }
    }

    private void initFilm() {
        if (database.find(Film.class).findCount() == 0) {
            saveFilm("Lo Squalo", GENERE_THRILLER, 1975, 124, "https://via.placeholder.com/300x450?text=Lo+Squalo");
            saveFilm("The Blues Brothers", GENERE_COMMEDIA, 1980, 133, "https://via.placeholder.com/300x450?text=The+Blues+Brothers");
            saveFilm("Il Padrino", GENERE_DRAMMATICO, 1972, 175, "https://via.placeholder.com/300x450?text=Il+Padrino");
            saveFilm("2001 Odissea nello spazio", GENERE_FANTASCIENZA, 1968, 149, "https://via.placeholder.com/300x450?text=2001+Odissea+nello+spazio");
            saveFilm("Arancia meccanica", GENERE_DRAMMATICO, 1971, 136, "https://via.placeholder.com/300x450?text=Arancia+meccanica");
            saveFilm("Febbre da cavallo", GENERE_COMMEDIA, 1976, 100, "https://via.placeholder.com/300x450?text=Febbre+da+cavallo");
            saveFilm("L'Evocazione-The Conjuring", GENERE_HORROR, 2013, 112, "https://via.placeholder.com/300x450?text=The+conjuring");
            saveFilm("The Shining", GENERE_HORROR, 1980, 146, "https://via.placeholder.com/300x450?text=The+Shining");
            saveFilm("Scarface", GENERE_DRAMMATICO, 1983, 170, "https://via.placeholder.com/300x450?text=Scarface");
        }
    }

    private void initAdminUser() {
        if (adminUserRepository.count() == 0) {
            AdminUser adminUser = new AdminUser();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin123");
            adminUser.setRole("ADMIN");
            adminUserRepository.save(adminUser);
        }
    }

    private void initProgrammazioni() {
        if (database.find(Programmazione.class).findCount() > 0) {
            return;
        }

        LocalDate dataInizio = LocalDate.of(2026, 3, 19);
        LocalDate dataFine = LocalDate.of(2026, 3, 31);

        Sala sala1 = findSalaByNome("Sala 1");
        Sala sala2 = findSalaByNome("Sala 2");
        Sala sala3 = findSalaByNome("Sala 3");

        FasciaOraria pomeriggio = findFasciaByNome("POMERIGGIO");
        FasciaOraria sera = findFasciaByNome("SERA");
        FasciaOraria notte = findFasciaByNome("NOTTE");

        LocalTime oraPomeriggio = LocalTime.of(16, 0);
        LocalTime oraSera = LocalTime.of(19, 30);
        LocalTime oraNotte = LocalTime.of(22, 30);

        // POMERIGGIO
        saveProgrammazione(findFilmByTitolo("Lo Squalo"), sala1, pomeriggio, oraPomeriggio, dataInizio, dataFine);
        saveProgrammazione(findFilmByTitolo("The Blues Brothers"), sala2, pomeriggio, oraPomeriggio, dataInizio, dataFine);
        saveProgrammazione(findFilmByTitolo("Febbre da cavallo"), sala3, pomeriggio, oraPomeriggio, dataInizio, dataFine);

        // SERA
        saveProgrammazione(findFilmByTitolo("Il Padrino"), sala1, sera, oraSera, dataInizio, dataFine);
        saveProgrammazione(findFilmByTitolo("Arancia meccanica"), sala2, sera, oraSera, dataInizio, dataFine);
        saveProgrammazione(findFilmByTitolo("L'Evocazione-The Conjuring"), sala3, sera, oraSera, dataInizio, dataFine);

        // NOTTE
        saveProgrammazione(findFilmByTitolo("2001 Odissea nello spazio"), sala1, notte, oraNotte, dataInizio, dataFine);
        saveProgrammazione(findFilmByTitolo("The Shining"), sala2, notte, oraNotte, dataInizio, dataFine);
        saveProgrammazione(findFilmByTitolo("Scarface"), sala3, notte, oraNotte, dataInizio, dataFine);
    }

    private void saveFilm(String titolo, String genere, Integer anno, Integer durataMinuti, String imageUrl) {
        Film film = new Film();
        film.setTitolo(titolo);
        film.setGenere(genere);
        film.setAnno(anno);
        film.setDurataMinuti(durataMinuti);
        film.setImageUrl(imageUrl);
        database.save(film);
    }

    private void saveProgrammazione(Film film,
                                    Sala sala,
                                    FasciaOraria fasciaOraria,
                                    LocalTime orarioInizio,
                                    LocalDate dataInizioProgrammazione,
                                    LocalDate dataFineProgrammazione) {
        Programmazione programmazione = new Programmazione();
        programmazione.setFilm(film);
        programmazione.setSala(sala);
        programmazione.setFasciaOraria(fasciaOraria);
        programmazione.setOrarioInizio(orarioInizio);
        programmazione.setDataInizioProgrammazione(dataInizioProgrammazione);
        programmazione.setDataFineProgrammazione(dataFineProgrammazione);
        database.save(programmazione);
    }

    private Sala findSalaByNome(String nome) {
        return database.find(Sala.class)
                .where()
                .eq("nome", nome)
                .findOne();
    }

    private FasciaOraria findFasciaByNome(String nome) {
        return database.find(FasciaOraria.class)
                .where()
                .eq("nome", nome)
                .findOne();
    }

    private Film findFilmByTitolo(String titolo) {
        return database.find(Film.class)
                .where()
                .eq("titolo", titolo)
                .findOne();
    }
}