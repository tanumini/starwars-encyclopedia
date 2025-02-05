package com.starwars;


import com.starwars.model.Person;
import com.starwars.repository.PeopleRepository;
import com.starwars.service.PeopleService;
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

class PeopleServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PeopleRepository peopleRepository;

    @InjectMocks
    private PeopleService peopleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: fetchAndStoreData does not interact with repository when offline mode is enabled
    @Test
    void testFetchAndStoreData_offlineMode() {
        // Given: Offline mode enabled
        peopleService.offlineModeEnabled = true;

        // When: fetchAndStoreData is called
        peopleService.fetchAndStoreData();

        // Then: No interactions with RestTemplate or PeopleRepository
        verifyNoInteractions(restTemplate, peopleRepository);
    }

    // Test: getPersonByName returns person if found
    @Test
    void testGetPersonByName_found() {
        // Given: A person exists in the repository
        Person person = new Person();
        person.setName("Luke Skywalker");
        when(peopleRepository.findByName("luke skywalker")).thenReturn(Optional.of(person));

        // When: getPersonByName is called
        ResponseEntity<Person> response = peopleService.getPersonByName("luke skywalker");

        // Then: Return the found person
        verify(peopleRepository).findByName("luke skywalker");
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() instanceof Person;
    }

    // Test: getPersonByName returns 404 if not found
    @Test
    void testGetPersonByName_notFound() {
        // Given: No person exists in the repository
        when(peopleRepository.findByName("luke skywalker")).thenReturn(Optional.empty());

        // When: getPersonByName is called
        ResponseEntity<Person> response = peopleService.getPersonByName("luke skywalker");

        // Then: Return a 404 response
        verify(peopleRepository).findByName("luke skywalker");
        assert response.getStatusCodeValue() == 404;
        assert response.getBody() == null;
    }
}
