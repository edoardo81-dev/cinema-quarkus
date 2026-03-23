package com.example.resource;

import com.example.dto.FasciaOrariaRequestDTO;
import com.example.dto.FasciaOrariaResponseDTO;
import com.example.service.FasciaOrariaService;

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
public class FasciaOrariaResource {

    private final FasciaOrariaService fasciaOrariaService;

    @Inject
    public FasciaOrariaResource(FasciaOrariaService fasciaOrariaService) {
        this.fasciaOrariaService = fasciaOrariaService;
    }

    @GET
    @Path("/public/fasce-orarie")
    public Response getAllFasceOrarie() {
        List<FasciaOrariaResponseDTO> fasceOrarie = fasciaOrariaService.getAllFasceOrarie();
        return Response.ok(fasceOrarie).build();
    }

    @GET
    @Path("/public/fasce-orarie/{id}")
    public Response getFasciaOrariaById(@PathParam("id") Long id) {
        FasciaOrariaResponseDTO fasciaOraria = fasciaOrariaService.getFasciaOrariaById(id);
        return Response.ok(fasciaOraria).build();
    }

    @POST
    @Path("/admin/fasce-orarie")
    @RolesAllowed("ADMIN")
    public Response createFasciaOraria(@Valid FasciaOrariaRequestDTO dto) {
        FasciaOrariaResponseDTO createdFasciaOraria = fasciaOrariaService.createFasciaOraria(dto);
        return Response.created(URI.create("/api/admin/fasce-orarie/" + createdFasciaOraria.getId()))
                .entity(createdFasciaOraria)
                .build();
    }

    @PUT
    @Path("/admin/fasce-orarie/{id}")
    @RolesAllowed("ADMIN")
    public Response updateFasciaOraria(@PathParam("id") Long id, @Valid FasciaOrariaRequestDTO dto) {
        FasciaOrariaResponseDTO updatedFasciaOraria = fasciaOrariaService.updateFasciaOraria(id, dto);
        return Response.ok(updatedFasciaOraria).build();
    }

    @DELETE
    @Path("/admin/fasce-orarie/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteFasciaOraria(@PathParam("id") Long id) {
        fasciaOrariaService.deleteFasciaOraria(id);
        return Response.noContent().build();
    }
}