//package com.starwars.service;
//
//import com.starwars.model.Planet;
//import com.starwars.model.SWAPIResponse;
//import com.starwars.repository.PlanetRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//@Service
//public class SWAPIService {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private PlanetRepository planetRepository;  // For storing data in H2
//
//    private static final String SWAPI_URL = "https://swapi.dev/api/"; // Base URL for SWAPI
//
//    public void fetchAndStoreData() {
//        // Fetch and store Planets
//        ResponseEntity<SWAPIResponse> planetResponse = restTemplate.exchange(
//                SWAPI_URL + "planets/", HttpMethod.GET, null, SWAPIResponse.class);
//
//
//        List<Planet> planets = planetResponse.getBody().getResults();
//        planetRepository.saveAll(planets); // Saving to H2 database
//    }
//}
