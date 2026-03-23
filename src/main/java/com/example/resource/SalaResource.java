package com.example.resource;

import com.example.dto.SalaRequestDTO;
import com.example.dto.SalaResponseDTO;
import com.example.service.SalaService;

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
public class SalaResource {

    private final SalaService salaService;

    @Inject
    public SalaResource(SalaService salaService) {
        this.salaService = salaService;
    }

    @GET
    @Path("/public/sale")
    public Response getAllSale() {
        List<SalaResponseDTO> sale = salaService.getAllSale();
        return Response.ok(sale).build();
    }

    @GET
    @Path("/public/sale/{id}")
    public Response getSalaById(@PathParam("id") Long id) {
        SalaResponseDTO sala = salaService.getSalaById(id);
        return Response.ok(sala).build();
    }

    @POST
    @Path("/admin/sale")
    @RolesAllowed("ADMIN")
    public Response createSala(@Valid SalaRequestDTO dto) {
        SalaResponseDTO createdSala = salaService.createSala(dto);
        return Response.created(URI.create("/api/admin/sale/" + createdSala.getId()))
                .entity(createdSala)
                .build();
    }

    @PUT
    @Path("/admin/sale/{id}")
    @RolesAllowed("ADMIN")
    public Response updateSala(@PathParam("id") Long id, @Valid SalaRequestDTO dto) {
        SalaResponseDTO updatedSala = salaService.updateSala(id, dto);
        return Response.ok(updatedSala).build();
    }

    @DELETE
    @Path("/admin/sale/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteSala(@PathParam("id") Long id) {
        salaService.deleteSala(id);
        return Response.noContent().build();
    }
}