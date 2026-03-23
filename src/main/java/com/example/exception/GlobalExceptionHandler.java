package com.example.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof ResourceNotFoundException ex) {
            ApiError apiError = new ApiError(
                    Response.Status.NOT_FOUND.getStatusCode(),
                    "Not Found",
                    ex.getMessage(),
                    uriInfo.getPath()
            );
            return Response.status(Response.Status.NOT_FOUND).entity(apiError).build();
        }

        if (exception instanceof ConflictException ex) {
            ApiError apiError = new ApiError(
                    Response.Status.CONFLICT.getStatusCode(),
                    "Conflict",
                    ex.getMessage(),
                    uriInfo.getPath()
            );
            return Response.status(Response.Status.CONFLICT).entity(apiError).build();
        }

        if (exception instanceof ConstraintViolationException ex) {
            String message = ex.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            ApiError apiError = new ApiError(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    "Bad Request",
                    message,
                    uriInfo.getPath()
            );

            return Response.status(Response.Status.BAD_REQUEST).entity(apiError).build();
        }

        ApiError apiError = new ApiError(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Internal Server Error",
                exception.getMessage(),
                uriInfo.getPath()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
    }
}