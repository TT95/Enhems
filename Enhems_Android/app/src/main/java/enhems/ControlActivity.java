/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Stjepan
 */
public class ControlActivity extends Activity {

    private ImageView mImgView;
    private ProgressDialog dialog;

    /**
     * Thread for setting new data
     */
    private class SetDataThread extends Thread {

        private final String data;

        public SetDataThread(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            PostSetData(SetData(data));
        }

    }

    /**
     * @param data new data to be set as string (min,max,value)
     * @return error message or null if no error
     */
    private String SetData(String data) {
        HttpPost request = new HttpPost(getString(R.string.root) + "Control");
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("data", data));
        params.add(new BasicNameValuePair("token", Token.get(this)));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ControlActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return HttpGraph.Get(request, "Control", this);
    }

    /**
     * To do after new data is set
     *
     * @param errorMessage error message to process, can be null if no error
     */
    private void PostSetData(final String errorMessage) {
        dialog.dismiss();
        if (errorMessage == null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    SetGraph();
                }
            }
            );
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ControlActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Show dialog to change user occupancy data
     */
    private void ShowControlDialog() {
        final Dialog controlDialog = new Dialog(ControlActivity.this);
        controlDialog.setTitle("Od-Do-Regulacija");
        controlDialog.setContentView(R.layout.dialog_control);
        final NumberPicker from = (NumberPicker) controlDialog.findViewById(R.id.from_picker);
        final NumberPicker to = (NumberPicker) controlDialog.findViewById(R.id.to_picker);
        final NumberPicker value = (NumberPicker) controlDialog.findViewById(R.id.value_picker);
        from.setMaxValue(23);
        from.setMinValue(0);
        to.setMaxValue(23);
        to.setMinValue(0);
        value.setMaxValue(1);
        value.setMinValue(0);

        //set listeners
        Button ok = (Button) controlDialog.findViewById(R.id.set_button);
        Button cancel = (Button) controlDialog.findViewById(R.id.cancel_button);

        ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String[] data = new String[]{String.valueOf(from.getValue()), String.valueOf(to.getValue()), String.valueOf(value.getValue())};
                controlDialog.dismiss();
                Async.StartWithDialog("Molimo pričekajte", new SetDataThread(TextUtils.join(",", data)), dialog);
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
     * Set graph to image view
     */
    private void SetGraph() {
        try {
            mImgView.setImageBitmap(BitmapFactory.decodeStream(openFileInput("Control")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HistoryActivity.class.getName()).log(Level.SEVERE, null, ex);
            Toast.makeText(ControlActivity.this, "Greška u dohvaćanju grafova", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        //setTitle(User.roomName.toUpperCase());
        setTitle(getIntent().getStringExtra("title"));
        dialog = new ProgressDialog(ControlActivity.this);
        mImgView = (ImageView) findViewById(R.id.control_graph);
        SetGraph();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_set_control) {
            ShowControlDialog();
        }

        return super.onOptionsItemSelected(item);
    }

}
