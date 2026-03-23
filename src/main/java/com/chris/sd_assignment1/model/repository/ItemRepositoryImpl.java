package com.chris.sd_assignment1.model.repository;

import com.chris.sd_assignment1.model.entities.Category;
import com.chris.sd_assignment1.model.entities.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepositoryImpl implements ItemRepository {

    @Override
    public Item save(Item entity) {
        String sql = "INSERT INTO items (name, category_id, base_price, discount_percentage, stock_quantity, description, date_added, release_date, image_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getName());
            pstmt.setLong(2, entity.getCategory().getId());
            pstmt.setDouble(3, entity.getBasePrice());
            pstmt.setDouble(4, entity.getDiscountPercentage());
            pstmt.setInt(5, entity.getStockQuantity());
            pstmt.setString(6, entity.getDescription());
            pstmt.setDate(7, entity.getDateAdded() != null ? Date.valueOf(entity.getDateAdded()) : null);
            pstmt.setDate(8, entity.getReleaseDate() != null ? Date.valueOf(entity.getReleaseDate()) : null);
            pstmt.setBytes(9, entity.getImageData());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                entity.setId(rs.getLong(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "SELECT i.*, c.name AS category_name FROM items i LEFT JOIN categories c ON i.category_id = c.id WHERE i.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(extractItemFromResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS category_name FROM items i LEFT JOIN categories c ON i.category_id = c.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item update(Item entity) {
        String sql = "UPDATE items SET name = ?, category_id = ?, base_price = ?, discount_percentage = ?, stock_quantity = ?, description = ?, date_added = ?, release_date = ?, image_data = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getName());
            pstmt.setLong(2, entity.getCategory().getId());
            pstmt.setDouble(3, entity.getBasePrice());
            pstmt.setDouble(4, entity.getDiscountPercentage());
            pstmt.setInt(5, entity.getStockQuantity());
            pstmt.setString(6, entity.getDescription());
            pstmt.setDate(7, entity.getDateAdded() != null ? Date.valueOf(entity.getDateAdded()) : null);
            pstmt.setDate(8, entity.getReleaseDate() != null ? Date.valueOf(entity.getReleaseDate()) : null);
            pstmt.setBytes(9, entity.getImageData());
            pstmt.setLong(10, entity.getId());

            pstmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Item> findByCategory_Id(Long categoryId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS category_name FROM items i LEFT JOIN categories c ON i.category_id = c.id WHERE i.category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Item extractItemFromResultSet(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setName(rs.getString("name"));
        item.setBasePrice(rs.getDouble("base_price"));
        item.setDiscountPercentage(rs.getDouble("discount_percentage"));
        item.setStockQuantity(rs.getInt("stock_quantity"));
        item.setDescription(rs.getString("description"));

        Date dateAdded = rs.getDate("date_added");
        if (dateAdded != null) {
            item.setDateAdded(dateAdded.toLocalDate());
        }

        Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            item.setReleaseDate(releaseDate.toLocalDate());
        }

        item.setImageData(rs.getBytes("image_data"));

        Category category = new Category();
        category.setId(rs.getLong("category_id"));
        category.setName(rs.getString("category_name"));
        item.setCategory(category);

        return item;
    }
}