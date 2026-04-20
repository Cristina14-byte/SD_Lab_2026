package com.chris.sd_assignment1.controller;

import com.chris.sd_assignment1.model.entities.Category;
import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.Role;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.services.CartService;
import com.chris.sd_assignment1.model.services.CategoryService;
import com.chris.sd_assignment1.model.services.ItemService;
import com.chris.sd_assignment1.model.services.ServiceLocator;
import com.chris.sd_assignment1.model.export.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MainShopController {

    @FXML private Label welcomeLabel;
    @FXML private Button adminPanelButton;
    @FXML private Button cartButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private FlowPane itemsContainer;

    private User currentUser;
    private ItemService itemService;
    private CategoryService categoryService;
    private CartService cartService;
    private List<Item> allItems;
    private List<Item> currentViewList;

    public void initializeDependencies(User user, ItemService itemService, CategoryService categoryService, CartService cartService) {
        this.currentUser = user;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.cartService = cartService;

        setupUI();
        loadCategories();
        loadItems();
    }

    private void setupUI() {
        welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");

        if (currentUser.getRole() == Role.VISITOR) {
            cartButton.setDisable(true);
            cartButton.setOpacity(0.5);
        }

        if (currentUser.getRole() == Role.ADMIN) {
            adminPanelButton.setVisible(true);
            adminPanelButton.setManaged(true);
        }

        sortComboBox.getItems().addAll(
                "Default",
                "Price: Low to High",
                "Price: High to Low",
                "Name: A to Z",
                "Name: Z to A",
                "Category: A to Z"
        );
        sortComboBox.getSelectionModel().select("Default");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        categoryComboBox.setOnAction(e -> updateFilters());
        sortComboBox.setOnAction(e -> updateFilters());
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        categoryComboBox.getItems().addAll(categories);
    }

    private void loadItems() {
        allItems = itemService.getAllItems();
        currentViewList = allItems;
        displayItems(allItems);
    }

    private void displayItems(List<Item> itemsToDisplay) {
        itemsContainer.getChildren().clear();

        for (Item item : itemsToDisplay) {
            VBox card = createItemCard(item);
            itemsContainer.getChildren().add(card);
        }
    }

    private VBox createItemCard(Item item) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-cursor: hand;");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(180);
        imageView.setPreserveRatio(true);

        if (item.getImageData() != null && item.getImageData().length > 0) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(item.getImageData());
                Image image = new Image(bis);
                imageView.setImage(image);
            } catch (Exception e) {
                loadPlaceholderImage(imageView);
            }
        } else {
            loadPlaceholderImage(imageView);
        }

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setTextFill(javafx.scene.paint.Color.web("#2c3e50"));
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(200);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("€" + String.format("%.2f", item.getFinalPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setTextFill(javafx.scene.paint.Color.web("#e74c3c"));

        Label stockLabel = new Label("Stock: " + item.getStockQuantity());
        stockLabel.setTextFill(javafx.scene.paint.Color.web("#7f8c8d"));

        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        viewButton.setMaxWidth(Double.MAX_VALUE);
        viewButton.setOnAction(e -> onViewItemClick(item));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, viewButton);
        return card;
    }

    private void loadPlaceholderImage(ImageView imageView) {
        try {
            String path = "/com/chris/sd_assignment1/images/placeholder.png";
            Image placeholder = new Image(getClass().getResourceAsStream(path));
            imageView.setImage(placeholder);
        } catch (Exception e) {
        }
    }

    protected void updateFilters() {
        String searchText = searchField.getText().toLowerCase();
        Category selectedCategory = categoryComboBox.getValue();
        String sortOption = sortComboBox.getValue();

        currentViewList = allItems.stream()
                .filter(item -> searchText.isEmpty() || item.getName().toLowerCase().contains(searchText))
                .filter(item -> selectedCategory == null || item.getCategory().getId().equals(selectedCategory.getId()))
                .collect(Collectors.toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Price: Low to High":
                    currentViewList.sort((i1, i2) -> Double.compare(i1.getFinalPrice(), i2.getFinalPrice()));
                    break;
                case "Price: High to Low":
                    currentViewList.sort((i1, i2) -> Double.compare(i2.getFinalPrice(), i1.getFinalPrice()));
                    break;
                case "Name: A to Z":
                    currentViewList.sort((i1, i2) -> i1.getName().compareToIgnoreCase(i2.getName()));
                    break;
                case "Name: Z to A":
                    currentViewList.sort((i1, i2) -> i2.getName().compareToIgnoreCase(i1.getName()));
                    break;
                case "Category: A to Z":
                    currentViewList.sort((i1, i2) -> i1.getCategory().getName().compareToIgnoreCase(i2.getCategory().getName()));
                    break;
            }
        }

        displayItems(currentViewList);
    }

    @FXML
    protected void onClearFiltersClick() {
        searchField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        sortComboBox.getSelectionModel().select("Default");
        currentViewList = allItems;
        displayItems(allItems);
    }

    @FXML
    protected void onCartClick(ActionEvent event) {
        if (currentUser.getRole() == Role.VISITOR) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Guests cannot access the shopping cart. Please register or log in.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/cart.fxml"));
            Parent root = loader.load();

            CartController cartController = loader.getController();
            cartController.initializeDependencies(currentUser, itemService, categoryService, cartService);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1080, 720));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the cart page.");
        }
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/login.fxml"));
            Parent root = loader.load();

            ServiceLocator.getInstance().getCartService().clearCart();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to login screen.");
        }
    }

    @FXML
    private void onViewItemClick(Item item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/item_details.fxml"));
            Parent root = loader.load();

            ItemDetailsController detailsController = loader.getController();
            detailsController.initializeDependencies(item, currentUser, itemService, categoryService, cartService);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(item.getName() + " - Details");
            stage.setScene(new Scene(root, 600, 500));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the item details.");
        }
    }

    @FXML
    protected void onAdminPanelClick(ActionEvent event) {
        if (currentUser.getRole() != Role.ADMIN) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "You do not have permission to access the Admin Panel.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chris/sd_assignment1/view/admin_panel.fxml"));
            Parent root = loader.load();

            AdminPanelController adminPanelController = loader.getController();
            adminPanelController.initializeDependencies(currentUser, itemService, categoryService, cartService);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1080, 720));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the admin panel.");
        }
    }

    @FXML
    protected void onExportCsvClick(ActionEvent event) {
        executeExport(new CsvExportStrategy(), "shop_inventory");
    }

    @FXML
    protected void onExportJsonClick(ActionEvent event) {
        executeExport(new JsonExportStrategy(), "shop_inventory");
    }

    @FXML
    protected void onExportXmlClick(ActionEvent event) {
        executeExport(new XmlExportStrategy(), "shop_inventory");
    }

    private void executeExport(ExportStrategy strategy, String filename) {
        if (currentViewList == null || currentViewList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Export Error", "No items to export!");
            return;
        }

        try {
            ExportContext context = new ExportContext();
            context.setStrategy(strategy);
            context.executeExport(currentViewList, filename);
            showAlert(Alert.AlertType.INFORMATION, "Export Success", "File saved in your project folder!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Error exporting data: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}