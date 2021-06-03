package com.paypal.mocca.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.GraphqlErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * GraphQL endpoint for the server
 */
@Path("graphql")
public class GraphQLEndpoint {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLEndpoint.class);
    /**
     * GraphQL media type
     */
    private static final String MEDIA_TYPE_GRAPHQL = "application/graphql";
    /**
     * Jackson object mapper
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    /**
     * Main GraphQL instance which is shared by graphql-java library.  Need it to be static
     * as Jersey creates new instance of resource class for each request.
     */
    private static final GraphQL graphQL = GraphQLFactory.getInstance(OBJECT_MAPPER);
    /**
     * Cache to keep Content-Type to MimeType mappings
     */
    private final Map<String, MimeType> mimeTypeMap = new HashMap<>();

    /**
     * POST method which handles all GraphQL requests. GraphQL requests are always
     * sent to single URI '/graphql'
     *
     * @param asyncResponse Async response given by JAX-RS
     * @param contentType Content Type of the request. Supports both application/json and
     * application/graphql
     * @param body Query or Mutation string in request body
     * @param request The request object used for passing as a context to graphql
     * processor
     */
    @Consumes({MediaType.APPLICATION_JSON, MEDIA_TYPE_GRAPHQL})
    @Produces({MediaType.APPLICATION_JSON})
    @POST
    public void graphQLPost(@Suspended final AsyncResponse asyncResponse,
                            @HeaderParam("Content-Type") final String contentType,
                            String body, @Context final HttpServletRequest request) {
        LOGGER.debug("Invoking graphQLPost");
        try {
            ExecutionInput executionInput = createExecutionInput(contentType, body, request);
            CompletionStage<Map<String, Object>> mapCompletionStage = executeQuery(executionInput);
            mapCompletionStage.whenComplete((map, th) -> {
                if (th == null) {
                    asyncResponse.resume(Response.status(200).entity(map).build());
                } else {
                    asyncResponse.resume(th);
                }
            });

        } catch (Exception e) {
            asyncResponse.resume(e);
        }
    }

    /**
     * Executes GraphQL query using graphql-java library executeAsync option
     *
     * @param executionInput Execution Input
     */
    private CompletionStage<Map<String, Object>> executeQuery(final ExecutionInput executionInput) throws GraphqlErrorException {
        return graphQL.executeAsync(executionInput).thenApply((ExecutionResult executionResult) -> {
            LOGGER.debug("Received executionResult");
            Map<Object, Object> extensions = new HashMap<>();
            executionResult = new ExecutionResultImpl(executionResult.getData(), executionResult.getErrors(),
                    extensions);
            List<GraphQLError> errors = executionResult.getErrors();
            errors.forEach(it -> LOGGER.error(it.toString()));
            if (errors != null && !errors.isEmpty()) {
                // GraphQL finished processing, but errors were returned in the
                // execution result. Resume response with a
                // GraphQLErrorException so it will be handled by the PPaaS
                // GraphQL error exception mapper.
                throw new MoccaServerGraphQLException(executionResult);
            } else {
                return executionResult.toSpecification();
            }
        });
    }

    /**
     * Return a {@link CompletableFuture} that completes exceptionally with the
     * given exception
     *
     * @param e Exception
     * @return {@link CompletableFuture} that completes exceptionally with the given
     * exception
     */
    private CompletionStage<Map<String, Object>> toCompleteableFuture(Exception e) {
        CompletableFuture<Map<String, Object>> cf = new CompletableFuture<>();
        cf.completeExceptionally(e);
        return cf;
    }

    /**
     * Create execution input based on mime type
     *
     * @param contentType request body content type
     * @param body request body
     * @param request http request
     * @return graphql tools execution input
     * @throws IOException if request cannot be serviced.
     */
    private ExecutionInput createExecutionInput(final String contentType, final String body, HttpServletRequest request)
            throws IOException {

        if (StringUtils.isBlank(contentType)) {
            throw new NotSupportedException("Content-Type header is required");
        }

        ExecutionInput executionInput;
        final MimeType mimeType = getMimeType(contentType);
        switch (mimeType.getBaseType()) {
            case MediaType.APPLICATION_JSON:
                GraphQLRequestBody graphQLRequestBody = extractGraphQLRequestBody(body);
                executionInput = ExecutionInput.newExecutionInput().context(getExecutionContext(request))
                        .query(graphQLRequestBody.getQuery()).operationName(graphQLRequestBody.getOperationName())
                        .variables(graphQLRequestBody.getVariables()).build();
                break;
            case MEDIA_TYPE_GRAPHQL:
                executionInput = ExecutionInput.newExecutionInput().context(getExecutionContext(request)).query(body)
                        .build();
                break;
            default:
                throw new NotSupportedException(String.format("Content type %s not accepted", contentType));
        }
        return executionInput;
    }

    /**
     * This creates the execution context for an {@link ExecutionInput}. An
     * AuthUtils will only be instantiated and added if the security context header
     * exists, otherwise we will not create the authUtils instance because
     * presumably there is no need for security context.
     *
     * @param request The {@link HttpServletRequest} from JAX-RS
     * @return a {@link Map} representing the execution context
     * @throws IOException
     */
    private Map<String, Object> getExecutionContext(HttpServletRequest request) {
        final Map<String, Object> contextMap = new HashMap<>();
        contextMap.put(HttpServletRequest.class.getName(), request);
        return contextMap;
    }

    /**
     * Determine if given request body contains a batched GraphQL query
     *
     * @param body Request body
     * @return True if batched query, false otherwise
     */
    private boolean isBatchedQuery(String body) {
        try {
            // Attempt to read as a list of queries
            OBJECT_MAPPER.readValue(body, new TypeReference<List<GraphQLRequestBody>>() {
            });
            // Batched query detected
            return true;
        } catch (Exception e) {
            // Batched query not detected
            return false;
        }
    }

    /**
     * Gets mime-type for a given Content-Type HTTP header. Gets it from cache if
     * present.
     *
     * @param contentType Content-Type HTTP header
     * @return MimeType
     * @throws NotSupportedException if mime type could not be parsed from header
     */
    private MimeType getMimeType(final String contentType) throws NotSupportedException {
        return mimeTypeMap.computeIfAbsent(contentType, (key) -> {
            MimeType mimeType;
            try {
                /* parsing of string happens on construction */
                mimeType = new MimeType(contentType);
            } catch (MimeTypeParseException e) {
                throw new NotSupportedException(String.format("Content type %s is not a valid mime type", contentType),
                        e);
            }
            return mimeType;
        });
    }

    /**
     * Extract graphql request body
     *
     * @param body graphql request
     * @return GraphQLRequestBody
     * @throws JsonParseException if json is malformed
     * @throws JsonMappingException if json cannot be mapped
     */
    private GraphQLRequestBody extractGraphQLRequestBody(final String body)
            throws JsonParseException, JsonMappingException {

        GraphQLRequestBody graphQLRequestBody;
        try {
            graphQLRequestBody = OBJECT_MAPPER.readValue(body, GraphQLRequestBody.class);
        } catch (Exception e) {
            // Could not read body as a single query.
            if (isBatchedQuery(body)) {
                throw new RuntimeException("Batch queries not supported");
            }

            try {
                // Rethrow and handle original exception with catches (sonar does
                // not like it when `if instanceof` statements are used)
                throw e;
            } catch (JsonParseException | JsonMappingException je) {
                // Rethrow to corresponding ExceptionMapper
                throw je;
            } catch (Exception fe) {
                throw new RuntimeException("Format not supported", fe);
            }

        }

        if (StringUtils.isBlank(graphQLRequestBody.getQuery())) {
            // Request body does not contain a query
            throw new RuntimeException("No Query");
        }
        if (null == graphQLRequestBody.getVariables()) {
            // graphql-java now requires variables element
            graphQLRequestBody.setVariables(new HashMap<String, Object>());
        }
        return graphQLRequestBody;

    }

    /**
     * Custom exception if graphql request fails.
     */
    public static class MoccaServerGraphQLException extends RuntimeException {
        /**
         * Error message
         */
        private static final String MESSAGE = "GraphQL errors to be handled by Exception mapper";
        /**
         * Execution request
         */
        private final ExecutionResult executionResult;

        /**
         * Basic constructor
         *
         * @param executionResult execution result
         */
        public MoccaServerGraphQLException(ExecutionResult executionResult) {
            super(MESSAGE);
            this.executionResult = executionResult;
        }

        /**
         * Get execution result if needed
         *
         * @return ExecutionResult
         */
        public ExecutionResult getExecutionResult() {
            return this.executionResult;
        }
    }

}
