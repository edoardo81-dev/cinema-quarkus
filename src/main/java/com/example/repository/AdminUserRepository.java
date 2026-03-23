package com.example.repository;

import com.example.model.AdminUser;
import io.ebean.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AdminUserRepository {

    private final Database database;

    @Inject
    public AdminUserRepository(Database database) {
        this.database = database;
    }

    public Optional<AdminUser> findByUsername(String username) {
        AdminUser adminUser = database.find(AdminUser.class)
                .where()
                .eq("username", username)
                .findOne();

        return Optional.ofNullable(adminUser);
    }

    public AdminUser save(AdminUser adminUser) {
        database.save(adminUser);
        return adminUser;
    }

    public long count() {
        return database.find(AdminUser.class).findCount();
    }
}