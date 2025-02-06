package com.starwars.service;

import com.starwars.model.Film;
import com.starwars.model.SWAPIResponse;
import com.starwars.repository.FilmRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class FilmService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FilmRepository filmRepository;

    @Value("${offline.mode.enabled}")
    public boolean offlineModeEnabled;

    @Value("${swapi.url}")
    private String SWAPI_URL;


    @Async
    @Scheduled(fixedRate = 86400000)
    public CompletableFuture<Void> fetchAndStoreData() {
        if (offlineModeEnabled) {
            System.out.println("Offline mode enabled. Fetching from H2 database.");
            return CompletableFuture.completedFuture(null);
        }

        try {
            String url = SWAPI_URL + "films/";
            ResponseEntity<SWAPIResponse<Film>> filmResponse;
            do {

                filmResponse = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<SWAPIResponse<Film>>() {}
                );

                if (filmResponse.getBody() != null && filmResponse.getBody().getResults() != null) {
                    List<Film> films = filmResponse.getBody().getResults();
                    if (films.isEmpty()) {
                        System.out.println("No films found in the SWAPI response.");
                    } else {
                        for (Film film : films) {
                            if (film.getTitle() != null) {
                                film.setTitle(film.getTitle().trim().toLowerCase());
                            }
                        }
                        filmRepository.saveAll(films);
                        System.out.println(films.size() + " films updated successfully.");
                    }
                } else {
                    System.err.println("Invalid response from SWAPI.");
                }

                url = filmResponse.getBody().getNext();
            }
            while (url != null);
        } catch (Exception e) {
            System.err.println("Error fetching films: " + e.getMessage());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }


    @PostConstruct
    public void init() {
        if (filmRepository.count() == 0) {
            System.out.println("Initializing database with SWAPI data...");
            fetchAndStoreData();
        } else {
            System.out.println("Database already contains data. Fetching data from H2 DB");
        }
    }

    public ResponseEntity<?> getFilmByTitle(String name) {
        if (name != null) {
            name = name.trim().toLowerCase();
        }
        List<Film> films = filmRepository.findDistinctByTitle(name);
        if (films.isEmpty()) {
            return ResponseEntity.status(404).body("Film not found for title: " + name);
        }

        if (films.size() == 1) {
            return ResponseEntity.ok(films.get(0));
        }
        return ResponseEntity.ok(films);
    }
}
