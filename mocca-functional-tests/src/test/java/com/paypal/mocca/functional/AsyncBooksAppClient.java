package com.paypal.mocca.functional;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.Var;
import com.paypal.mocca.client.model.Author;
import com.paypal.mocca.client.model.Book;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncBooksAppClient extends MoccaClient {

    @Query
    CompletableFuture<List<Book>> books();

}