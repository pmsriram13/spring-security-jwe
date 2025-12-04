package com.perficient.predictor.batch.controller;


import com.perficient.predictor.batch.dto.CompetitionRequest;
import com.perficient.predictor.batch.entity.Competition;
import com.perficient.predictor.batch.service.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.perficient.predictor.batch.dto.CompetitionResponseDTO;

@RestController
@RequestMapping("/api/v1/competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    /**
     * POST endpoint to create a new Competition.
     * Expects Competition name and Country name in the request body.
     *
     * @param request The CompetitionRequest DTO containing name and country name.
     * @return The created Competition entity with HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<CompetitionResponseDTO> createCompetition(@Valid @RequestBody CompetitionRequest request) {
        try {
            // 1. Service layer returns the full JPA Entity (Competition)
            Competition competitionEntity = competitionService.createCompetition(request);

            // 2. Map the full entity to the slim DTO, explicitly excluding the 'country' field
            CompetitionResponseDTO responseDto = mapToDto(competitionEntity);

            // 3. Return the DTO in the ResponseEntity
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

        } catch (IllegalStateException e) {
            // Handles unique constraint violation on Competition name
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handles missing Country lookup (Country not found)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Log error and return generic internal server error
            System.err.println("Error creating competition: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred during competition creation.");
        }
    }

    /**
     * Maps the full JPA entity to the client-facing DTO.
     * This ensures only required fields are serialized to JSON.
     * * NOTE: For larger projects, use a dedicated mapper (like MapStruct) or a Mapper class.
     */
    private CompetitionResponseDTO mapToDto(Competition entity) {
        return new CompetitionResponseDTO(
                entity.getCompetitionId(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
                // The country property from the entity is correctly ignored here.
        );
    }
}