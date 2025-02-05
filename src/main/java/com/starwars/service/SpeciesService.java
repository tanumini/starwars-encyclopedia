package com.starwars.service;


import com.starwars.model.Planet;
import com.starwars.model.SWAPIResponse;
import com.starwars.model.Species;
import com.starwars.repository.SpeciesRepository;
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
public class SpeciesService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Value("${offline.mode.enabled}")
    public boolean offlineModeEnabled;

    @Value("${swapi.url}")
    private String SWAPI_URL;
    @Async
    @Scheduled(fixedRate = 86400000) // Runs every 24 hours
    public CompletableFuture<Void> fetchAndStoreData() {
        if (offlineModeEnabled) {
            System.out.println("Offline mode enabled. Skipping species data fetch.");
            return CompletableFuture.completedFuture(null);
        }

        try {
            String url = SWAPI_URL + "species/";
            ResponseEntity<SWAPIResponse<Species>> speciesResponse;
            do {
                speciesResponse = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<SWAPIResponse<Species>>() {
                        }
                );

                if (speciesResponse.getBody() != null && speciesResponse.getBody().getResults() != null) {
                    List<Species> species = speciesResponse.getBody().getResults();
                    if (species.isEmpty()) {
                        System.out.println("No species found in the SWAPI response.");
                    } else {
                        for (Species species1 : species) {
                            if (species1.getName() != null) {
                                species1.setName(species1.getName().trim().toLowerCase());
                            }
                        }
                        speciesRepository.saveAll(species);
                        System.out.println(species.size() + " species updated successfully.");
                    }
                } else {
                    System.err.println("Invalid response from SWAPI.");
                }
                url = speciesResponse.getBody().getNext();
            }
            while (url != null);
        } catch (Exception e) {
            System.err.println("Error fetching species: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

//    @Scheduled(fixedRate = 86400000) // Runs every 24 hours
//    public void fetchAndStoreData() {
//        if (offlineModeEnabled) return; // Skip API calls if offline mode is enabled
//        try {
//            String  url = SWAPI_URL + "species/";
//            ResponseEntity<SWAPIResponse<Species>>  speciesResponse;
//            do {
//                speciesResponse = restTemplate.exchange(
//                        url, HttpMethod.GET, null,
//                        new ParameterizedTypeReference<SWAPIResponse<Species>>() {
//                        }
//                );
//                if (speciesResponse.getBody() != null && speciesResponse.getBody().getResults() != null) {
//                    List<Species> species = speciesResponse.getBody().getResults();
//                    if (species.isEmpty()) {
//                        System.out.println("No species found in the SWAPI response.");
//
//                    } else {
//                        for (Species species1 : species) {
//                            if (species1.getName() != null) {
//                                species1.setName(species1.getName().trim().toLowerCase());
//                            }
//                        }
//                        speciesRepository.saveAll(species);
//                        System.out.println(species.size() + " species updated successfully.");
//                    }
//                } else {
//                    // Return a 400 bad request if no valid response is received
//                    System.err.println("Invalid response from SWAPI.");
//                }
//                url = speciesResponse.getBody().getNext();
//            }
//            while( url != null);
//        } catch (Exception e) {
//            System.err.println(" Error fetching species: " + e.getMessage());
//        }
//    }
    @PostConstruct
    public void init() {
        if (speciesRepository.count() == 0) {  // Fetch only if DB is empty
            System.out.println("Initializing database with SWAPI data...");
            fetchAndStoreData();
        } else {
            System.out.println("Database already contains data. Fetching data from H2 DB");
        }
    }
    public ResponseEntity<Species> getSpeciesByName(String name) {
        if (name != null) {
            name = name.trim().toLowerCase();
        }
            Optional<Species> species = speciesRepository.findByName(name);
            return species.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}