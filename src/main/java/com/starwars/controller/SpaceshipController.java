package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Spaceship;
import com.starwars.service.SpaceshipService;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/spaceships")
public class SpaceshipController {

    private final SpaceshipService spaceshipService;

    public SpaceshipController(SpaceshipService spaceshipService) {
        this.spaceshipService = spaceshipService;
    }

    @GetMapping("/{name}")
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