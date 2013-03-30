package com.kittens.database;

import com.google.gson.annotations.Expose;

import com.kittens.BCrypt;
import com.kittens.Utils;

import java.lang.Object;
import java.lang.String;

public class User extends Object {

	// fields
	@Expose private String username;
	@Expose private String email;
	private String password;
	private boolean isAdmin;
	@Expose private final String UUID;

	/**
	 * Creates a user with the given values.
	 */
	public User(String username, String email, String password, boolean isAdmin) {
		this.username = username;
		this.email = email;
		this.password = BCrypt.hashpw(password, BCrypt.gensalt(/* work factor = 10 */));
		this.isAdmin = isAdmin;
		UUID = Utils.uuid();
	}
	/**
	 * Creates a user with the given values for the fields.
	 * CAUTION! Passing {@code false} for hash results in the password not being hashed.
	 */
	User(String UUID, String username, String email, String password, boolean isAdmin) {
		this.UUID = UUID;
		this.username = username;
		this.email = email;
		this.password = password;
		this.isAdmin = isAdmin;
	}
	/**
	 * Returns this dataset's UUID.
	 */
	public String getUUID() {
		return UUID;
	}
	/**
	 * Returns the user's name.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Sets the username to what's given.
	 */
	public User setUsername(String username) {
		this.username = username;
		return this;
	}
	/**
	 * Returns the user's email address.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * Sets the email for the user.
	 */
	public User setEmail(String email) {
		this.email = email;
		return this;
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
	/**
	 * Makes this user an administrator.
	 */
	public User makeAdmin() {
		isAdmin = true;
		return this;
	}

}
