package com.kittens.database;

import com.kittens.Utils;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;

public class Dataset extends Object {

	/**
	 * A row in a dataset.
	 */
	public static final class Row extends Object {

		// the array of values in this row
		private String[] values;

		/**
		 * Creates a row with the given values.
		 */
		public Row(String ... values) {
			this.values = values;
		}
		/**
		 * Returns the size/width of this row.
		 */
		public int getWidth() {
			return values.length;
		}
		/**
		 * Sets the values contained in the row.
		 */
		public Row setValues(String ... values) {
			this.values = values;
			return this;
		}
		/**
		 * Returns the values in this row.
		 */
		public String[] getValues() {
			return values;
		}

	}

	/**
	 *
	 */
	public static final Dataset newSampleDataset(final User user) {
		return new Dataset(
			user,
			"Sample Dataset",
			"A sample dataset just for you."
		).setHeaders(
			"person",
			"place",
			"thing"
		).setRows(
			new Dataset.Row("foo", "bar", "baz"),
			new Dataset.Row("12", "10", "42")
		);
	}

	// uuid
	private final String UUID;
	// the dataset name
	protected String name;
	// the dataset description
	protected String description;
	// the headers of the dataset
	protected ArrayList<String> headers;
	// the rows of data
	protected ArrayList<Row> rows;

	/**
	 * Creates the dataset given the UUID, name, and desc.
	 */
	Dataset(String UUID, String name, String description) {
		this.UUID = UUID;
		this.name = name;
		this.description = description;
	}
	/**
	 * Creates an empty dataset given an owner, name, and description.
	 */
	public Dataset(User owner, String name, String description) {
		this.name = name;
		this.description = description;
		headers = new ArrayList<String>(/* 16 */);
		rows = new ArrayList<Row>(/* 16 */);
		UUID = Utils.uuid();
	}
	/**
	 * Returns this dataset's UUID.
	 */
	public String getUUID() {
		return UUID;
	}
	/**
	 * Returns the name of this dataset.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of this dataset.
	 */
	public Dataset setName(String name) {
		this.name = name;
		return this;
	}
	/**
	 * Returns the description of the dataset.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Sets the description of this dataset.
	 */
	public Dataset setDescription(String description) {
		this.description = description;
		return this;
	}
	/**
	 * Returns the headers for this dataset.
	 */
	public ArrayList<String> getHeaders() {
		return headers;
	}
	/**
	 * Sets the heders for the dataset.
	 */
	public Dataset setHeaders(String ... headers) {
		for (String header : headers) {
			this.headers.add(header);
		}
		return this;
	}
	/**
	 * Returns the rows of this dataset.
	 */
	public ArrayList<Dataset.Row> getRows() {
		return rows;
	}
	/**
	 * Returns the values in a particular row.
	 */
	public Dataset.Row getRow(int y) {
		if (y >= rows.size()) {
			return null;
		}
		return rows.get(y);
	}
	/**
	 * Clears all rows in the table to set the given rows.
	 */
	public Dataset setRows(Dataset.Row ... rows) {
		this.rows = new ArrayList<Dataset.Row>(rows.length);
		for (Dataset.Row row : rows) {
			if (row.getWidth() < headers.size()) {
				return null;
			}
			this.rows.add(row);
		}
		return this;
	}
	/**
	 * Adds the specified rows to the dataset.
	 */
	public Dataset addRows(Dataset.Row ... rows) {
		for (Dataset.Row row : rows) {
			if (row.getWidth() < headers.size()) {
				return null;
			}
			this.rows.add(row);
		}
		return this;
	}
	/**
	 * Returns the length of this dataset.
	 */
	public int rowCount() {
		return rows.size() + 1;
	}
	/**
	 * Returns the width of the dataset.
	 */
	public int width() {
		return headers.size();
	}

}
