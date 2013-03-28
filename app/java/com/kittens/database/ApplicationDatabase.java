package com.kittens.database;

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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
		boolean needsDefaultAdministrator = false;
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
			if (!r.next()) {
				// the users table has not been created
				// we will create it now, but while doing
				// so we need to add the default admin
				needsDefaultAdministrator = true;
			}
			// prepare the database with tables
			s = database.createStatement();
			// s.executeUpdate("PRAGMA foreign_keys = ON;");
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS users(%s, %s, %s, %s, %s);",
				"id INTEGER PRIMARY KEY",
				"name TEXT NOT NULL UNIQUE",
				"email TEXT NOT NULL UNIQUE",
				"password TEXT NOT NULL",
				"admin BOOLEAN NOT NULL"
			));
			if (needsDefaultAdministrator) {
				p = database.prepareStatement(String.format(
					"INSERT INTO %s %s;",
					"users(name, email, password, admin)",
					"VALUES(?, ?, ?, ?)"
				));
				User defaultAdmin = new User(
					"root", "root@root", "$2a$10$q9yRNn2oxFY6hXnxBhRKGu1tMqYSpBh8cLpbRW8PyAjIdaP8qEnf2", true, false
				);
				p.setString(1, defaultAdmin.getUsername());
				p.setString(2, defaultAdmin.getEmail());
				p.setString(3, defaultAdmin.getPassword());
				p.setBoolean(4, defaultAdmin.isAdmin());
				p.executeUpdate();
			}
			// add some more tables
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS datasets(%s, %s, %s);",
				"id INTEGER PRIMARY KEY",
				"name TEXT NOT NULL",
				"desc TEXT NOT NULL"
			));
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS fields(%s, %s, %s, %s, %s);",
				"id INTEGER PRIMARY KEY",
				"did INTEGER NOT NULL",
				"name TEXT NOT NULL",
				"value TEXT NOT NULL",
				"FOREIGN KEY (did) REFERENCES datasets(id)"
			));
			s.executeUpdate(String.format(
				"CREATE TABLE IF NOT EXISTS owners(%s, %s, %s, %s);",
				"uid INTEGER NOT NULL",
				"did INTEGER NOT NULL",
				"FOREIGN KEY (uid) REFERENCES users(id)",
				"FOREIGN KEY (did) REFERENCES datasets(id)"
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
			PreparedStatement ps = database.prepareStatement("INSERT INTO users(name, email, password, admin) VALUES(?, ?, ?, ?);");
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPassword());
			ps.setBoolean(4, user.isAdmin());
			ps.executeUpdate();
			ps.close();
		}
		finally {
			closeConnection();
		}
	}
	/**
	 * Returns the id for a user.
	 */
	public long getIdForUser(User user) throws SQLException {
		long id = -42; // not in database
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement("SELECT id FROM users WHERE email = ?;");
			ps.setString(1, user.getEmail());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			rs.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
		return id;
	}
	/**
	 * Returns a list of all the users in the database.
	 */
	public ArrayList<String> getAllUsers() throws SQLException {
		ArrayList<String> usernames = new ArrayList<String>();
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement("SELECT name FROM users;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) usernames.add(rs.getString(1));
			rs.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
		return usernames;
	}
	/**
	 * Returns whether the given username is
	 * already in use in the database.
	 */
	public boolean usernameAlreadyExists(String username) throws SQLException {
		return getAllUsers().contains(username);
	}
	/**
	 * Returns whether the given username is
	 * already in use in the database.
	 */
	public boolean emailAlreadyExists(String email) throws SQLException {
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
	 * Returns the user with the given credientials if they exist,
	 * @return null if the user does not exist
	 */
	public User getUserWithCredentials(String email, String password) throws SQLException {
		User user = null;
		try {
			openConnection();
			PreparedStatement ps = database.prepareStatement("SELECT name, email, password, admin FROM users WHERE email = ?;");
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			// the email should be unique
			// and thus the result set should
			// at max contain one result
			while (rs.next()) user = new User(
			                                    /* username */ rs.getString(1),
			                                    /* email */ rs.getString(2),
			                                    /* password */ rs.getString(3),
			                                    /* is admin */ rs.getBoolean(4),
			                                    /* hash? */ false
			);
			rs.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
		return (user != null && BCrypt.checkpw(password, user.getPassword())) ? user : null;
	}

}
