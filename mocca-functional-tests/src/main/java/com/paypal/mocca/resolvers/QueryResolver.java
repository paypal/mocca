package com.paypal.mocca.resolvers;

import com.paypal.mocca.model.Book;
import com.paypal.mocca.repository.AuthorRepository;
import com.paypal.mocca.repository.BookRepository;

import java.util.List;

/**
 * Query resolver for application
 */
public class QueryResolver implements com.paypal.mocca.api.QueryResolver {

    /**
     * Author repository
     */
    private final AuthorRepository authorRepository;
    /**
     * Book repository
     */
    private final BookRepository bookRepository;

    /**
     * Injects book repository and author repository
     *
     * @param bookRepository BookRepository
     * @param authorRepository AuthorRepository
     */
    public QueryResolver(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Get list of all books on store
     *
     * @return books list
     */
    @Override
    public List<Book> books() {
        return this.bookRepository.getAll();
    }
}
