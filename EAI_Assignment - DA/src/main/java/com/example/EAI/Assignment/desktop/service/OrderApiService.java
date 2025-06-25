package com.example.EAI.Assignment.desktop.service;

import com.example.EAI.Assignment.desktop.model.order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrderApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OrderApiService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/api")
                .build();
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Mono<List<order>> getAllOrders() {
        return webClient.get()
                .uri("/orders")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, new TypeReference<List<order>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse orders", e);
                    }
                });
    }

    public Mono<order> getOrderById(String orderId) {
        return webClient.get()
                .uri("/orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, order.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse order", e);
                    }
                });
    }

    public Mono<order> updateOrderStatus(String orderId, order.OrderStatus newStatus) {
        return webClient.put()
                .uri("/orders/{orderId}/status?status={status}", orderId, newStatus)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, order.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse updated order", e);
                    }
                });
    }

    public Mono<Void> deleteOrder(String orderId) {
        return webClient.delete()
                .uri("/orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(Void.class);
    }
} 