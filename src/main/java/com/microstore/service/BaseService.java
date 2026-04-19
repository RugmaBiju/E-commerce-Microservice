package com.microstore.service;

import java.util.List;
import java.util.Optional;

/**
 * BaseService Interface - Generic service interface for CRUD operations.
 * 
 * OOP Concepts Demonstrated:
 * - INTERFACE: Defines a contract for all service implementations
 * - GENERICS: Type-safe operations for any entity type
 * - ABSTRACTION: Hides implementation details
 */
public interface BaseService<T> {

    /** Create a new entity */
    T create(T entity);

    /** Read/Find entity by ID */
    Optional<T> findById(Long id);

    /** Read all entities */
    List<T> findAll();

    /** Update an entity */
    T update(Long id, T entity);

    /** Delete an entity */
    void delete(Long id);

    /** Search entities by query */
    List<T> search(String query);

    /** Count all entities */
    long count();
}
