package com.paypal.mocca.repository;

import com.paypal.mocca.model.Author;

import java.util.HashMap;
import java.util.Map;

/**
 * Author repository which caches author data
 */
public class AuthorRepository {
    /**
     * Author map
     */
    private static final Map<Integer, Author> AUTHOR_MAP = new HashMap<>();

    /**
     * Constructor which loads initial data
     */
    public AuthorRepository() {
        Author author = new Author();
        author.setId(1);
        author.setName("Test1");
        AUTHOR_MAP.put(1, author);
    }

    /**
     * Save author to store.  Calculates id based on map size
     *
     * @param name author name
     * @return Saved author with id updated
     */
    public Author save(String name) {
        final int id = AUTHOR_MAP.size() + 1;
        Author author = new Author();
        author.setId(id);
        author.setName(name);
        AUTHOR_MAP.put(id, author);
        return author;
    }

    /**
     * Get given author by id.  Null author does not exists.
     *
     * @param authorId author id
     * @return author
     */
    public Author get(int authorId) {
        return AUTHOR_MAP.get(authorId);
    }
}
