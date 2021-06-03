package com.paypal.mocca.resolvers;

import com.paypal.mocca.model.Author;
import com.paypal.mocca.model.Book;
import com.paypal.mocca.repository.AuthorRepository;
import com.paypal.mocca.repository.BookRepository;

/**
 * GraphQL mutation resolver for all mutations
 */
public class MutationResolver implements com.paypal.mocca.api.MutationResolver {
    /**
     * Book Repository
     */
    private final BookRepository bookRepository;
    /**
     * Author repository
     */
    private final AuthorRepository authorRepository;

    /**
     * Initialize resolver with both repositories
     *
     * @param bookRepository book repository
     * @param authorRepository author repoisitory
     */
    public MutationResolver(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    /**
     * Add given book to store
     *
     * @param name book name
     * @param authorId author id
     * @return added book with id
     */
    @Override
    public Book addBook(String name, Integer authorId) {
        if (authorRepository.get(authorId) == null) {
            throw new RuntimeException("Author does not exists");
        }
        return this.bookRepository.save(name, authorId);
    }

    /**
     * Add author to store
     *
     * @param name author name
     * @return added author with id
     */
    @Override
    public Author addAuthor(String name) {
        return this.authorRepository.save(name);
    }
}
