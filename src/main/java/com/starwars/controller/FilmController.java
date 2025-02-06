package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Film;
import com.starwars.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/films")
@Tag(name = "Films", description = "Operations related to Star Wars Films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }
    @Operation(summary = "Get a film by title", description = "Fetches film details from database")
    @ApiResponse(responseCode = "200", description = "Successfully fetched film details")
    @ApiResponse(responseCode = "400", description = "Bad request, invalid film title")
    @ApiResponse(responseCode = "404", description = "Film not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")

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