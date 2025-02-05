package com.starwars.controller;


import com.starwars.dto.GenericStarWarsResponse;
import com.starwars.model.Vehicle;
import com.starwars.service.VehicleService;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/starwars/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/{name}")
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