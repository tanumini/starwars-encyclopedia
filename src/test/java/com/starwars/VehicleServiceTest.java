package com.starwars;


import com.starwars.model.Vehicle;
import com.starwars.repository.VehicleRepository;
import com.starwars.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: fetchAndStoreData does not interact with repository when offline mode is enabled
    @Test
    void testFetchAndStoreData_offlineMode() {
        // Given: Offline mode enabled
        vehicleService.offlineModeEnabled = true;

        // When: fetchAndStoreData is called
        vehicleService.fetchAndStoreData();

        // Then: No interactions with VehicleRepository
        verifyNoInteractions(vehicleRepository);
    }

    // Test: getVehicleByName returns vehicle if found
    @Test
    void testGetVehicleByName_found() {
        // Given: A vehicle exists in the repository
        Vehicle vehicle = new Vehicle();
        vehicle.setName("Speeder Bike");
        when(vehicleRepository.findByName("speeder bike")).thenReturn(Optional.of(vehicle));

        // When: getVehicleByName is called
        ResponseEntity<Vehicle> response = vehicleService.getVehicleByName("speeder bike");

        // Then: Return the found vehicle
        verify(vehicleRepository).findByName("speeder bike");
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() instanceof Vehicle;
    }

    // Test: getVehicleByName returns 404 if not found
    @Test
    void testGetVehicleByName_notFound() {
        // Given: No vehicle exists in the repository
        when(vehicleRepository.findByName("speeder bike")).thenReturn(Optional.empty());

        // When: getVehicleByName is called
        ResponseEntity<Vehicle> response = vehicleService.getVehicleByName("speeder bike");

        // Then: Return a 404 response
        verify(vehicleRepository).findByName("speeder bike");
        assert response.getStatusCodeValue() == 404;
        assert response.getBody() == null;
    }
}
