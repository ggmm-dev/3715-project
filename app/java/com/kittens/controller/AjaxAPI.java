package com.kittens.controller;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.kittens.database.Dataset;
import com.kittens.database.User;
import com.kittens.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AjaxAPI extends BaseController {

	// the version of this object
	public static final long serialVersionUID = 0L;
	// JSON serilaizer/deserializer
	private final JsonParser parser = new JsonParser();

	/**
	 * Parses a {@code JSONObject} into a dataset.
	 * <pre>
	 * {@code
	 * {
	 *     "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
	 *     "name": "The name of the dataset",
	 *     "description": "The description of the dataset."
	 *     "headers": ["a", "b", "c"],
	 *     "rows": [
	 *         ["foo", "bar", "baz"],
	 *         ["foo", "bar", "baz"]
	 *     ]
	 * }
	 * }
	 * </pre>
	 */
	private Dataset parseDataset(JsonObject datasetJson, final User user) {
		// fields
		final String name, description;
		final ArrayList<String> headers = new ArrayList<String>();
		final ArrayList<Dataset.Row> rows = new ArrayList<Dataset.Row>();
		// set the fields
		name = datasetJson.get("name").getAsString();
		description = datasetJson.get("description").getAsString();
		JsonArray jsonHeaders = datasetJson.getAsJsonArray("headers");
		for (JsonElement header : jsonHeaders) {
			headers.add(header.getAsString());
		}
		JsonArray rowsArrayJson = datasetJson.getAsJsonArray("rows");
		// for each row
		for (JsonElement rowJson : rowsArrayJson) {
			JsonArray rowArrayJson = rowJson.getAsJsonArray();
			ArrayList<String> values = new ArrayList<String>();
			for (JsonElement columnJson : rowArrayJson) {
				values.add(columnJson.getAsString());
			}
			rows.add(new Dataset.Row(values));
		}
		return (new Dataset(user, name, description, new Date())).setHeaders(headers).setRows(rows);
	}
	/**
	 * Handle PUT requests.
	 */
	@Override public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User currentSessionUser = Utils.getUserFromRequest(request);
		if (database == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		if (currentSessionUser == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		String json = Utils.readStream(request.getInputStream());
		Dataset dataset = parseDataset(parser.parse(json).getAsJsonObject(), currentSessionUser);
		if (!dataset.isValid()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		try {
			database.addDataset(currentSessionUser, dataset);
		}
		catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

}
