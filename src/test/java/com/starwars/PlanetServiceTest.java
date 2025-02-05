package com.starwars;


import com.starwars.model.Planet;
import com.starwars.repository.PlanetRepository;
import com.starwars.service.PlanetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlanetServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PlanetRepository planetRepository;

    @InjectMocks
    private PlanetService planetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: fetchAndStoreData does not interact with repository when offline mode is enabled
    @Test
    void testFetchAndStoreData_offlineMode() {
        // Given: Offline mode enabled
        planetService.offlineModeEnabled = true;

        // When: fetchAndStoreData is called
        planetService.fetchAndStoreData();

        // Then: No interactions with RestTemplate or PlanetRepository
        verifyNoInteractions(restTemplate, planetRepository);
    }

    // Test: getPlanetByName returns planet if found
    @Test
    void testGetPlanetByName_found() {
        // Given: A planet exists in the repository
        Planet planet = new Planet();
        planet.setName("Tatooine");
        when(planetRepository.findByName("tatooine")).thenReturn(Optional.of(planet));

        // When: getPlanetByName is called
        ResponseEntity<Planet> response = planetService.getPlanetByName("tatooine");

        // Then: Return the found planet
        verify(planetRepository).findByName("tatooine");
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() instanceof Planet;
    }

    // Test: getPlanetByName returns 404 if not found
    @Test
    void testGetPlanetByName_notFound() {
        // Given: No planet exists in the repository
        when(planetRepository.findByName("tatooine")).thenReturn(Optional.empty());

        // When: getPlanetByName is called
        ResponseEntity<Planet> response = planetService.getPlanetByName("tatooine");

        // Then: Return a 404 response
        verify(planetRepository).findByName("tatooine");
        assert response.getStatusCodeValue() == 404;
        assert response.getBody() == null;
    }
}
