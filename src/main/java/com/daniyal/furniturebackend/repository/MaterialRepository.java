package com.daniyal.furniturebackend.repository;

import com.daniyal.furniturebackend.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}




