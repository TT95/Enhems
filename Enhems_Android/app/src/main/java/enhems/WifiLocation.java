package enhems;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by teo on 6/2/17.
 */
public class WifiLocation {
    /**
     * @param context application context
     * @param rsss json with pairs BSSID - RSS
     * @return String array of current room data
     */
    public static String GetData(Context context, JSONObject rsss) {
        System.out.println(android.os.Process.getThreadPriority(android.os.Process.myTid()) + " INSTANCE");
        HttpClient httpclient = AppHttpClient.GetInstance(context);
        HttpPost request = new HttpPost(context.getString(R.string.root) + "Location");
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("rsss", rsss.toString()));
        params.add(new BasicNameValuePair("token", Token.get(context)));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        System.out.println(android.os.Process.getThreadPriority(android.os.Process.myTid()) + " ENTER");
            HttpResponse response = httpclient.execute(request);
        System.out.println(android.os.Process.getThreadPriority(android.os.Process.myTid()) + " EXIT");
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String room = URLDecoder.decode(EntityUtils.toString(response.getEntity(), Charset.defaultCharset().name()), "UTF-8");
        System.out.println(android.os.Process.getThreadPriority(android.os.Process.myTid()) + " RESULT " + room);
                return room;
            }
        } catch (IOException ex) {
            ex.getStackTrace();
            Logger.getLogger(CurrentValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";

    }
}
