package com.starwars;

import com.starwars.model.Film;
import com.starwars.repository.FilmRepository;
import com.starwars.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilmServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: fetchAndStoreData does not interact with repository when offline mode is enabled
    @Test
    void testFetchAndStoreData_offlineMode() {
        // Given: Offline mode enabled
        filmService.offlineModeEnabled = true;

        // When: fetchAndStoreData is called
        filmService.fetchAndStoreData();

        // Then: No interactions with RestTemplate or FilmRepository
        verifyNoInteractions(restTemplate, filmRepository);
    }

    // Test: getFilmByTitle returns film if found
    @Test
    void testGetFilmByTitle_found() {
        // Given: A film exists in the repository
        Film film = new Film();
        film.setTitle("Star Wars");
        when(filmRepository.findDistinctByTitle("star wars")).thenReturn(Arrays.asList(film));

        // When: getFilmByTitle is called
        ResponseEntity<?> response = filmService.getFilmByTitle("star wars");

        // Then: Return the found film
        verify(filmRepository).findDistinctByTitle("star wars");
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() instanceof Film;
    }

    // Test: getFilmByTitle returns 404 if not found
    @Test
    void testGetFilmByTitle_notFound() {
        // Given: No film exists in the repository
        when(filmRepository.findDistinctByTitle("star wars")).thenReturn(Arrays.asList());

        // When: getFilmByTitle is called
        ResponseEntity<?> response = filmService.getFilmByTitle("star wars");

        // Then: Return a 404 response
        verify(filmRepository).findDistinctByTitle("star wars");
        assert response.getStatusCodeValue() == 404;
        assert response.getBody().equals("Film not found for title: star wars");
    }
}
