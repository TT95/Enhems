/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Stjepan
 */
public class CurrentActivity extends Activity {

    Spinner roomSelection;
    private TextView mTemp;
    private TextView mHum;
    private TextView mCO2;
    private TextView mSetTemp;
    private TextView mSystemStatus;
    private Button mControl;
    private ProgressDialog dialog;
    private boolean onResumeFlag;
    private String[] rooms;

    /**
     * Set data to text views
     *
     * @param data String array of data to be displayed
     */
    private void SetData(String[] data) {
        boolean opMode = true;
        boolean systemOn = true;
        if (data[5].equals("---")) {
            opMode = false;
        }
        if (data[6].equals("---")) {
            systemOn = false;
        }
        mTemp.setText(data[1]);
        mHum.setText(data[2]);
        mCO2.setText(data[3]);

        //operation mode setup
        if (opMode) {
            mSetTemp.setText(data[4]);
            mSetTemp.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String textValue = mSetTemp.getText().toString();
                    if (textValue.endsWith("C")) {
                        textValue = textValue.substring(0, textValue.length() - 2);
                        ShowSetTempDialog(Integer.valueOf(textValue));
                    } else {
                        ShowSetTempDialog(-1);
                    }
                }
            });
        } else {
            mSetTemp.setText("---");
            mSetTemp.setOnClickListener(null);
        }

        //system status notification
        if (systemOn) {
            mSystemStatus.setText("Sustav radi");
            mSystemStatus.setTextColor(Color.GREEN);
        } else {
            mSystemStatus.setText("Sustav ne radi");
            mSystemStatus.setTextColor(Color.RED);
        }
    }

    /**
     * @param measure measure for which to get graphs
     * @return error message or null if no error
     */
    private String GetHistory(String measure) {
        String[] timePeriods = new String[]{"L24", "LW"};
        for (String timePeriod : timePeriods) {
            HttpGet request = new HttpGet(getString(R.string.root) + "Graph?measure=" + measure + "&timeperiod="
                    + timePeriod + "&token=" + Token.get(this) + "&room=" + roomSelection.getSelectedItem().toString());
            String errorMessage = HttpGraph.Get(request, timePeriod, getApplicationContext());
            if (errorMessage != null) {
                return errorMessage;
            }
        }
        return null;
    }

    /**
     * Get control graph
     *
     * @return error message or null if no error
     */
    private String GetControl() {
        HttpGet request = new HttpGet(getString(R.string.root) + "Control?token=" + Token.get(this)
        +"&room="+roomSelection.getSelectedItem().toString());
        return HttpGraph.Get(request, "Control", getApplicationContext());
    }

    /**
     * Logout user
     *
     * @return error message or null if no error
     */
    private String Logout() {
        HttpClient httpclient = AppHttpClient.GetInstance(getApplicationContext());
        HttpPost request = new HttpPost(getString(R.string.root) + "Logout");
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("token", Token.get(this)));
        try {

            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                Token.set(this, null);
                return null;//success
            } else if (statusCode == 401) {
                return "Potrebna autentifikacija";
            } else {
                return "Greška na poslužitelju";
            }
        } catch (IOException ex) {
            Logger.getLogger(CurrentActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Neuspješno spajanje";

    }

    private String Setpoint(String setpoint, final String preSetPoint) {
        HttpClient httpclient = AppHttpClient.GetInstance(getApplicationContext());
        HttpPost request = new HttpPost(getString(R.string.root) + "Setpoint");
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("setpoint", setpoint));
        params.add(new BasicNameValuePair("token", Token.get(this)));
        params.add(new BasicNameValuePair("room", roomSelection.getSelectedItem().toString()));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return null;//sucess
            }
        } catch (IOException ex) {
            Logger.getLogger(CurrentActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return preSetPoint;
    }

    /**
     * Thread for refreshing data
     */
    private class RefreshThread extends Thread {

        @Override
        public void run() {
            PostRefresh(CurrentValues.GetData(getApplicationContext(),roomSelection.getSelectedItem().toString()));
        }
    }


    /**
     * Thread for getting graphs
     */
    private class GetGraphThread extends Thread {

        private final String measure;

        public GetGraphThread(String measure) {
            this.measure = measure;
        }

        @Override
        public void run() {
            if (measure == null) {
                PostGetGraph(GetControl(), ControlActivity.class);
            } else {
                PostGetGraph(GetHistory(measure), HistoryActivity.class);
            }
        }

    }

    /**
     * Thread for user logout
     */
    private class LogoutThread extends Thread {

        @Override
        public void run() {
            PostLogout(Logout());
        }

    }

    /**
     * Thread for setting temperature setpoint
     */
    private class SetPointThread extends Thread {

        private final String setpoint;
        private final String preSetPoint;

        public SetPointThread(String setpoint, String preSetPoint) {
            this.setpoint = setpoint;
            this.preSetPoint = preSetPoint;
        }

        @Override
        public void run() {
            PostSetPoint(Setpoint(setpoint, preSetPoint));
        }
    }

    /**
     * To do after data refresh
     *
     * @param data String array of data(refreshed) to be displayed
     */
    private void PostRefresh(final String[] data) {
        dialog.dismiss();
        runOnUiThread(new Runnable() {
            public void run() {
                SetData(data);
            }
        });
    }

    /**
     * To do after getting graphs
     *
     * @param errorMessage error message to process, can be null if no error
     * @param cls class of the next activity to be started if getting graphs was
     * successful
     */
    private void PostGetGraph(final String errorMessage, Class cls) {
        dialog.dismiss();
        if (errorMessage == null) {
            Intent i = new Intent(CurrentActivity.this, cls);
            i.putExtra("title", roomSelection.getSelectedItem().toString());
            startActivity(i);
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CurrentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * To do after user logout
     *
     * @param errorMessage error message to process, can be null if no error
     */
    private void PostLogout(final String errorMessage) {
        dialog.dismiss();
        if (errorMessage == null) {
            Intent i = new Intent(CurrentActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CurrentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void PostSetPoint(final String preSetPoint) {
        if (preSetPoint != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mSetTemp.setText(preSetPoint);
                    Toast.makeText(CurrentActivity.this, "Neuspješno postavljanje temperature!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Show temperature setpoint dialog
     *
     * @param value current setpoint value, or -1 if there is none
     */
    private void ShowSetTempDialog(int value) {
        final Dialog controlDialog = new Dialog(CurrentActivity.this);
        controlDialog.setTitle("Tempteratura");
        controlDialog.setContentView(R.layout.dialog_settemp);
        final NumberPicker setPointPicker = (NumberPicker) controlDialog.findViewById(R.id.numPicker);
        setPointPicker.setMaxValue(30);
        setPointPicker.setMinValue(15);
        if (value != -1) {
            setPointPicker.setValue(value);
        }

        //set listeners
        Button ok = (Button) controlDialog.findViewById(R.id.set_button);
        Button cancel = (Button) controlDialog.findViewById(R.id.cancel_button);

        ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String setPoint = String.valueOf(setPointPicker.getValue());
                Thread thread = new SetPointThread(setPoint, mSetTemp.getText().toString());
                thread.start();
                mSetTemp.setText(setPoint + "°C");
                controlDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                controlDialog.dismiss();
            }
        });

        controlDialog.show();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        dialog = new ProgressDialog(CurrentActivity.this);
        mTemp = (TextView) findViewById(R.id.tempCur_text);
        mHum = (TextView) findViewById(R.id.hum_text);
        mCO2 = (TextView) findViewById(R.id.co2_text);
        mSetTemp = (TextView) findViewById(R.id.tempSet_text);
        roomSelection = (Spinner) findViewById(R.id.roomSelection);
        mControl = (Button) findViewById(R.id.control_button);
        mSystemStatus = (TextView) findViewById(R.id.systemStatus_text);
        Intent i = getIntent();
        String[] units = i.getStringArrayExtra("units");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, units);
        roomSelection.setAdapter(adapter);
        setTitle("ENHEMS");

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        //set listeners
        mTemp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Async.StartWithDialog("Molimo pričekajte", new GetGraphThread((String) mTemp.getTag()), dialog);
            }
        });
        mHum.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Async.StartWithDialog("Molimo pričekajte", new GetGraphThread((String) mHum.getTag()), dialog);
            }
        });
        mCO2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Async.StartWithDialog("Molimo pričekajte", new GetGraphThread((String) mCO2.getTag()), dialog);
            }
        });
        mControl.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Async.StartWithDialog("Molimo pričekajte", new GetGraphThread(null), dialog);
            }
        });
        roomSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Async.StartWithDialog("Molimo pričekajte", new RefreshThread(), dialog);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        onResumeFlag = false;


        //wifi localization
        startWifiLocationScanning();
    }

    private final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private final int INTERVAL_FOR_SCAN = 1000;
    private void startWifiLocationScanning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult

        } else {
            startWifiScanning();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            startWifiScanning();

        }
    }
    private static WifiReceiver receiverWifi;
    private static WifiManager wifi;
    private void startWifiScanning() {
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        receiverWifi = new WifiReceiver(wifi);
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        Timer t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                wifi.startScan();
//            }
//        }, 0,INTERVAL_FOR_SCAN);
        wifi.startScan();
    }
    class WifiReceiver extends BroadcastReceiver {
        private WifiManager wifi;
        public WifiReceiver(WifiManager wifi) {
            this.wifi = wifi;
        }
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiList = wifi.getScanResults();
            String string = "";
            //json for sending wifi location
            JSONObject json = new JSONObject();
            for (ScanResult result : wifiList) {
                String ssid = result.SSID;
                String bssid = result.BSSID;
                Integer level = result.level;
//                TextView textView = (TextView) findViewById(R.id.location);
//                textView.setText(ssid);
                try {
                    json.put(bssid, level);
                    //TODO handle exception!
                } catch (JSONException ex) {  }
            }

            new LocationTask().execute(json);

//            Thread locationThread = new Thread(new Runnable() {
//                @Override
//                public void run(){
//                    writeToLocationFIeld(WifiLocation.GetData(getApplicationContext(), null));
//                }
//            });
//            locationThread.start();

        }
    }
    class LocationTask extends AsyncTask<JSONObject, Void, String> {

        private Exception exception;

        protected String doInBackground(JSONObject... jsons) {
            try {
                JSONObject json = jsons[0];
                return WifiLocation.GetData(getApplicationContext(), json);
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(String room) {
                    TextView textView = (TextView) findViewById(R.id.location);
                    String locationText = "Location: " + room;
                    textView.setText(locationText);
            wifi.startScan();
        }
    }
    public void writeToLocationFIeld(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                // use data here
                TextView textView = (TextView) findViewById(R.id.location);
                String locationText = "Location: " + string;
                textView.setText(locationText);
            }
        });
    }
    @Override
    protected void onStop()
    {
        try {
//            unregisterReceiver(receiverWifi);
        } catch (Exception ignore) {}
        super.onStop();
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (onResumeFlag) {
            Async.StartWithDialog("Molimo pričekajte", new RefreshThread(), dialog);
        } else {
            onResumeFlag = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu_current, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Async.StartWithDialog("Molimo pričekajte", new RefreshThread(), dialog);
        } else if (id == R.id.action_logout) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            Async.StartWithDialog("Molimo pričekajte", new LogoutThread(), dialog);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Jeste li sigurni?")
                    .setNegativeButton("Ne", dialogClickListener)
                    .setPositiveButton("Da", dialogClickListener)
                    .show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
