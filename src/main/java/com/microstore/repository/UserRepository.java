package com.microstore.repository;

import com.microstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Data Access Object (DAO) for User entity.
 * Extends JpaRepository which provides CRUD operations via JDBC underneath.
 * 
 * Demonstrates: Interface usage, DAO pattern, Database connectivity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find user by email */
    Optional<User> findByEmail(String email);

    /** Find user by email and password (login) */
    Optional<User> findByEmailAndPassword(String email, String password);

    /** Check if email already exists */
    boolean existsByEmail(String email);

    /** Find users by role */
    List<User> findByRole(String role);

    /** Search users by name or email */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsers(@Param("query") String query);
}
