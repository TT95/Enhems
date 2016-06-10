/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.models;

/**
 *
 * @author Stjepan
 */
public class User {

    /**
     * Constructor, set attributes
     *
     * @param name user name
     * @param userID user ID
     * @param roomID room ID
     * @param roomName room name
     */
    public User(String name, int userID, int roomID, String roomName) {
        this.name = name;
        this.userID = userID;
        this.roomID = roomID;
        this.roomName = roomName;
    }

    private String name;
    private int userID;
    private int roomID;
    private String roomName;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return the roomID
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * @param roomID the roomID to set
     */
    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    /**
     * @return the roomName
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * @param roomName the roomName to set
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    
    @Override
    public String toString() {
    	return name;
    }
}
