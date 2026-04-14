package main.java.com.ecom.dao;

import com.ecom.model.Product;
import com.ecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements DAO<Product> {

    // CREATE
    @Override
    public void create(Product p) throws Exception {
        String sql = "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt   (4, p.getStock());
            ps.executeUpdate();
            System.out.println("✅ Product added!");
        }
    }

    // READ — by ID
    @Override
    public Product findById(int id) throws Exception {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // READ — all products
    @Override
    public List<Product> findAll() throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // UPDATE
    @Override
    public void update(Product p) throws Exception {
        String sql = "UPDATE products SET name=?, category=?, price=?, stock=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt   (4, p.getStock());
            ps.setInt   (5, p.getId());
            ps.executeUpdate();
            System.out.println("✅ Product updated!");
        }
    }

    // DELETE
    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Product deleted!");
        }
    }

    // SEARCH by name or category
    public List<Product> search(String keyword) throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt   ("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getDouble("price"),
            rs.getInt   ("stock")
        );
    }
}