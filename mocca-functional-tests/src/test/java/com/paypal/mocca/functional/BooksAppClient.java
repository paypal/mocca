package com.paypal.mocca.functional;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.Variable;
import com.paypal.mocca.client.model.Author;
import com.paypal.mocca.client.model.Book;

import java.util.List;

public interface BooksAppClient extends MoccaClient {

    // FIXME This method is not used anywhere at the moment because Feign does not call the registered encoder
    //  if the request method does not have any parameter
    @Query
    List<Book> books();

    @Query
    List<Book> books(String variables);

    @Mutation
    Author addAuthor(@Variable(ignore = "id") Author author);

    @Mutation
    Book addBook(String variables);

}
