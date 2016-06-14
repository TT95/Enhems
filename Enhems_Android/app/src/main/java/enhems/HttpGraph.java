/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.content.Context;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Stjepan
 */
public class HttpGraph {

    /**
     * Gets graph defined in the request and saves it to filename/path
     *
     * @param request httpRequest to be executed
     * @param filename filename path to save graph
     * @param context application context
     * @return error message or null if no error
     */
    public static String Get(HttpUriRequest request, String filename, Context context) {
        HttpClient httpclient = AppHttpClient.GetInstance(context);
        try {
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                byte[] image = EntityUtils.toByteArray(response.getEntity());
                FileOutputStream file = context.openFileOutput(filename, Context.MODE_PRIVATE);
                file.write(image);
                file.close();
            } else if (statusCode == 404) {
                return "Nema podataka za prikazati";
            } else {
                return "Greška na poslužitelju";
            }
        } catch (IOException ex) {
            Logger.getLogger(CurrentActivity.class.getName()).log(Level.SEVERE, null, ex);
            return "Neuspješno spajanje";
        }
        return null;
    }
}
