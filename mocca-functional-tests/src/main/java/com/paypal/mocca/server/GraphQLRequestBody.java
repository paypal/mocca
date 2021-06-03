package com.paypal.mocca.server;

import java.util.Map;

/**
 * Representation for GraphQL request body
 */
public class GraphQLRequestBody {

	/**
	 * GraphQL Query
	 */
	private String query;
	/**
	 * GraphQL Operation Name
	 */
	private String operationName;
	/**
	 * GraphQL variables
	 */
	private Map<String, Object> variables;

	/**
	 * Get query string
	 *
	 * @return query string
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Set query string
	 *
	 * @param query
	 *            query string
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Get GraphQL operation name
	 *
	 * @return operation name
	 */
	public String getOperationName() {
		return operationName;
	}

	/**
	 * Set operation name
	 *
	 * @param operationName
	 *            operation name
	 */
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	/**
	 * Get Variables
	 *
	 * @return variables
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}

	/**
	 * Set query variables
	 *
	 * @param variables
	 *            variables
	 */
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
}
