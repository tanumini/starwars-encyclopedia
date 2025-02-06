package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Vehicle;
import com.starwars.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/vehicles")
@Tag(name = "Vehicles", description = "Operations related to Star Wars Vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get vehicle details by name", description = "Fetches vehicle details from database")
    @ApiResponse(responseCode = "200", description = "Successfully fetched vehicle details")
    @ApiResponse(responseCode = "400", description = "Bad request, invalid vehicle title")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getVehicle(
            @PathVariable String name) {
        ResponseEntity<?> response = vehicleService.getVehicleByName(name);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return response;
        }
        Vehicle vehicle = (Vehicle) response.getBody();
        GenericStarWarsResponse<Vehicle> vehicleResponse = new GenericStarWarsResponse<>();
        vehicleResponse.setType("Vehicle");
        vehicleResponse.setCount(vehicle.getFilms().size());
        vehicleResponse.setName(vehicle.getName());
        vehicleResponse.setFilms(vehicle.getFilms().toString());
        vehicleResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class)
                .getVehicle(name)).withSelfRel());
        return ResponseEntity.ok(vehicleResponse);
    }
}