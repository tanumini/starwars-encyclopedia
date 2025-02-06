package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Spaceship;
import com.starwars.service.SpaceshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/spaceships")
@Tag(name = "Spaceships", description = "Operations related to Star Wars Spaceships")
public class SpaceshipController {

    private final SpaceshipService spaceshipService;

    public SpaceshipController(SpaceshipService spaceshipService) {
        this.spaceshipService = spaceshipService;
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get spaceship details by name", description = "Fetches spaceship details from database")
    @ApiResponse(responseCode = "200", description = "Successfully fetched spaceship details")
    @ApiResponse(responseCode = "400", description = "Bad request, invalid spaceship title")
    @ApiResponse(responseCode = "404", description = "Spaceship not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getSpaceship(
            @PathVariable String name) {
        ResponseEntity<?> response = spaceshipService.getSpaceshipByName(name);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return response;
        }
        Spaceship spaceship = (Spaceship) response.getBody();
        GenericStarWarsResponse<Spaceship> spaceshipResponse = new GenericStarWarsResponse<>();
        spaceshipResponse.setType("Spaceship");
        spaceshipResponse.setCount(spaceship.getFilms().size());
        spaceshipResponse.setName(spaceship.getName());
        spaceshipResponse.setFilms(spaceship.getFilms().toString());
        spaceshipResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SpaceshipController.class)
                .getSpaceship(name)).withSelfRel());
        return ResponseEntity.ok(spaceshipResponse);
    }
}