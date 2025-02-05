package com.starwars;


import com.starwars.model.Spaceship;
import com.starwars.repository.SpaceshipRepository;
import com.starwars.service.SpaceshipService;
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

class SpaceshipServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SpaceshipRepository spaceshipRepository;

    @InjectMocks
    private SpaceshipService spaceshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: fetchAndStoreData does not interact with repository when offline mode is enabled
    @Test
    void testFetchAndStoreData_offlineMode() {
        // Given: Offline mode enabled
        spaceshipService.offlineModeEnabled = true;

        // When: fetchAndStoreData is called
        spaceshipService.fetchAndStoreData();

        // Then: No interactions with RestTemplate or SpaceshipRepository
        verifyNoInteractions(restTemplate, spaceshipRepository);
    }

    // Test: getSpaceshipByName returns spaceship if found
    @Test
    void testGetSpaceshipByName_found() {
        // Given: A spaceship exists in the repository
        Spaceship spaceship = new Spaceship();
        spaceship.setName("Millennium Falcon");
        when(spaceshipRepository.findByName("millennium falcon")).thenReturn(Optional.of(spaceship));

        // When: getSpaceshipByName is called
        ResponseEntity<Spaceship> response = spaceshipService.getSpaceshipByName("millennium falcon");

        // Then: Return the found spaceship
        verify(spaceshipRepository).findByName("millennium falcon");
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() instanceof Spaceship;
    }

    // Test: getSpaceshipByName returns 404 if not found
    @Test
    void testGetSpaceshipByName_notFound() {
        // Given: No spaceship exists in the repository
        when(spaceshipRepository.findByName("millennium falcon")).thenReturn(Optional.empty());

        // When: getSpaceshipByName is called
        ResponseEntity<Spaceship> response = spaceshipService.getSpaceshipByName("millennium falcon");

        // Then: Return a 404 response
        verify(spaceshipRepository).findByName("millennium falcon");
        assert response.getStatusCodeValue() == 404;
        assert response.getBody() == null;
    }
}
