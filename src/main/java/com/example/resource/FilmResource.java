package com.example.resource;

import com.example.dto.FilmRequestDTO;
import com.example.dto.FilmResponseDTO;
import com.example.service.FilmService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.RolesAllowed;

import java.net.URI;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FilmResource {

    private final FilmService filmService;

    @Inject
    public FilmResource(FilmService filmService) {
        this.filmService = filmService;
    }

    @GET
    @Path("/public/films")
    public Response getAllFilms() {
        List<FilmResponseDTO> films = filmService.getAllFilms();
        return Response.ok(films).build();
    }

    @GET
    @Path("/public/films/{id}")
    public Response getFilmById(@PathParam("id") Long id) {
        FilmResponseDTO film = filmService.getFilmById(id);
        return Response.ok(film).build();
    }

    @POST
    @Path("/admin/films")
    @RolesAllowed("ADMIN")
    public Response createFilm(@Valid FilmRequestDTO dto) {
        FilmResponseDTO createdFilm = filmService.createFilm(dto);
        return Response.created(URI.create("/api/admin/films/" + createdFilm.getId()))
                .entity(createdFilm)
                .build();
    }

    @PUT
    @Path("/admin/films/{id}")
    @RolesAllowed("ADMIN")
    public Response updateFilm(@PathParam("id") Long id, @Valid FilmRequestDTO dto) {
        FilmResponseDTO updatedFilm = filmService.updateFilm(id, dto);
        return Response.ok(updatedFilm).build();
    }

    @DELETE
    @Path("/admin/films/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteFilm(@PathParam("id") Long id) {
        filmService.deleteFilm(id);
        return Response.noContent().build();
    }
}