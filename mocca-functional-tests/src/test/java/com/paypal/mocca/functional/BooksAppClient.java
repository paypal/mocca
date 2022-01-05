package com.paypal.mocca.functional;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.Var;
import com.paypal.mocca.client.model.Author;
import com.paypal.mocca.client.model.Book;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface BooksAppClient extends MoccaClient {

    @Query
    List<Book> books();

    @Mutation
    Author addAuthor(@NotNull @Var("name") String authorName);

    @Mutation
    Book addBook(@Var("name") String name, @Var("authorId") int authorId);

}
