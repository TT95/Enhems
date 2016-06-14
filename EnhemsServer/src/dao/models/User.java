/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.models;

import java.util.Set;

/**
 *
 * @author TeoToplak 
 */
public class User {
	
	
	private String name;
	private int userID;
	private Set<Unit> rooms;
	
	
	public User(String name, int userID, Set<Unit> rooms) {
		super();
		this.name = name;
		this.userID = userID;
		this.rooms = rooms;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public Set<Unit> getRooms() {
		return rooms;
	}
	public void setRooms(Set<Unit> rooms) {
		this.rooms = rooms;
	}
	
	
	
	

}
