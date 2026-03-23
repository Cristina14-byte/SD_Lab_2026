package com.chris.sd_assignment1.model.repository;

import com.chris.sd_assignment1.model.entities.Item;
import com.chris.sd_assignment1.model.entities.Order;
import com.chris.sd_assignment1.model.entities.OrderItem;
import com.chris.sd_assignment1.model.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order save(Order entity) {
        String insertOrderSql = "INSERT INTO orders (user_id, order_date, status, total_price) VALUES (?, ?, ?, ?) RETURNING id";
        String insertOrderItemSql = "INSERT INTO order_items (order_id, item_id, quantity, price_per_unit) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql)) {
                orderStmt.setLong(1, entity.getUser().getId());
                orderStmt.setTimestamp(2, Timestamp.valueOf(entity.getOrderDate()));
                orderStmt.setString(3, entity.getStatus());
                orderStmt.setDouble(4, entity.getTotalPrice());

                ResultSet rs = orderStmt.executeQuery();
                Long generatedOrderId = null;
                if (rs.next()) {
                    generatedOrderId = rs.getLong(1);
                }

                if (generatedOrderId == null) {
                    conn.rollback();
                    throw new SQLException("Eroare la generarea ID-ului pentru comanda.");
                }

                try (PreparedStatement itemStmt = conn.prepareStatement(insertOrderItemSql)) {
                    for (OrderItem orderItem : entity.getItems()) {
                        itemStmt.setLong(1, generatedOrderId);
                        itemStmt.setLong(2, orderItem.getItem().getId());
                        itemStmt.setInt(3, orderItem.getQuantity());
                        itemStmt.setDouble(4, orderItem.getPricePerUnit());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();
                conn.setAutoCommit(true);

                return new Order(generatedOrderId, entity.getUser(), entity.getOrderDate(), entity.getStatus(), entity.getItems(), entity.getTotalPrice());
            } catch (SQLException ex) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String orderSql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(orderSql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Order partialOrder = extractBaseOrder(rs);
                List<OrderItem> items = findOrderItemsByOrderId(id, conn);
                return Optional.of(new Order(partialOrder.getId(), partialOrder.getUser(), partialOrder.getOrderDate(), partialOrder.getStatus(), items, partialOrder.getTotalPrice()));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order partialOrder = extractBaseOrder(rs);
                List<OrderItem> items = findOrderItemsByOrderId(partialOrder.getId(), conn);
                orders.add(new Order(partialOrder.getId(), partialOrder.getUser(), partialOrder.getOrderDate(), partialOrder.getStatus(), items, partialOrder.getTotalPrice()));
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order update(Order entity) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getStatus());
            pstmt.setLong(2, entity.getId());
            pstmt.executeUpdate();

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order partialOrder = extractBaseOrder(rs);
                List<OrderItem> items = findOrderItemsByOrderId(partialOrder.getId(), conn);
                orders.add(new Order(partialOrder.getId(), partialOrder.getUser(), partialOrder.getOrderDate(), partialOrder.getStatus(), items, partialOrder.getTotalPrice()));
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Order extractBaseOrder(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));

        return new Order(
                rs.getLong("id"),
                user,
                rs.getTimestamp("order_date").toLocalDateTime(),
                rs.getString("status"),
                new ArrayList<>(),
                rs.getDouble("total_price")
        );
    }

    private List<OrderItem> findOrderItemsByOrderId(Long orderId, Connection conn) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getLong("item_id"));

                items.add(new OrderItem(
                        rs.getLong("id"),
                        item,
                        rs.getInt("quantity"),
                        rs.getDouble("price_per_unit")
                ));
            }
        }
        return items;
    }
}