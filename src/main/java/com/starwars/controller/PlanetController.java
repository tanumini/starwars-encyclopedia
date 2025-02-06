package com.starwars.controller;

import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Planet;
import com.starwars.service.PlanetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/planets")
@Tag(name = "Planets", description = "Operations related to Star Wars Planets")
public class PlanetController {

    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get planet details by name", description = "Fetches planet details from database")
    @ApiResponse(responseCode = "200", description = "Successfully fetched planet details")
    @ApiResponse(responseCode = "400", description = "Bad request, invalid planet title")
    @ApiResponse(responseCode = "404", description = "Planet not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getPlanet(
            @PathVariable String name) {
        ResponseEntity<?> response = planetService.getPlanetByName(name);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return response;
        }
        Planet planet = (Planet) response.getBody();
        GenericStarWarsResponse<Planet> planetResponse = new GenericStarWarsResponse<>();
        planetResponse.setType("Planet");
        planetResponse.setCount(planet.getFilms().size());
        planetResponse.setName(planet.getName());
        planetResponse.setFilms(planet.getFilms().toString());
        planetResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PlanetController.class)
                .getPlanet(name)).withSelfRel());
        return ResponseEntity.ok(planetResponse);
    }
}