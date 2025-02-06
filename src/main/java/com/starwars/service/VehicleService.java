package com.starwars.service;

import com.starwars.model.SWAPIResponse;
import com.starwars.model.Vehicle;
import com.starwars.repository.VehicleRepository;
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
public class VehicleService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Value("${offline.mode.enabled}")
    public boolean offlineModeEnabled;

    @Value("${swapi.url}")
    private String SWAPI_URL;

    @Async
    @Scheduled(fixedRate = 86400000) // Runs every 24 hours
    public CompletableFuture<Void> fetchAndStoreData() {
        if (offlineModeEnabled) {
            System.out.println("Offline mode enabled. Skipping vehicle data fetch.");
            return CompletableFuture.completedFuture(null);
        }

        try {
            String url = SWAPI_URL + "vehicles/";
            ResponseEntity<SWAPIResponse<Vehicle>> vehicleResponse;
            do {
                vehicleResponse = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<SWAPIResponse<Vehicle>>() {
                        }
                );

                if (vehicleResponse.getBody() != null && vehicleResponse.getBody().getResults() != null) {
                    List<Vehicle> vehicles = vehicleResponse.getBody().getResults();
                    if (vehicles.isEmpty()) {
                        System.out.println("No vehicles found in the SWAPI response.");
                    } else {
                        for (Vehicle vehicle : vehicles) {
                            if (vehicle.getName() != null) {
                                vehicle.setName(vehicle.getName().trim().toLowerCase());
                            }
                        }
                        vehicleRepository.saveAll(vehicles);
                        System.out.println(vehicles.size() + " vehicles updated successfully.");
                    }
                } else {
                    System.err.println("Invalid response from SWAPI.");
                }
                url = vehicleResponse.getBody().getNext();
            }
            while (url != null);
        } catch (Exception e) {
            System.err.println("Error fetching vehicles: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    @PostConstruct
    public void init() {
        if (vehicleRepository.count() == 0) {
            System.out.println("Initializing database with SWAPI data...");
            fetchAndStoreData();
        } else {
            System.out.println("Database already contains data. Fetching data from H2 DB");
        }
    }
    public ResponseEntity<Vehicle> getVehicleByName(String name) {
        if (name != null) {
            name = name.trim().toLowerCase();
        }
        Optional<Vehicle> vehicle = vehicleRepository.findByName(name);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}