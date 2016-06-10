package enhems;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Security;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
*
* @author Stjepan
*/
public class AppHttpClient extends DefaultHttpClient {
	

//	public static HttpClient GetInstance() {
//		return HttpClientBuilder.create().build();
//	}
	
   /**
    * @param context application context
    * @return HttpClient instance
    */
   public static HttpClient GetInstance() {
       if (instance == null) {
           instance = new AppHttpClient();
       }
       return instance;
   }

   /**
    * Private constructor for singleton pattern
    *
    * @param context application context
    */
   private AppHttpClient() {
   }

   /**
    * @return custom connectionManager for SSL
    */
   @Override
   protected ClientConnectionManager createClientConnectionManager() {
       SchemeRegistry registry = new SchemeRegistry();
       registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8084));
       // Register for port 8443 our SSLSocketFactory with our keystore
       // to the ConnectionManager
       registry.register(new Scheme("https", newSslSocketFactory(), 8443));
       return new ThreadSafeClientConnManager(getParams(), registry);
   }

   /**
    * @return SSlSocketFactory for given certificate in resources, or default
    * sslSocketFactory if failure occurs
    */
   private SSLSocketFactory newSslSocketFactory() {
	   
	   //needed provider
		if(Security.getProvider("BC")==null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	   
       try {
           // Get an instance of the Bouncy Castle KeyStore format
           KeyStore trusted = KeyStore.getInstance("BKS");
           // Get the raw resource, which contains the keystore with
           // your trusted certificates (root and any intermediate certs)

           InputStream in = new FileInputStream(Paths.get("./data/store").toFile());
           // Initialize the keystore with the provided trusted certificates
           // Also provide the password of the keystore
           trusted.load(in, "tomcat".toCharArray());
           in.close();

           // Pass the keystore to the SSLSocketFactory. The factory is responsible
           // for the verification of the server certificate.
           SSLSocketFactory sf = new SSLSocketFactory(trusted);
           // Hostname verification from certificate
           sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
           return sf;
       } catch (Exception ex) {
    	   if(ex instanceof IOException) {
    		   Utilities.showErrorDialog("Greška",
    				   "Nastala je greška prilikom dohvata certifikata! "
    					+ "Provjerite da se u direktoriju aplikacije nalazi prikladni folder \"data\".",null, ex);
    	   }
           System.exit(1);
       }
       //return default if fail
       return SSLSocketFactory.getSocketFactory();
   }

   private static AppHttpClient instance = null;
}
