package com.starwars;

import com.starwars.model.Species;
import com.starwars.repository.SpeciesRepository;
import com.starwars.service.SpeciesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpeciesServiceTest {

    @Mock
    private SpeciesRepository speciesRepository;

    @InjectMocks
    private SpeciesService speciesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: fetchAndStoreData does not interact with repository when offline mode is enabled
    @Test
    void testFetchAndStoreData_offlineMode() {
        // Given: Offline mode enabled
        speciesService.offlineModeEnabled = true;

        // When: fetchAndStoreData is called
        speciesService.fetchAndStoreData();

        // Then: No interactions with SpeciesRepository
        verifyNoInteractions(speciesRepository);
    }

    // Test: getSpeciesByName returns species if found
    @Test
    void testGetSpeciesByName_found() {
        // Given: A species exists in the repository
        Species species = new Species();
        species.setName("Wookiee");
        when(speciesRepository.findByName("wookiee")).thenReturn(Optional.of(species));

        // When: getSpeciesByName is called
        ResponseEntity<Species> response = speciesService.getSpeciesByName("wookiee");

        // Then: Return the found species
        verify(speciesRepository).findByName("wookiee");
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() instanceof Species;
    }

    // Test: getSpeciesByName returns 404 if not found
    @Test
    void testGetSpeciesByName_notFound() {
        // Given: No species exists in the repository
        when(speciesRepository.findByName("wookiee")).thenReturn(Optional.empty());

        // When: getSpeciesByName is called
        ResponseEntity<Species> response = speciesService.getSpeciesByName("wookiee");

        // Then: Return a 404 response
        verify(speciesRepository).findByName("wookiee");
        assert response.getStatusCodeValue() == 404;
        assert response.getBody() == null;
    }
}
