package com.starwars.service;

import com.starwars.model.Person;
import com.starwars.model.SWAPIResponse;
import com.starwars.repository.PeopleRepository;
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
public class PeopleService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PeopleRepository peopleRepository;

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
            String url = SWAPI_URL + "people/";
            ResponseEntity<SWAPIResponse<Person>> peopleResponse;
            do {
                peopleResponse = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<SWAPIResponse<Person>>() {}
                );

                if (peopleResponse.getBody() != null && peopleResponse.getBody().getResults() != null) {
                    List<Person> people = peopleResponse.getBody().getResults();
                    if (people.isEmpty()) {
                        System.out.println("No people found in the SWAPI response.");
                    } else {
                        for (Person person : people) {
                            if (person.getName() != null) {
                                person.setName(person.getName().trim().toLowerCase());
                            }
                        }
                        peopleRepository.saveAll(people);
                        System.out.println(people.size() + " people updated successfully.");
                    }
                } else {
                    System.err.println("Invalid response from SWAPI.");
                }

                url = peopleResponse.getBody().getNext();
            }
            while (url != null);
        } catch (Exception e) {
            System.err.println("Error fetching people: " + e.getMessage());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }


    @PostConstruct
    public void init() {
        if (peopleRepository.count() == 0) {
            System.out.println("Initializing database with SWAPI data...");
            fetchAndStoreData();
        } else {
            System.out.println("Database already contains data. Fetching data from H2 DB");
        }
    }
    public ResponseEntity<Person> getPersonByName(String name) {
        if (name != null) {
            name = name.trim().toLowerCase();
        }
        Optional<Person> person = peopleRepository.findByName(name);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}