package com.microstore.model;

/**
 * Searchable Interface - Defines search capability for entities.
 * 
 * OOP Concepts Demonstrated:
 * - INTERFACE: Defines a contract that implementing classes must follow
 * - ABSTRACTION: Hides search implementation details
 * - POLYMORPHISM: Different entities implement search differently
 */
public interface Searchable {

    /**
     * Checks if the entity matches a given search query.
     * @param query The search string to match against
     * @return true if the entity matches the query
     */
    boolean matchesSearchQuery(String query);

    /**
     * Returns all searchable text content from the entity.
     * Used for building search indexes or full-text search.
     * @return Concatenated searchable text
     */
    String getSearchableText();
}
