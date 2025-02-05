package com.starwars.controller;

import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Species;
import com.starwars.service.SpeciesService;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/species")
public class SpeciesController {

    private final SpeciesService speciesService;

    public SpeciesController(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getSpecies(
            @PathVariable String name) {
        ResponseEntity<?> response = speciesService.getSpeciesByName(name);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return response;
        }
        Species species = (Species) response.getBody();
        GenericStarWarsResponse<Species> speciesResponse = new GenericStarWarsResponse<>();
        speciesResponse.setType("Species");
        speciesResponse.setCount(species.getFilms().size());
        speciesResponse.setName(species.getName());
        speciesResponse.setFilms(species.getFilms().toString());
        speciesResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SpeciesController.class)
                .getSpecies(name)).withSelfRel());
        return ResponseEntity.ok(speciesResponse);
    }
}
