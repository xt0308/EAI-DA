package com.example.EAI.Assignment.desktop.controller;

import com.example.EAI.Assignment.desktop.service.OrderApiService;
import com.example.EAI.Assignment.desktop.model.order;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.context.ApplicationContext;
import com.example.EAI.Assignment.desktop.SpringContextHolder;

import java.time.format.DateTimeFormatter;

@Component
public class OrderDetailPageController {

    @Autowired
    private OrderApiService orderApiService;

    @FXML
    private Text orderIdText;
    
    @FXML
    private Label customerNameLabel;
    
    @FXML
    private Label customerPhoneLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label orderTimeLabel;
    
    @FXML
    private Label estimatedPickupLabel;
    
    @FXML
    private Label actualPickupLabel;
    
    @FXML
    private TextArea specialInstructionsTextArea;
    
    @FXML
    private Label subtotalLabel;
    
    @FXML
    private Label taxLabel;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Label paymentMethodLabel;
    
    @FXML
    private Label restaurantNameLabel;
    
    @FXML
    private Label restaurantAddressLabel;
    
    @FXML
    private Button updateStatusButton;
    
    @FXML
    private Button cancelOrderButton;
    
    @FXML
    private Button deleteOrderButton;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private ListView<String> foodItemsListView;

    @FXML
    private Button backButton;

    private order currentOrder;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    public void setOrder(order order) {
        this.currentOrder = order;
        populateOrderDetails();
        updateButtonVisibility();
    }

    private void populateOrderDetails() {
        if (currentOrder == null) return;

        orderIdText.setText("Order ID: " + currentOrder.getId().toString());
        customerNameLabel.setText(currentOrder.getCustomerName());
        customerPhoneLabel.setText(currentOrder.getCustomerPhone());
        statusLabel.setText(currentOrder.getStatus().getDisplayName());
        
        orderTimeLabel.setText(currentOrder.getOrderTime() != null ? 
                currentOrder.getOrderTime().format(timeFormatter) : "N/A");
        
        estimatedPickupLabel.setText(currentOrder.getEstimatedPickupTime() != null ? 
                currentOrder.getEstimatedPickupTime().format(timeFormatter) : "N/A");
        
        actualPickupLabel.setText(currentOrder.getActualPickupTime() != null ? 
                currentOrder.getActualPickupTime().format(timeFormatter) : "N/A");
        
        // Populate food items as a list
        if (foodItemsListView != null) {
            String foodItems = currentOrder.getFoodItems();
            if (foodItems != null && !foodItems.trim().isEmpty()) {
                String[] items = foodItems.split(",");
                ObservableList<String> foodList = FXCollections.observableArrayList();
                for (String item : items) {
                    foodList.add(item.trim());
                }
                foodItemsListView.setItems(foodList);
            } else {
                foodItemsListView.setItems(FXCollections.observableArrayList("No food items"));
            }
        }
        
        specialInstructionsTextArea.setText(currentOrder.getSpecialInstructions() != null ? 
                currentOrder.getSpecialInstructions() : "No special instructions");
        
        subtotalLabel.setText(String.format("RM%.2f", currentOrder.getSubtotal()));
        taxLabel.setText(String.format("RM%.2f", currentOrder.getTax()));
        totalLabel.setText(String.format("RM%.2f", currentOrder.getTotalPrice()));
        paymentMethodLabel.setText(currentOrder.getPaymentMethod());
        restaurantNameLabel.setText(currentOrder.getRestaurantName());
        restaurantAddressLabel.setText(currentOrder.getRestaurantAddress());
    }

    private void updateButtonVisibility() {
        if (currentOrder.getStatus() == order.OrderStatus.ORDER_CANCELLED || currentOrder.getStatus() == order.OrderStatus.ORDER_PICKED_UP) {
            deleteOrderButton.setVisible(true);
            updateStatusButton.setDisable(true);
            cancelOrderButton.setDisable(true);
        } else {
            deleteOrderButton.setVisible(false);
            updateStatusButton.setDisable(false);
            cancelOrderButton.setDisable(false);
        }
    }

    @FXML
    private void handleUpdateStatus() {
        ChoiceDialog<order.OrderStatus> dialog = new ChoiceDialog<>(currentOrder.getStatus(), order.OrderStatus.values());
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Select new status for order " + currentOrder.getOrderId());
        dialog.setContentText("Choose status:");

        dialog.showAndWait().ifPresent(newStatus -> {
            if (newStatus != currentOrder.getStatus()) {
                setLoading(true);
                orderApiService.updateOrderStatus(currentOrder.getId().toString(), newStatus)
                        .subscribe(
                            updatedOrder -> Platform.runLater(() -> {
                                setLoading(false);
                                currentOrder = updatedOrder;
                                populateOrderDetails();
                                updateButtonVisibility();
                                showAlert("Success", "Order status updated successfully to " + newStatus.getDisplayName());
                            }),
                            error -> Platform.runLater(() -> {
                                setLoading(false);
                                showAlert("Error", "Failed to update order status: " + error.getMessage());
                            })
                        );
            }
        });
    }

    @FXML
    private void handleCancelOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Order");
        alert.setHeaderText("Cancel Order Confirmation");
        alert.setContentText("Are you sure you want to cancel order " + currentOrder.getOrderId() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                setLoading(true);
                orderApiService.updateOrderStatus(currentOrder.getId().toString(), order.OrderStatus.ORDER_CANCELLED)
                        .subscribe(
                            updatedOrder -> Platform.runLater(() -> {
                                setLoading(false);
                                currentOrder = updatedOrder;
                                populateOrderDetails();
                                updateButtonVisibility();
                                showAlert("Success", "Order cancelled successfully");
                            }),
                            error -> Platform.runLater(() -> {
                                setLoading(false);
                                showAlert("Error", "Failed to cancel order: " + error.getMessage());
                            })
                        );
            }
        });
    }

    @FXML
    private void handleDeleteOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Order");
        alert.setHeaderText("Delete Order Confirmation");
        alert.setContentText("Are you sure you want to permanently delete order " + currentOrder.getOrderId() + "?\nThis action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                setLoading(true);
                orderApiService.deleteOrder(currentOrder.getId().toString())
                        .subscribe(
                            result -> Platform.runLater(() -> {
                                setLoading(false);
                                showAlert("Success", "Order deleted successfully");
                                closeWindow();
                            }),
                            error -> Platform.runLater(() -> {
                                setLoading(false);
                                showAlert("Error", "Failed to delete order: " + error.getMessage());
                            })
                        );
            }
        });
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    @FXML
    private void handleBack() {
        // Switch back to the home page in the same window
        try {
            ApplicationContext springContext = SpringContextHolder.getContext();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomePage.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent homeRoot = loader.load();
            Scene scene = backButton.getScene();
            scene.setRoot(homeRoot);
        } catch (Exception e) {
            showAlert("Navigation Error", "Failed to go back to home page: " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        updateStatusButton.setDisable(loading);
        cancelOrderButton.setDisable(loading);
        deleteOrderButton.setDisable(loading);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 