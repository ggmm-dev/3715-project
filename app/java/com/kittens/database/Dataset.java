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

	// the owner/manager of the dataset
	protected User owner;
	// the dataset name
	protected String name;
	// the dataset description
	protected String description;
	// the list of collaborators
	protected ArrayList<User> collaborators;
	// the rows of data
	// the first (0th) row will be the headers
	protected ArrayList<Row> rows;
	// uuid
	private final String UUID;

	/**
	 * Creates an empty dataset given an owner, name, and description.
	 */
	public Dataset(User owner, String name, String description) {
		this.owner = owner;
		this.name = name;
		this.description = description;
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
	 * Returns the owner of this dataset.
	 */
	public User getOwner() {
		return owner;
	}
	/**
	 * Sets the owner of the dataset.
	 */
	public Dataset setOwner(User owner) {
		this.owner = owner;
		return this;
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
	 * Adds the specified rows to the dataset.
	 */
	public Dataset addRows(Dataset.Row ... rows) {
		for (Dataset.Row row : rows) {
			this.rows.add(row);
		}
		return this;
	}
	/**
	 * Clears all rows in the table to set the given rows.
	 */
	public Dataset setRows(Dataset.Row ... rows) {
		this.rows = new ArrayList<Dataset.Row>(rows.length);
		for (Dataset.Row row : rows) {
			this.rows.add(row);
		}
		return this;
	}
	/**
	 * Returns the width of this dataset.
	 */
	public int rowCount() {
		return rows.size();
	}

}
