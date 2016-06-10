/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Model;

import java.util.LinkedList;
import java.util.List;

import dao.SQLDao;
import dao.models.User;

/**
 *
 * @author Stjepan
 */
public class TokenRep {

    /**
     * Adds new user to repository or replaces the user with the same name
     *
     * @param newUser user to be added or replaced
     */
    public static void Add(Token token) {
        rep.add(token);
    }

    /**
     * Remove user from repository
     *
     * @param user user to be removed
     */
    public static void Remove(Token token) {
        rep.remove(token);
    }

    /**
     * @param token token to search users by
     * @return user if there is one with such token, null otherwise
     */
    public static Token Find(String tokenString) {
        for (Token token : rep) {
            if (token.getToken().equals(tokenString)) {
                return token;
            }
        }
        return null;
    }
    
    public static User FindByToken(String tokenString) {
        if (tokenString==null || tokenString.isEmpty()) {
            return null;
        }
        Token token = Find(tokenString);
        if (token==null) {
            return null;
        }
        return SQLDao.getUser(token.getUserID());
    }

    /**
     * Checks if there are users whose login has expired
     */
    public static void ExpirationCheck() {
        List<Token> removeList = new LinkedList<>();
        rep.stream().filter((token) -> token.isExpired()).forEach((token) -> {
            removeList.add(token);
        });
        if (removeList.size() > 0) {
            removeList.stream().forEach((token) -> {
                rep.remove(token);
            });
        }
        removeList.clear();
    }
    
    private static final List<Token> rep = new LinkedList<>();
}
