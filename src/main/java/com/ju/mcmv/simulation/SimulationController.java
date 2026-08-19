package com.ju.mcmv.simulation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ju.mcmv.simulation.DTO.SimulationRequest;
import com.ju.mcmv.simulation.DTO.SimulationResponse;

@RestController
@RequestMapping("/simulations")
@CrossOrigin

public class SimulationController {

    private final SimulationService service;

    public SimulationController(SimulationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SimulationResponse> save(@RequestBody SimulationRequest request) {

        SimulationResponse saved = service.save(request);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
public ResponseEntity<List<SimulationResponse>> getAll() {
    return ResponseEntity.ok(service.findAll());
}
}
