/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Model;

/**
 *
 * @author Stjepan
 */
public class Token {
    
    private String token;
    private int userID;
    private final long created;
    
    public Token(String token, int userID){
        this.token=token;
        this.userID=userID;
        this.created=System.currentTimeMillis();
    }

    public boolean isExpired(){
        return System.currentTimeMillis()>=this.created+24 * 3600 * 1000;
    }
    
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
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
    
}
