package com.example.config;

import com.example.model.FasciaOraria;
import com.example.model.Film;
import com.example.model.Prenotazione;
import com.example.model.Programmazione;
import com.example.model.Sala;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import com.example.model.AdminUser;

import javax.sql.DataSource;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class EbeanConfig {

    private final DataSource dataSource;

    @Inject
    public EbeanConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Produces
    @ApplicationScoped
    public Database database() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("db");
        config.setDataSource(dataSource);
        config.setDefaultServer(false);
        config.setRegister(false);

        config.addClass(Film.class);
        config.addClass(Sala.class);
        config.addClass(FasciaOraria.class);
        config.addClass(Programmazione.class);
        config.addClass(Prenotazione.class);
        config.addClass(AdminUser.class);

        String activeProfile = ConfigProvider.getConfig()
                .getOptionalValue("quarkus.profile", String.class)
                .orElse("dev");

        boolean isProd = "prod".equals(activeProfile);

        config.setDdlGenerate(!isProd);
        config.setDdlRun(!isProd);

        return DatabaseFactory.create(config);
    }
}