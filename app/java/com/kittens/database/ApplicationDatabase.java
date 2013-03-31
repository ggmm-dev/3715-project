package com.kittens.database;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import com.kittens.BCrypt;

import java.io.File;
import java.lang.Class;
import java.lang.Object;
import java.lang.String;
import java.lang.Exception;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class ApplicationDatabase extends Object {

	// has the database module been loaded successfully?
	private static boolean loaded = false;
	// try to load the database class
	static {
		try {
			Class.forName("org.sqlite.JDBC");
			loaded = true;
		}
		catch (Exception e) {
			loaded = false;
		}
	}
	// the name of the database file
	private final String DB_NAME = "kittens.sqlite3";
	// the path to the directory containing the database
	private final String DB_PATH;
	// the connection to the database
	private Connection database = null;

	/**
	 * Opens a connection (if possible) to the database.
	 */
	private void openConnection() throws SQLException {
		database = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
	}
	/**
	 * Closes the open connection (if exists) to the database.
	 */
	private void closeConnection() throws SQLException {
		if (database == null) {
			return;
		}
		database.close();
	}
	/**
	 * Connects to, or creates an application database.
	 */
	public ApplicationDatabase(String path) throws SQLException {
		DB_PATH = path + File.separator + DB_NAME;
		Statement s = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			openConnection();
			// check to see if the `users` table exists
			p = database.prepareStatement("SELECT name FROM sqlite_master WHERE type = ? AND name = ?;");
			p.setString(1, "table");
			p.setString(2, "users");
			r = p.executeQuery();
			if (r.next()) {
				// the users table has been created
				// assume all table have too
				return;
			}
			// prepare the database with tables
			s = database.createStatement();
			// s.executeUpdate("PRAGMA foreign_keys = ON;");
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS users(%s, %s, %s, %s, %s, %s);",
				"id INTEGER PRIMARY KEY",
				"uuid TEXT NOT NULL UNIQUE",
				"name TEXT NOT NULL",
				"email TEXT NOT NULL UNIQUE",
				"password TEXT NOT NULL",
				"admin BOOLEAN NOT NULL"
			));
			p = database.prepareStatement(String.format(
				"INSERT INTO %s %s;",
				"users(uuid, name, email, password, admin)",
				"VALUES(?, ?, ?, ?, ?)"
			));
			User defaultAdmin = new User("Admin", "admin@localhost", "password", /* is admin */ true);
			p.setString(1, defaultAdmin.getUUID());
			p.setString(2, defaultAdmin.getUsername());
			p.setString(3, defaultAdmin.getEmail());
			p.setString(4, defaultAdmin.getPassword());
			p.setBoolean(5, defaultAdmin.isAdmin());
			p.executeUpdate();
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS datasets(%s, %s, %s, %s, %s);",
				"id INTEGER PRIMARY KEY",
				"uuid TEXT NOT NULL UNIQUE",
				"name TEXT NOT NULL",
				"desc TEXT NOT NULL",
				"created INTEGER NOT NULL"
			));
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS access(%s, %s, %s, %s);",
				"id INTEGER PRIMARY KEY",
				"user TEXT NOT NULL",
				"dataset TEXT NOT NULL",
				"owner BOOLEAN NOT NULL"
			));
			s.executeUpdate(String.format(
				"CREATE UNIQUE INDEX %s ON %s;",
				"user_dataset",
				"access(user, dataset)"
			));
		}
		finally {
			if (s != null) {
				s.close();
			}
			if (p != null) {
				p.close();
			}
			if (r != null) {
				r.close();
			}
			closeConnection();
		}
		return;
	}
	/**
	 * Adds the given user to the database.
	 */
	public void createUser(User user) throws SQLException {
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"INSERT INTO %s %s;",
				"users(uuid, name, email, password, admin)",
				"VALUES(?, ?, ?, ?, ?)"
			));
			ps.setString(1, user.getUUID());
			ps.setString(2, user.getUsername());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPassword());
			ps.setBoolean(5, user.isAdmin());
			ps.executeUpdate();
			ps.close();
		}
		finally {
			closeConnection();
		}
	}
	/**
	 * Returns the user with the given UUID.
	 */
	private User getUserForUUID(final String UUID) throws SQLException {
		// no need to open connection
		// or close connection, as this
		// will be called once one has
		// already been established
		PreparedStatement ps = database.prepareStatement(String.format(
			"SELECT %s FROM %s WHERE %s;",
			"name, email, password, admin",
			"users",
			"uuid = ?"
		));
		ps.setString(1, UUID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return new User(UUID, rs.getString(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
		}
		return null;
	}
	/**
	 * Returns the user with the given email address.
	 */
	public User getUserForEmail(final String email) throws SQLException {
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"SELECT %s FROM %s WHERE %s;",
				"uuid, name, email, password, admin",
				"users",
				"email = ?"
			));
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5));
			}
		}
		finally {
			closeConnection();
		}
		return null;
	}
	/**
	 * Returns whether the given email address is already in use in the database.
	 */
	public boolean emailInDatabase(String email) throws SQLException {
		ArrayList<String> emails = new ArrayList<String>();
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement("SELECT email FROM users;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) emails.add(rs.getString(1));
			rs.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
		return emails.contains(email);
	}
	/**
	 * Returns the user matching the given credientials if they exist.
	 * @return null if the user does not exist
	 */
	public User getUserWithCredentials(String email, String password) throws SQLException {
		User user = null;
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"SELECT uuid, name, email, password, admin FROM users WHERE email = ?;"
			));
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5));
			}
			rs.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
		return (user != null && BCrypt.checkpw(password, user.getPassword())) ? user : null;
	}
	/**
	 * Returns the dataset that matches the given id.
	 */
	public Dataset getDataset(String datasetUUID) throws SQLException {
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"SELECT %s FROM %s WHERE %s",
				"name, desc, created",
				"datasets",
				"uuid = ?"
			));
			ps.setString(1, datasetUUID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Dataset dataset = new Dataset(
					datasetUUID,
					rs.getString(1),
					rs.getString(2),
					new Date(rs.getLong(3))
				);
				populateDataset(dataset);
				return dataset;
			}
			return null;
		}
		finally {
			closeConnection();
		}
	}
	/**
	 * Adds the given dataset into the database, setting the user as the owner.
	 */
	public void addDataset(final User user, final Dataset dataset) throws SQLException {
		final int width = dataset.getWidth();
		PreparedStatement ps;
		Statement s;
		try {
			openConnection();
			// insert the dataset into the datasets table
			ps = database.prepareStatement(String.format(
				"INSERT INTO %s %s;",
				"datasets(uuid, name, desc, created)",
				"VALUES(?, ?, ?, ?)"
			));
			ps.setString(1, dataset.getUUID());
			ps.setString(2, dataset.getName());
			ps.setString(3, dataset.getDescription());
			ps.setLong(4, dataset.getDateOfCreation().getTime());
			ps.executeUpdate();
			// create the table for the rows
			s = database.createStatement();
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS [%s](%s);",
				dataset.getUUID(),
				// join together the headers from the dataset
				Joiner.on(" TEXT NOT NULL, ").join(dataset.getHeaders()) + " TEXT NOT NULL"
			));
			// add the row data
			final ArrayList<Dataset.Row> rows = dataset.getRows();
			for (Dataset.Row row : rows) {
				final String[] values = row.getValues();
				ps = database.prepareStatement(String.format(
					"INSERT INTO [%s] %s;",
					dataset.getUUID(),
					"VALUES(" + Joiner.on(",").join(Strings.repeat("?", width).split("(?!^)")) + ")"
				));
				for (int i = 1; i <= width; i++) {
					ps.setString(i, values[i - 1]);
				}
				ps.executeUpdate();
			}
			// grant access and ownership to the dataset
			ps = database.prepareStatement(String.format(
				"INSERT INTO %s %s;",
				"access(user, dataset, owner)",
				"VALUES(?, ?, ?)"
			));
			ps.setString(1, user.getUUID());
			ps.setString(2, dataset.getUUID());
			ps.setBoolean(3, true);
			ps.executeUpdate();
			s.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
	}
	/**
	 * Reconstructs and returns all the datasets accessible by the give user.
	 */
	public ArrayList<Dataset> getDatasetsForUser(final User user) {
		ArrayList<Dataset> datasets = new ArrayList<Dataset>();
		final String userUUID = user.getUUID();
		try {
			PreparedStatement ps;
			ResultSet r1, r2;
			openConnection();
			// get all the dataset the user has access to
			ps = database.prepareStatement(String.format(
				"SELECT %s FROM %s WHERE %s;",
				"user, dataset, owner",
				"access",
				"user = ?"
			));
			ps.setString(1, userUUID);
			r1 = ps.executeQuery();
			while (r1.next()) {
				// get the metadata for the current dataset
				ps = database.prepareStatement(String.format(
					"SELECT %s FROM %s WHERE %s;",
					"name, desc, created",
					"datasets",
					"uuid = ?"
				));
				final String datasetUUID = r1.getString(2);
				ps.setString(1, datasetUUID);
				r2 = ps.executeQuery();
				while (r2.next()) {
					// create and add the dataset with the proper metadata
					datasets.add(
						new Dataset(
							datasetUUID,
							r2.getString(1),
							r2.getString(2),
							new Date(r2.getLong(3))
						)
					);
				}
			}
			// fill the datasets with their data
			final int numberOfDatasets = datasets.size();
			for (int i = 0; i < numberOfDatasets; i++) {
				populateDataset(datasets.get(i));
			}
			ps.close();
			closeConnection();
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			return null;
		}
		return datasets;
	}
	/**
	 * Updates the name and description of the dataset.
	 */
	public void updateDatasetMetadata(String UUID, String name, String description) {
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"UPDATE %s SET %s WHERE %s;",
				"datasets",
				"name = ?, desc = ?",
				"uuid = ?"
			));
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setString(3, UUID);
			ps.executeUpdate();
			ps.close();
			closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Populates the given dataset with its headers and rows.
	 */
	private void populateDataset(Dataset dataset) throws SQLException {
		ArrayList<String> headers = new ArrayList<String>();
		String uuid = dataset.getUUID();
		PreparedStatement ps = database.prepareStatement(String.format(
			"SELECT * FROM [%s];",
			uuid
		));
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		// the headers
		int numberOfColumns = rsmd.getColumnCount();
		for (int j = 1; j <= numberOfColumns; j++) {
			// add the headers
			headers.add(rsmd.getColumnName(j));
		}
		dataset.setHeaders(headers);
		// the data
		while (rs.next()) {
			// populate the rows
			ArrayList<String> values = new ArrayList<String>();
			for (int j = 1; j <= numberOfColumns; j++) {
				values.add(rs.getString(j));
			}
			dataset.addRows(new Dataset.Row(values));
		}
		// add the collaborators back
		ps = database.prepareStatement(String.format(
			"SELECT %s FROM %s WHERE %s;",
			"user, owner",
			"access",
			"dataset = ?"
		));
		ps.setString(1, uuid);
		rs = ps.executeQuery();
		while (rs.next()) {
			User user = getUserForUUID(rs.getString(1));
			if (rs.getBoolean(2)) {
				dataset.setOwner(user);
				continue;
			}
			dataset.addCollaborators(user);
		}
		rs.close();
		ps.close();
	}
	/**
	 * Drop and re-adds the whole database.
	 * This is probably SUPER INEFFICIENT.
	 */
	public void dropCreateDataset(String uuid, ArrayList<String> headers, ArrayList<Dataset.Row> rows) throws SQLException {
		final int width = headers.size();
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"DROP TABLE [%s];", uuid
			));
			ps.executeUpdate();
			Statement s = database.createStatement();
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS [%s](%s);",
				uuid,
				// join together the headers from the dataset
				Joiner.on(" TEXT NOT NULL, ").join(headers) + " TEXT NOT NULL"
			));
			// add the row data
			for (Dataset.Row row : rows) {
				final String[] values = row.getValues();
				ps = database.prepareStatement(String.format(
					"INSERT INTO [%s] %s;",
					uuid,
					"VALUES(" + Joiner.on(",").join(Strings.repeat("?", width).split("(?!^)")) + ")"
				));
				for (int i = 1; i <= width; i++) {
					ps.setString(i, values[i - 1]);
				}
				ps.executeUpdate();
			}
			s.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
	}
	/**
	 * Grants the given user access to the dataset.
	 */
	public void addCollaborator(String userUUID, String datasetUUID) throws SQLException {
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement(String.format(
				"INSERT INTO %s %s;",
				"access(user, dataset, owner)",
				"VALUES(?, ?, ?)"
			));
			ps.setString(1, userUUID);
			ps.setString(2, datasetUUID);
			ps.setBoolean(3, false);
			ps.executeUpdate();
			ps.close();
		}
		finally {
			closeConnection();
		}
	}

}
