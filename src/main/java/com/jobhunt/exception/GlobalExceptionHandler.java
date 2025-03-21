package com.jobhunt.exception;

import com.jobhunt.payload.FieldViolation;
import com.jobhunt.payload.Response;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle generic exceptions.
     *
     * @param ex the exception
     * @param request the web request
     * @return a standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
    }

    /**
     * Handle EntityExistsException (400 Bad Request).
     *
     * @param e the exception
     * @return a standardized error response
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Response<Void>> handleEntityExistsException(EntityExistsException e, WebRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request.getDescription(false));
    }

    /**
     * Handle EntityNotFoundException (400 Bad Request).
     *
     * @param e the exception
     * @param request the web request
     * @return a standardized error response
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response<Void>> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request.getDescription(false));
    }

    /**
     * Handle RuntimeException (400 Bad Request).
     *
     * @param e the exception
     * @param request the web request
     * @return a standardized error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<Void>> handleRuntimeException(RuntimeException e, WebRequest request) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request.getDescription(false));
    }

    /**
     * Handle validation errors (BindException).
     *
     * @param ex the BindException
     * @param request the web request
     * @return a standardized error response
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleValidationException(BindException ex, WebRequest request) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldErrorToViolation)
                .collect(Collectors.toList());

        Response<Void> response = new Response<>();
        Response.Metadata metadata = new Response.Metadata();
        metadata.setCode("400");
        metadata.setMessage("Validation failed");
        metadata.setErrors(violations);
        response.setMeta(metadata);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Build an error response.
     *
     * @param message the error message
     * @param status  the HTTP status
     * @param path    the request path
     * @return a ResponseEntity containing the error response
     */
    private ResponseEntity<Response<Void>> buildErrorResponse(String message, HttpStatus status, String path) {
        Response<Void> response = new Response<>();
        Response.Metadata metadata = new Response.Metadata();
        metadata.setCode(String.valueOf(status.value()));
        metadata.setMessage(message);
        response.setMeta(metadata);

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Map FieldError to FieldViolation for validation errors.
     *
     * @param fieldError the FieldError
     * @return FieldViolation
     */
    private FieldViolation mapFieldErrorToViolation(FieldError fieldError) {
        return new FieldViolation(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
