package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Person;
import com.starwars.service.PeopleService;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/people")
public class PeopleController {

    private final PeopleService peopleService;

    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

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