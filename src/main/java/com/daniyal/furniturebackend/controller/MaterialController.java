package com.daniyal.furniturebackend.controller;

import com.daniyal.furniturebackend.model.Material;
import com.daniyal.furniturebackend.repository.MaterialRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialRepository materialRepository;

    public MaterialController(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @GetMapping
    public List<Material> getAll() {
        return materialRepository.findAll();
    }

    @PostMapping
    public Material addMaterial(@RequestBody Material material) {
        return materialRepository.save(material);
    }

    @PostMapping("/batch")
    public List<Material> addMaterials(@RequestBody List<Material> materials) {
        return materialRepository.saveAll(materials);
    }


}





