package com.daniyal.furniturebackend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaterialQuantity {
    private String name;
    private Double quantity;

    public MaterialQuantity() {}

    public MaterialQuantity(String name, Double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

}


