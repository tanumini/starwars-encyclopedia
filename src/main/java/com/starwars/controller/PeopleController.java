package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Person;
import com.starwars.service.PeopleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/people")
@Tag(name = "People", description = "Operations related to Star Wars People")
public class PeopleController {

    private final PeopleService peopleService;

    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }
    @Operation(summary = "Get person details by name", description = "Fetches people details from database")
    @ApiResponse(responseCode = "200", description = "Successfully fetched people details")
    @ApiResponse(responseCode = "400", description = "Bad request, invalid people title")
    @ApiResponse(responseCode = "404", description = "People not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{name}")
    public ResponseEntity<?> getPerson(
            @PathVariable String name) {
        ResponseEntity<?> response = peopleService.getPersonByName(name);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return response;
        }
        Person person = (Person) response.getBody();
        GenericStarWarsResponse<Person> personResponse = new GenericStarWarsResponse<>();
        personResponse.setType("Person");
        personResponse.setCount(person.getFilms().size());
        personResponse.setName(person.getName());
        personResponse.setFilms(person.getFilms().toString());
        personResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PeopleController.class)
                .getPerson(name)).withSelfRel());
        return ResponseEntity.ok(personResponse);
    }
}