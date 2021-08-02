package com.paypal.mocca.functional;

import com.paypal.mocca.client.model.Author;
import com.paypal.mocca.client.model.Book;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Basic mutation tests
 */
public class MoccaMutationTest extends AbstractFunctionalTests {

    /**
     * Test basic graphql mutations on books
     *
     * @throws Exception if request cannot be made
     */
    @Test
    public void testBasicMutations() throws Exception {

        //Add Author first
        Author author = client.addAuthor("mocca");

        assertNotNull(author);
        assertNotNull(author.getId());
        assertEquals(author.getName(), "mocca");

        //Add Book 
        Book book = client.addBook("moccaBook", author.getId());
        assertNotNull(book);
        assertNotNull(book.getId());
        assertEquals(book.getAuthorId(), author.getId());
        assertEquals(book.getName(), "moccaBook");
    }
}
