package com.example.resource;

import com.example.dto.PrenotazioneRequestDTO;
import com.example.dto.PrenotazioneResponseDTO;
import com.example.service.PrenotazioneService;

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
public class PrenotazioneResource {

    private final PrenotazioneService prenotazioneService;

    @Inject
    public PrenotazioneResource(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @GET
    @Path("/admin/prenotazioni")
    @RolesAllowed("ADMIN")
    public Response getAllPrenotazioni() {
        List<PrenotazioneResponseDTO> prenotazioni = prenotazioneService.getAllPrenotazioni();
        return Response.ok(prenotazioni).build();
    }

    @GET
    @Path("/admin/prenotazioni/{id}")
    @RolesAllowed("ADMIN")
    public Response getPrenotazioneById(@PathParam("id") Long id) {
        PrenotazioneResponseDTO prenotazione = prenotazioneService.getPrenotazioneById(id);
        return Response.ok(prenotazione).build();
    }

    @GET
    @Path("/admin/prenotazioni/programmazione/{programmazioneId}")
    @RolesAllowed("ADMIN")
    public Response getPrenotazioniByProgrammazioneId(@PathParam("programmazioneId") Long programmazioneId) {
        List<PrenotazioneResponseDTO> prenotazioni = prenotazioneService.getPrenotazioniByProgrammazioneId(programmazioneId);
        return Response.ok(prenotazioni).build();
    }

    @POST
    @Path("/public/prenotazioni")
    public Response createPrenotazione(@Valid PrenotazioneRequestDTO dto) {
        PrenotazioneResponseDTO createdPrenotazione = prenotazioneService.createPrenotazione(dto);
        return Response.created(URI.create("/api/public/prenotazioni/" + createdPrenotazione.getId()))
                .entity(createdPrenotazione)
                .build();
    }

    @PUT
    @Path("/admin/prenotazioni/{id}/annulla")
    @RolesAllowed("ADMIN")
    public Response annullaPrenotazione(@PathParam("id") Long id) {
        PrenotazioneResponseDTO updatedPrenotazione = prenotazioneService.annullaPrenotazione(id);
        return Response.ok(updatedPrenotazione).build();
    }

    @DELETE
    @Path("/admin/prenotazioni/{id}")
    @RolesAllowed("ADMIN")
    public Response deletePrenotazione(@PathParam("id") Long id) {
        prenotazioneService.deletePrenotazione(id);
        return Response.noContent().build();
    }
}