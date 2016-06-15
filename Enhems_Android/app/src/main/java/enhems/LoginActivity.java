package enhems;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class LoginActivity extends Activity {

    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressDialog dialog;

    /**
     * Thread for user login
     */
    private class LoginThread extends Thread {

        private final String[] attributes;

        public LoginThread(String[] attributes) {
            this.attributes = attributes;
        }

        @Override
        public void run() {
            PostLogin(Login(attributes));
        }
    }

    /**
     * User login
     *
     * @param attributes attributes used for login, either username and password
     * or token
     * @return error message or null if no error
     */
    private String Login(String[] attributes) {
        HttpClient httpclient = AppHttpClient.GetInstance(getApplicationContext());
        HttpPost request = new HttpPost(getString(R.string.root) + "Login");
        List<NameValuePair> params = new ArrayList();
        if (attributes != null && attributes.length == 2) {
            params.add(new BasicNameValuePair("username", attributes[0]));
            params.add(new BasicNameValuePair("pass", attributes[1]));
        } else {
            params.add(new BasicNameValuePair("token", Token.get(this)));
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String token = EntityUtils.toString(response.getEntity());
                Token.set(this, token);
                return null;//login success
            } else if (statusCode == 400) {
                return "Neispravni podaci";//bad credencials
            } else if (statusCode == 403) {
                return "";//invalid token or expired
            } else {
                return "Greška na poslužitelju";
            }
        } catch (IOException ex) {
            Logger.getLogger(LoginActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Neuspješno spajanje";//timeout or other error
    }

    /**
     * To do after user login
     *
     * @param errorMessage error message to process, can be null if no error
     */
    private void PostLogin(final String errorMessage) {
        if (errorMessage == null) {
            String[] units = UnitsAssigned.GetData(getApplicationContext());
            Intent i = new Intent(LoginActivity.this, CurrentActivity.class);
            i.putExtra("units", units);
            dialog.dismiss();
            startActivity(i);
            finish();
        } else {
            dialog.dismiss();
            if (!errorMessage.isEmpty()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(LoginActivity.this);

        Async.StartWithDialog("Molimo pričekajte", new LoginThread(null), dialog);

        mLoginButton = (Button) findViewById(R.id.login_button);
        mUsername = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.password);

        //click listener
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Async.StartWithDialog("Molimo pričekajte", new LoginThread(new String[]{mUsername.getText().toString(), mPassword.getText().toString()}), dialog);
            }
        });
    }
}
