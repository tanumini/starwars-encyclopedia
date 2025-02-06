package com.starwars.service;

import com.starwars.model.Planet;
import com.starwars.model.SWAPIResponse;
import com.starwars.repository.PlanetRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PlanetService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlanetRepository planetRepository;

    @Value("${offline.mode.enabled}")
    public boolean offlineModeEnabled;

    @Value("${swapi.url}")
    private String SWAPI_URL;

    @Async
    @Scheduled(fixedRate = 86400000)
    public CompletableFuture<Void> fetchAndStoreData() {
        if (offlineModeEnabled) return CompletableFuture.completedFuture(null);

        try {
            String url = SWAPI_URL + "planets/";
            ResponseEntity<SWAPIResponse<Planet>> planetResponse;
            do {
                planetResponse = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<SWAPIResponse<Planet>>() {
                        }
                );
                SWAPIResponse<Planet> body = planetResponse.getBody();
                if (body != null && body.getResults() != null) {
                    List<Planet> planets = body.getResults();
                    if (planets.isEmpty()) {
                        System.out.println("No planets found in the SWAPI response.");
                    } else {
                        for (Planet planet : planets) {
                            if (planet.getName() != null) {
                                planet.setName(planet.getName().trim().toLowerCase());
                            }
                        }
                        planetRepository.saveAll(planets);
                        System.out.println(planets.size() + " planets updated successfully.");
                    }
                } else {
                    System.err.println("Invalid response from SWAPI.");
                }
                url = body.getNext();
            }
            while (url != null);
        } catch (Exception e) {
            System.err.println("Error fetching planets: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    @PostConstruct
    public void init() {
        if (planetRepository.count() == 0) {
            System.out.println("Initializing database with SWAPI data...");
            fetchAndStoreData();
        } else {
            System.out.println("Database already contains data. Fetching data from H2 DB");
        }
    }
    public ResponseEntity<Planet> getPlanetByName(String name) {
        if (name != null) {
            name = name.trim().toLowerCase();
        }
        Optional<Planet> planet = planetRepository.findByName(name);
        return planet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }


}
