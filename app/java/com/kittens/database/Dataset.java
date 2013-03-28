package com.kittens.database;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;

public class Dataset extends Object {

	/**
	 * A row in a dataset.
	 */
	public static class Row extends Object {

		// the size of a row
		// i.e. the number of entries/columns
		private int size;
		// the array of values in this row
		private ArrayList<String> values;

		/**
		 * Creates a row with the given values,
		 * setting size to the appropriate number.
		 */
		public Row(String ... values) {
			size = values.length;
			this.values = new ArrayList<String>(size);
			for (String value : values) {
				this.values.add(value);
			}
		}
		/**
		 * Returns the size of the row.
		 */
		public int getSize() {
			return size;
		}
		/**
		 * Sets the values contained in the row.
		 */
		public void setValues(String ... values) {
			if (size < values.length) {
				size = values.length;
			}
			for (String value : values) {
				this.values.add(value);
			}
		}
		/**
		 * Returns the values in this row.
		 */
		public ArrayList<String> getValues() {
			return values;
		}

	}

	// the owner/manager of the dataset
	protected User owner;
	// the dataset name
	// (project name)
	protected String name;
	// the dataset description
	protected String description;
	// the list of collaborators
	protected ArrayList<User> collaborators;
	// the rows of data
	// the first (0th) row will be the headers
	protected ArrayList<Row> rows;

	/**
	 * No args constructor.
	 */
	Dataset() {
		// empty
	}
	/**
	 * Creates an empty dataset given an owner, name, and description.
	 */
	public Dataset(User owner, String name, String description) {
		this.owner = owner;
		this.name = name;
		this.description = description;
	}
	/**
	 * Returns the owner of this dataset.
	 */
	public User getOwner() {
		return owner;
	}
	/**
	 * Returns the name of this dataset.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns the description of the dataset.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Sets the owner of the dataset.
	 */
	public void setOwner(User newOwner) {
		owner = newOwner;
	}
	/**
	 * Sets the name of this dataset.
	 */
	public void setNam(String newName) {
		name = newName;
	}
	/**
	 * Sets the description of the dataset.
	 */
	public void setDescription(String newDescription) {
		description = newDescription;
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
		return rows.get(y);
	}
	/**
	 * Adds the specified rows to the dataset.
	 */
	public void addRows(Dataset.Row ... rows) {
		for (Dataset.Row row : rows)
			this.rows.add(row);
	}
	/**
	 * Returns the width of this dataset.
	 */
	public int width() {
		int w = 0;
		for (Dataset.Row row : rows) {
			w = (row.getSize() > w) ? row.getSize() : w;
		}
		return w;
	}

}
