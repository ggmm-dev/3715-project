package com.kittens.database;

import com.kittens.BCrypt;

import java.lang.Object;
import java.lang.String;

public class User extends Object {

	// fields
	private String username;
	private String email;
	private String password;
	private boolean isAdmin;

	/**
	 * Creates a user with the given values.
	 */
	public User(String username, String email, String password, boolean isAdmin) {
		this(username, email, password, isAdmin, true);
	}
	/**
	 * Creates a user with the given values for the fields.
	 * CAUTION! Passing {@code false} for hash results in the password not being hashed.
	 */
	User(String username, String email, String password, boolean isAdmin, boolean hash) {
		this.username = username;
		this.email = email;
		this.password = (hash) ? BCrypt.hashpw(password, BCrypt.gensalt(/* work factor = 10 */)) : password;
		this.isAdmin = isAdmin;
	}
	/**
	 * Returns the user's name.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Returns the user's email address.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * Returns the user's password (hashed).
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Returns whether or not the user is an administrator.
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

}
