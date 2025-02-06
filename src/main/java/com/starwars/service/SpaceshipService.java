package com.starwars.service;

import com.starwars.model.SWAPIResponse;
import com.starwars.model.Spaceship;
import com.starwars.repository.SpaceshipRepository;
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
public class SpaceshipService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SpaceshipRepository spaceshipRepository;

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
            String url = SWAPI_URL + "starships/";
            ResponseEntity<SWAPIResponse<Spaceship>> spaceshipResponse;
            do {
                spaceshipResponse = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<SWAPIResponse<Spaceship>>() {}
                );

                if (spaceshipResponse.getBody() != null && spaceshipResponse.getBody().getResults() != null) {
                    List<Spaceship> spaceships = spaceshipResponse.getBody().getResults();
                    if (spaceships.isEmpty()) {
                        System.out.println("No spaceships found in the SWAPI response.");
                    } else {
                        for (Spaceship spaceship : spaceships) {
                            if (spaceship.getName() != null) {
                                spaceship.setName(spaceship.getName().trim().toLowerCase());
                            }
                        }
                        spaceshipRepository.saveAll(spaceships);
                        System.out.println(spaceships.size() + " spaceships updated successfully.");
                    }
                } else {
                    System.err.println("Invalid response from SWAPI.");
                }

                url = spaceshipResponse.getBody().getNext();
            }
            while (url != null);
        } catch (Exception e) {
            System.err.println("Error fetching spaceships: " + e.getMessage());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }

    @PostConstruct
    public void init() {
        if (spaceshipRepository.count() == 0) {
            System.out.println("Initializing database with SWAPI data...");
            fetchAndStoreData();
        } else {
            System.out.println("Database already contains data. Fetching data from H2 DB");
        }
    }
    public ResponseEntity<Spaceship> getSpaceshipByName(String name) {
        if (name != null) {
            name = name.trim().toLowerCase();
        }
            Optional<Spaceship> spaceship = spaceshipRepository.findByName(name);
            return spaceship.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        }
}