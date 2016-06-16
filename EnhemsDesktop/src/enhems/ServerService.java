	package enhems;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import enhems.components.ImagePanel;

public class ServerService {
	
//	private static String serverRoot = "https://192.168.0.16:8443/AppBE/";
//	private static String serverRoot = "http://192.168.10.92:8080/AppBE/";
	private static String serverRoot = "https://161.53.68.191:8443/EnhemsServer/";

	public static void executeRequest(ServerRequest request) {
		new Thread(()-> {
//			System.out.println("krenuo "+request);
			request.execute();
//			System.out.println("zavrsio "+request);
			SwingUtilities.invokeLater(()-> {
				request.afterExecution();
			});
		}).start();
	}
	
    public static String FCspeed(String fcspeed, final String preFCspeed, String room) {
        HttpClient httpclient = AppHttpClient.GetInstance();
        HttpPost request = new HttpPost(serverRoot + "FCspeed");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("fcspeed", fcspeed));
        params.add(new BasicNameValuePair("token", Token.getToken()));
        params.add(new BasicNameValuePair("room", room));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(request);
            EntityUtils.consume(response.getEntity()); // to deallocate connection
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
            	request.releaseConnection();
                return null;//sucess
            }
        } catch (IOException ex) {
        	MyLogger.log("Error setting FCSpeed on server", ex);
        }
    	request.releaseConnection();
        return preFCspeed;
    }
    
    public static String Setpoint(String setpoint, final String preSetPoint, String room) {
        HttpClient httpclient = AppHttpClient.GetInstance();
        HttpPost request = new HttpPost(serverRoot + "Setpoint");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("setpoint", setpoint));
        params.add(new BasicNameValuePair("token", Token.getToken()));
        params.add(new BasicNameValuePair("room", room));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(request);
            EntityUtils.consume(response.getEntity()); // to deallocate connection
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
            	request.releaseConnection();
                return null;//sucess
            }
        } catch (IOException ex) {
        	MyLogger.log("Error setting setpoint on server", ex);
        }
    	request.releaseConnection();

        return preSetPoint;
    }
	
	
    /**
     * @param context application context
     * @return String array of current room data
     */
    public static String[] GetCurrentValuesData(String room) {
        HttpClient httpclient = AppHttpClient.GetInstance();
        
        HttpGet request = new HttpGet(serverRoot + "Current?token="+Token.getToken()+ "&room="+room);
        try {
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String data = URLDecoder.decode(EntityUtils.toString(response.getEntity(),
            		Charset.defaultCharset().name()), "UTF-8");
            if (statusCode == 200) {
            	request.releaseConnection();
                return data.split("&");
            }
        } catch (IOException ex) {
        	MyLogger.log("Error getting current values from server", ex);
        }
    	request.releaseConnection();
        return new String[]{"---", "---", "---", "---", "---", "---", "---", "---"};
    }
    
    /**
     * 
     * @return All unti names avaliable to user in String array
     */
    public static String[] GetAssignedUnits() {
        HttpClient httpclient = AppHttpClient.GetInstance();
        HttpGet request = new HttpGet(serverRoot + "Units?token="+Token.getToken());
        try {
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String data = URLDecoder.decode(EntityUtils.toString(response.getEntity(),
            		Charset.defaultCharset().name()), "UTF-8");
            if (statusCode == 200) {
            	request.releaseConnection();
                return data.split("&");
            }
        } catch (IOException ex) {
        	MyLogger.log("Error getting current values from server", ex);
        }
    	request.releaseConnection();
        return null;
    }
    
	/**
	 * User login
	 *
	 * @param attributes attributes used for login, either username and password
	 * or token (give null when loging with token)
	 * @return error message or null if no error
	 */
	public static int Login(String[] attributes) {
		String username;
		//should be protected client
		HttpClient httpclient = AppHttpClient.GetInstance();
		HttpPost request = new HttpPost(serverRoot + "Login");
		List<BasicNameValuePair> params = new ArrayList<>();
		if (attributes != null && attributes.length == 2) {
			username = attributes[0];
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("pass", attributes[1]));
		} else {
			params.add(new BasicNameValuePair("token", Token.getToken()));
			username = Token.getUsername();
		}
		try {
			request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpclient.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			String token = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				Token.setToken(token, username);
			}
			request.releaseConnection();
			return statusCode;

		} catch (IOException ex) {
			
			MyLogger.log("Error doing login on the server", ex);
			if(ex instanceof ConnectException) {
				Utilities.showErrorDialog("Error", "Connection timed out.\nServer could be offline.", null, ex);
				System.exit(1);
			}
		}
		request.releaseConnection();
		return -1;
	}
	
    /**
     * @param measure measure for which to get graphs
     * @return error message or null if no error
     */
    public static Image getGraph(String measure, String timePeriod, String roomName) {
    	
        	HttpGet request = new HttpGet(serverRoot + "Graph?measure=" +
        			measure + "&timeperiod=" + timePeriod + "&token=" + Token.getToken()
        			+ "&room="+roomName);
        	HttpClient httpclient = AppHttpClient.GetInstance();
        	 try {
                 HttpResponse response = httpclient.execute(request);
                 int statusCode = response.getStatusLine().getStatusCode();
                 byte[] image = EntityUtils.toByteArray(response.getEntity());
                 request.releaseConnection();	
                 if (statusCode == 200) {
                     return ImageIO.read(new ByteArrayInputStream(image));
                 } else if (statusCode == 404) {
             		try {
            			return ImageIO.read(
            					Utilities.class.getResource("res/icons/NoData.png"));
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
                	 return null;
                 } else {
                	 Utilities.showErrorDialog("Greška "+statusCode,
                			 "Greška sa poslužiteljske strane prilikom dohvata grafa", null, null);
                	 return null;
                 }
             } catch (IOException ex) {
            	 Utilities.showErrorDialog("Greška",
            			 "Neuspješno uspostavljanje veze sa poslužiteljem"
            			 + " prilikom dohvata grafa", null, ex);
            	 request.releaseConnection();
                 return null;
             }
    }
    
}
