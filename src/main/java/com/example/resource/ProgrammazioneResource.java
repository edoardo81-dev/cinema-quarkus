package com.example.resource;

import com.example.dto.ProgrammazioneRequestDTO;
import com.example.dto.ProgrammazioneResponseDTO;
import com.example.service.ProgrammazioneService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProgrammazioneResource {

    private final ProgrammazioneService programmazioneService;

    @Inject
    public ProgrammazioneResource(ProgrammazioneService programmazioneService) {
        this.programmazioneService = programmazioneService;
    }

    @GET
    @Path("/public/programmazioni")
    public Response getAllProgrammazioni() {
        List<ProgrammazioneResponseDTO> programmazioni = programmazioneService.getAllProgrammazioni();
        return Response.ok(programmazioni).build();
    }

    @GET
    @Path("/public/programmazioni/{id}")
    public Response getProgrammazioneById(@PathParam("id") Long id) {
        ProgrammazioneResponseDTO programmazione = programmazioneService.getProgrammazioneById(id);
        return Response.ok(programmazione).build();
    }

    @GET
    @Path("/public/programmazioni/sala/{salaId}")
    public Response getProgrammazioniBySalaId(@PathParam("salaId") Long salaId) {
        List<ProgrammazioneResponseDTO> programmazioni = programmazioneService.getProgrammazioniBySalaId(salaId);
        return Response.ok(programmazioni).build();
    }

    @GET
    @Path("/public/programmazioni/film/{filmId}")
    public Response getProgrammazioniByFilmId(@PathParam("filmId") Long filmId) {
        List<ProgrammazioneResponseDTO> programmazioni = programmazioneService.getProgrammazioniByFilmId(filmId);
        return Response.ok(programmazioni).build();
    }

    @GET
    @Path("/public/programmazioni/fascia-oraria/{fasciaOrariaId}")
    public Response getProgrammazioniByFasciaOrariaId(@PathParam("fasciaOrariaId") Long fasciaOrariaId) {
        List<ProgrammazioneResponseDTO> programmazioni = programmazioneService.getProgrammazioniByFasciaOrariaId(fasciaOrariaId);
        return Response.ok(programmazioni).build();
    }

    @POST
    @Path("/admin/programmazioni")
    @RolesAllowed("ADMIN")
    public Response createProgrammazione(@Valid ProgrammazioneRequestDTO dto) {
        ProgrammazioneResponseDTO createdProgrammazione = programmazioneService.createProgrammazione(dto);
        return Response.created(URI.create("/api/admin/programmazioni/" + createdProgrammazione.getId()))
                .entity(createdProgrammazione)
                .build();
    }

    @PUT
    @Path("/admin/programmazioni/{id}")
    @RolesAllowed("ADMIN")
    public Response updateProgrammazione(@PathParam("id") Long id, @Valid ProgrammazioneRequestDTO dto) {
        ProgrammazioneResponseDTO updatedProgrammazione = programmazioneService.updateProgrammazione(id, dto);
        return Response.ok(updatedProgrammazione).build();
    }

    @DELETE
    @Path("/admin/programmazioni/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteProgrammazione(@PathParam("id") Long id) {
        programmazioneService.deleteProgrammazione(id);
        return Response.noContent().build();
    }
}