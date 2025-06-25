package com.example.EAI.Assignment.desktop.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Getter
@Setter
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class order {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_phone")
    private String customerPhone;
    // seperate item using comma separated values
    @Column(name = "food_items")
    private String foodItems;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
    @Column(name = "subtotal")
    private double subtotal;
    @Column(name = "tax")
    private double tax;
    @Column(name = "total_price")
    private double totalPrice;
    @Column(name = "order_time")
    private LocalDateTime orderTime;
    @Column(name = "estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;
    @Column(name = "actual_pickup_time")
    private LocalDateTime actualPickupTime;
    @Column(name = "special_instructions")
    private String specialInstructions;
    @Column(name = "restaurant_name")
    private String restaurantName;
    @Column(name = "restaurant_address")
    private String restaurantAddress;
    @Column(name = "payment_method")
    private String paymentMethod;

    public enum OrderStatus {
        ORDER_PENDING("Order Pending"),
        ORDER_ACCEPTED("Order Accepted"),
        ORDER_IN_KITCHEN("Order in Kitchen"),
        ORDER_READY_TO_PICKUP("Order Ready to Pickup"),
        ORDER_PICKED_UP("Order Picked Up"), 
        ORDER_CANCELLED("Order Cancelled");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OrderStatus.ORDER_READY_TO_PICKUP) {
            this.estimatedPickupTime = LocalDateTime.now().plusMinutes(15); // 15 minutes from ready
        } else if (newStatus == OrderStatus.ORDER_PICKED_UP) {
            this.actualPickupTime = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", foodItems='" + foodItems + '\'' +
                ", status=" + status +
                ", subtotal=" + subtotal +
                ", tax=" + tax +
                ", totalPrice=" + totalPrice +
                ", orderTime=" + orderTime +
                ", estimatedPickupTime=" + estimatedPickupTime +
                ", actualPickupTime=" + actualPickupTime +
                ", specialInstructions='" + specialInstructions + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantAddress='" + restaurantAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
