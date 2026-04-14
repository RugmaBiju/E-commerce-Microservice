package main.java.com.ecom.dao;

import com.ecom.model.User;
import com.ecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements DAO<User> {

    // CREATE — Register new user
    @Override
    public void create(User u) throws Exception {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.executeUpdate();
            System.out.println("✅ User registered successfully!");
        }
    }

    // READ — Find by ID
    @Override
    public User findById(int id) throws Exception {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // READ — Find all users
    @Override
    public List<User> findAll() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // UPDATE
    @Override
    public void update(User u) throws Exception {
        String sql = "UPDATE users SET name=?, email=?, role=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getRole());
            ps.setInt   (4, u.getId());
            ps.executeUpdate();
            System.out.println("✅ User updated!");
        }
    }

    // DELETE
    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ User deleted!");
        }
    }

    // Check login (bonus)
    public User login(String email, String password) throws Exception {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // Map DB row → User object
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt   ("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role")
        );
    }
}
