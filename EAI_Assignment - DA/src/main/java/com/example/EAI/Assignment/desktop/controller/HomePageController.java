package com.example.EAI.Assignment.desktop.controller;

import com.example.EAI.Assignment.desktop.service.OrderApiService;
import com.example.EAI.Assignment.desktop.model.order;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import com.example.EAI.Assignment.desktop.SpringContextHolder;
import javafx.geometry.Pos;


@Component
public class HomePageController {

    @Autowired
    private OrderApiService orderApiService;

    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Label searchResultLabel;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private TableView<order> ordersTable;
    
    @FXML
    private TableColumn<order, String> orderIdColumn;
    
    @FXML
    private TableColumn<order, String> customerNameColumn;
    
    @FXML
    private TableColumn<order, String> statusColumn;
    
    @FXML
    private TableColumn<order, Double> totalPriceColumn;
    
    @FXML
    private TableColumn<order, String> orderTimeColumn;
    
    @FXML
    private TableColumn<order, Void> actionsColumn;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private ProgressIndicator loadingIndicator;

    private ObservableList<order> ordersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableActions();
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadAllOrders();
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getId().toString())
        );
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        statusColumn.setCellValueFactory(cellData -> {
            order.OrderStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status.getDisplayName());
        });
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderTimeColumn.setCellValueFactory(cellData -> {
            String formattedTime = cellData.getValue().getOrderTime()
                    .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            return new SimpleStringProperty(formattedTime);
        });
        
        // Center align all columns
        orderIdColumn.setCellFactory(column -> new TableCell<order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-alignment: CENTER;");
            }
        });
        customerNameColumn.setCellFactory(column -> new TableCell<order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-alignment: CENTER;");
            }
        });
        statusColumn.setCellFactory(column -> new TableCell<order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-alignment: CENTER;");
            }
        });
        totalPriceColumn.setCellFactory(column -> new TableCell<order, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : String.format("RM%.2f", price));
                setStyle("-fx-alignment: CENTER;");
            }
        });
        orderTimeColumn.setCellFactory(column -> new TableCell<order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    private void setupTableActions() {
        actionsColumn.setCellFactory(column -> new TableCell<order, Void>() {
            private final Button viewButton = new Button("View");
            private final Button updateButton = new Button("Update");
            
            {
                viewButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
                updateButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
                
                viewButton.setOnAction(event -> {
                    order order = getTableView().getItems().get(getIndex());
                    openOrderDetailPage(order);
                });
                
                updateButton.setOnAction(event -> {
                    order order = getTableView().getItems().get(getIndex());
                    openOrderDetailPage(order);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    buttons.setAlignment(Pos.CENTER);
                    buttons.getChildren().addAll(viewButton, updateButton);
                    setGraphic(buttons);
                }
            }
        });
        
        // Double-click to view order details
        ordersTable.setRowFactory(tv -> {
            TableRow<order> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    order order = row.getItem();
                    openOrderDetailPage(order);
                }
            });
            return row;
        });
    }

    @FXML
    private void handleSearch() {
        String orderId = searchField.getText().trim();
        if (orderId.isEmpty()) {
            showAlert("Search Error", "Please enter an order ID to search.");
            return;
        }

        setLoading(true);
        orderApiService.getOrderById(orderId)
                .subscribe(
                    foundOrder -> Platform.runLater(() -> {
                        setLoading(false);
                        if (foundOrder != null) {
                            openOrderDetailPage(foundOrder);
                        } else {
                            searchResultLabel.setText("Order not found");
                            searchResultLabel.setStyle("-fx-text-fill: #dc3545;");
                        }
                    }),
                    error -> Platform.runLater(() -> {
                        setLoading(false);
                        searchResultLabel.setText("Error searching for order");
                        searchResultLabel.setStyle("-fx-text-fill: #dc3545;");
                        showAlert("Search Error", "Failed to search for order: " + error.getMessage());
                    })
                );
    }

    @FXML
    private void handleRefresh() {
        loadAllOrders();
    }

    private void loadAllOrders() {
        setLoading(true);
        statusLabel.setText("Loading orders...");
        
        orderApiService.getAllOrders()
                .subscribe(
                    orders -> Platform.runLater(() -> {
                        setLoading(false);
                        ordersList.clear();
                        ordersList.addAll(orders);
                        ordersTable.setItems(ordersList);
                        statusLabel.setText("Loaded " + orders.size() + " orders");
                    }),
                    error -> Platform.runLater(() -> {
                        setLoading(false);
                        statusLabel.setText("Error loading orders");
                        statusLabel.setStyle("-fx-text-fill: #dc3545;");
                        showAlert("Error", "Failed to load orders: " + error.getMessage());
                    })
                );
    }

    private void openOrderDetailPage(order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderDetailPage.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            Parent root = loader.load();
            OrderDetailPageController controller = loader.getController();
            controller.setOrder(order);
            // Replace the root of the current scene
            Scene scene = ordersTable.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showAlert("Error", "Failed to open order details: " + e.getMessage());
        }
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        searchButton.setDisable(loading);
        refreshButton.setDisable(loading);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 