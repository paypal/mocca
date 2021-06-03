package com.paypal.mocca.repository;

import com.paypal.mocca.model.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Book repository which caches books data
 */
public class BookRepository {
    /**
     * Books store
     */
    private static final Map<Integer, Book> BOOK_MAP = new HashMap<>();

    /**
     * Constructor which loads initial data
     */
    public BookRepository() {
        Book book1 = new Book();
        book1.setId(1);
        book1.setName("Book1");

        Book book2 = new Book();
        book2.setId(2);
        book2.setName("Book2");
        BOOK_MAP.put(book1.getId(), book1);
        BOOK_MAP.put(book2.getId(), book2);
    }

    /**
     * Save book to store.  Calculates id based on map size
     *
     * @param name book name
     * @param authorId book author id
     * @return Saved book with id updated
     */
    public Book save(String name, int authorId) {
        final int id = BOOK_MAP.size() + 1;
        Book book = new Book();
        book.setId(id);
        book.setName(name);
        book.setAuthorId(authorId);
        BOOK_MAP.put(id, book);
        return book;
    }

    /**
     * Get book by id.  Null if book does not exists.
     *
     * @param id book id
     * @return Book
     */
    public Book get(int id) {
        return BOOK_MAP.get(id);
    }

    /**
     * Get all books
     *
     * @return all saved books
     */
    public List<Book> getAll() {
        return BOOK_MAP.entrySet()
                .stream().map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
