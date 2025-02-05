package com.starwars.controller;

import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Planet;
import com.starwars.service.PlanetService;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/planets")

public class PlanetController {

    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }
    @GetMapping("/{name}")
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