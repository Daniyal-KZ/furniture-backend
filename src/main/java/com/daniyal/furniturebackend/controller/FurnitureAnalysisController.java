package com.daniyal.furniturebackend.controller;

import com.daniyal.furniturebackend.model.GptRequest;
import com.daniyal.furniturebackend.model.MaterialQuantity;
import com.daniyal.furniturebackend.service.GptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/furniture")
public class FurnitureAnalysisController {

    private final GptService gptService;

    public FurnitureAnalysisController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeFurniture(@RequestBody GptRequest request) {
        try {
            List<MaterialQuantity> materials = gptService.analyzeFurniture(request.getDescription());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("description", request.getDescription());
            response.put("materials", materials);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}