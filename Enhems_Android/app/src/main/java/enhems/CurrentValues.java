/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.content.Context;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Stjepan
 */
public class CurrentValues {

        /**
         * @param context application context
         * @return String array of current room data
         */
        public static String[] GetData(Context context) {
            HttpClient httpclient = AppHttpClient.GetInstance(context);
            HttpGet request = new HttpGet(context.getString(R.string.root) + "Current?token="+Token.get(context));
            try {
                HttpResponse response = httpclient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String data = URLDecoder.decode(EntityUtils.toString(response.getEntity(), Charset.defaultCharset().name()), "UTF-8");
                    return data.split("&");
                }
            } catch (IOException ex) {
                Logger.getLogger(CurrentValues.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new String[]{"---", "---", "---", "---", "---", "---", "---"};
        }
    }
