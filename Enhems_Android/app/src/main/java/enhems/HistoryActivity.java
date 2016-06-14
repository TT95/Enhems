/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enhems;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stjepan
 */
public class HistoryActivity extends Activity {

    private Bitmap[] graphs;
    private final String[] menuItemLabels = new String[]{"Promjeni na zadnji tjedan", "Promjeni na zadnjih 24 sata"};
    private ImageView mImgView;
    private int i = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //setTitle(User.roomName.toUpperCase());
        setTitle(getIntent().getStringExtra("title"));
        mImgView = (ImageView) findViewById(R.id.history_graph);
        try {
            graphs = new Bitmap[]{BitmapFactory.decodeStream(openFileInput("L24")), BitmapFactory.decodeStream(openFileInput("LW"))};
            mImgView.setImageBitmap(graphs[i++ % 2]);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HistoryActivity.class.getName()).log(Level.SEVERE, null, ex);
            Toast.makeText(HistoryActivity.this, "Greška u dohvaćanju grafova", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change) {
            item.setTitle(menuItemLabels[i % 2]);
            mImgView.setImageBitmap(graphs[i++ % 2]);
        }

        return super.onOptionsItemSelected(item);
    }
    
}
