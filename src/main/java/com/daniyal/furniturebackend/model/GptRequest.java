package com.daniyal.furniturebackend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GptRequest {
    private String description;

    public GptRequest() {}

    public GptRequest(String description) {
        this.description = description;
    }

}


