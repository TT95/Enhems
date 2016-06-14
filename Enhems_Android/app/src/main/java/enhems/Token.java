/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *
 * @author Stjepan
 */
public class Token {

    public static String get(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("ENHEMS", Context.MODE_PRIVATE);
        return sharedPref.getString("enhems_token", null);
    }

    public static void set(Context context, String token) {
        Editor editor = context.getSharedPreferences("ENHEMS", Context.MODE_PRIVATE).edit();
        editor.putString("enhems_token", token);
        editor.commit();
    }
}
