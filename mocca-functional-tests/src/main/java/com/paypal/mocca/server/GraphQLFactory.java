package com.paypal.mocca.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.paypal.mocca.model.Author;
import com.paypal.mocca.repository.AuthorRepository;
import com.paypal.mocca.repository.BookRepository;
import com.paypal.mocca.resolvers.MutationResolver;
import com.paypal.mocca.resolvers.QueryResolver;
import graphql.GraphQL;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.kickstart.tools.PerFieldObjectMapperProvider;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserBuilder;
import graphql.kickstart.tools.SchemaParserOptions;
import graphql.schema.GraphQLSchema;

import java.util.Arrays;
import java.util.List;

/**
 * Factory which instantiates GraphQL
 */
public final class GraphQLFactory {
    /**
     * GraphQL instance
     */
    private static GraphQL graphQL;

    /**
     * Static factory method
     *
     * @param objectMapper jackson objectmapper
     * @return GraphQL
     */
    public static GraphQL getInstance(ObjectMapper objectMapper) {
        if (graphQL == null) {
            graphQL = graphQL(objectMapper);
        }
        return graphQL;
    }

    /**
     * GraphQL Schema representation.
     *
     * @param schemaParser Schema parser
     * @return GraphQLSchema
     */
    public static GraphQLSchema graphQLSchema(SchemaParser schemaParser) {
        return schemaParser.makeExecutableSchema();
    }

    /**
     * Instantiate new graphql instance
     *
     * @param objectMapper Jackson Objectmapper
     * @return GraphQL
     */
    public static GraphQL graphQL(final ObjectMapper objectMapper) {
        return GraphQL.newGraphQL(graphQLSchema(schemaParser(objectMapper))).build();
    }

    /**
     * Create new schema parser for graphql-java
     *
     * @param objectMapper Jackson Object mapper
     * @return SchemaParser
     */
    public static SchemaParser schemaParser(final ObjectMapper objectMapper) {
        SchemaParserOptions.Builder optionsBuilder = SchemaParserOptions.newOptions();
        optionsBuilder.introspectionEnabled(true);

        SchemaParserBuilder builder = new SchemaParserBuilder();
        builder.files("schema/query.graphqls",
                "schema/book.graphqls",
                "schema/author.graphqls",
                "schema/mutation.graphqls");
        builder.dictionary(Author.class);


        objectMapper.registerModule(new Jdk8Module());
        PerFieldObjectMapperProvider perFieldObjectMapperProvider = fieldDefinition -> objectMapper;
        optionsBuilder.objectMapperProvider(perFieldObjectMapperProvider);
        final BookRepository bookRepository = new BookRepository();
        final AuthorRepository authorRepository = new AuthorRepository();
        List<GraphQLResolver<?>> resolvers = Arrays.asList(
                new QueryResolver(bookRepository, authorRepository),
                new MutationResolver(bookRepository, authorRepository)
        );
        return builder.resolvers(resolvers).build();
    }
}
