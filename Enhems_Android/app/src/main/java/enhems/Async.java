/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.app.ProgressDialog;

/**
 *
 * @author Stjepan
 */
public class Async {

    /**
     * Start async thread with dialog
     *
     * @param message message to be displayed in dialog
     * @param thread thread to be started
     * @param dialog progressDialog
     */
    public static void StartWithDialog(String message, Thread thread, ProgressDialog dialog) {
        dialog.setMessage(message);
        dialog.show();
        thread.start();
    }

}
