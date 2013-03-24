package com.kittens.database;

import com.kittens.BCrypt;

import java.io.File;
import java.lang.Class;
import java.lang.Object;
import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ApplicationDatabase extends Object {

	/**
	 * Has the database module been loaded successfully?
	 */
	private static boolean loaded = false;
	/**
	 * Try to load the database class...
	 */
	static {
		try {
			Class.forName("org.sqlite.JDBC");
			loaded = true;
		}
		catch (Exception e) {
			loaded = false;
		}
	}
	/**
	 * A string.
	 */
	private final String DB_NAME = "kittens.sqlite3";
	/**
	 * The path to the directory containing the database.
	 */
	private final String DB_PATH;
	/**
	 * The connection to the app's database.
	 */
	private Connection db = null;

	/**
	 * Opens a connection (if possible) to the database.
	 */
	private void openConnection() throws SQLException {
		db = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
	}
	/**
	 * Closes the open connection (if exists) to the database.
	 */
	private void closeConnection() throws SQLException {
		if (db == null) return;
		db.close();
	}
	/**
	 * Connects to, or creates an application database.
	 */
	public ApplicationDatabase(String webPath) throws SQLException {
		DB_PATH = webPath + File.separator + DB_NAME;
		Statement s = null;
		try {
			openConnection();
			s = db.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL);");
			s.close();
		}
		finally {
			// cleanup all the things
			if (s != null) {
				s.close();
			}
			closeConnection();
		}
	}
	/**
	 * Adds the given user to the database.
	 */
	public boolean createUser(User user) throws SQLException {
		try {
			openConnection();
			PreparedStatement ps = db.prepareStatement("INSERT INTO users(username, email, password) VALUES(?, ?, ?);");
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPassword());
			ps.executeUpdate();
			ps.close();
			return true;
		}
		finally {
			closeConnection();
		}
	}
	/**
	 *
	 */
	public ArrayList<String> getAllUsers() throws SQLException {
		ArrayList<String> usernames = new ArrayList<String>();
		try {
			openConnection();
			PreparedStatement ps = db.prepareStatement("select username from users;");
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
			PreparedStatement ps = db.prepareStatement("select email from users;");
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
			PreparedStatement ps = db.prepareStatement("select username, email, password from users where email = ?");
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			// the email should be unique
			// and thus the result set should
			// at max contain one result
			while (rs.next()) user = new User(/* username */ rs.getString(1), /* email */ rs.getString(2), /* password */ rs.getString(3), /* is admin */ false, /* hash? */ false);
			rs.close();
			ps.close();
		}
		finally {
			closeConnection();
		}
		return (user != null && BCrypt.checkpw(password, user.getPassword())) ? user : null;
	}

}
