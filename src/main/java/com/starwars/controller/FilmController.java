package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Film;
import com.starwars.service.FilmService;
import org.hibernate.annotations.Parameter;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getFilm(
            @PathVariable String name) {
        ResponseEntity<?> response = filmService.getFilmByTitle(name);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return response;
        }
        Film film = (Film) response.getBody();
        GenericStarWarsResponse<Film> filmResponse = new GenericStarWarsResponse<>();
        filmResponse.setType("Film");
        filmResponse.setCount(1);
        filmResponse.setName(film.getTitle());
        filmResponse.setFilms(film.getTitle());
        filmResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FilmController.class)
                .getFilm(name)).withSelfRel());
        return ResponseEntity.ok(filmResponse);
    }
}