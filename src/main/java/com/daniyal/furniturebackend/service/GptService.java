package com.daniyal.furniturebackend.service;

import com.daniyal.furniturebackend.model.Material;
import com.daniyal.furniturebackend.model.MaterialQuantity;
import com.daniyal.furniturebackend.repository.MaterialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GptService {

    private final MaterialRepository materialRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    public GptService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<MaterialQuantity> analyzeFurniture(String userDescription) {
        try {

            List<Material> materials = materialRepository.findAll();


            String prompt = buildPrompt(userDescription, materials);

            String response = callGpt(prompt);

            return parseResponse(response);

        } catch (Exception e) {
            throw new RuntimeException("GPT Error: " + e.getMessage());
        }
    }

    private String buildPrompt(String userRequest, List<Material> materials) {
        String materialsList = materials.stream()
                .map(m -> String.format("- %s (%s, %s)", m.getName(), m.getType(), m.getUnit()))
                .collect(Collectors.joining("\n"));

        return String.format("""
       
        ,ТУТ ПРОМПТ
       
        """, userRequest, materialsList);
    }

    private String callGpt(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            var headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            body.put("temperature", 0.1);
            body.put("response_format", Map.of("type", "json_object"));
            var entity = new org.springframework.http.HttpEntity<>(body, headers);

            var response = restTemplate.postForObject(
                    "https://api.openai.com/v1/chat/completions",
                    entity,
                    Map.class
            );

            // Извлекаем текст ответа
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            throw new RuntimeException("GPT API call failed: " + e.getMessage());
        }
    }

    private List<MaterialQuantity> parseResponse(String gptResponse) {
        try {
            String json = gptResponse.replace("```json", "").replace("```", "").trim();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(json, Map.class);

            List<Map<String, Object>> materials = (List<Map<String, Object>>) data.get("materials");

            return materials.stream()
                    .map(item -> new MaterialQuantity(
                            (String) item.get("name"),
                            ((Number) item.get("quantity")).doubleValue()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPT response: " + e.getMessage());
        }
    }
}


