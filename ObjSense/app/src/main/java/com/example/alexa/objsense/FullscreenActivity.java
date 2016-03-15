package com.example.alexa.objsense;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.alexa.objsense.DrawableSurface;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;

    //Dynamic Shit
    private TextView myTextView;
    private QRCodeReaderView mydecoderview;
    private DrawableSurface drawingSurface;
    private Button testButton;
    private Context context = this;
    private GregorianCalendar lastSeen;
    private String lastUUIDSeen="";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        //Set main view to camera
        setContentView(R.layout.activity_fullscreen);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        myTextView = (TextView) findViewById(R.id.textView);
        drawingSurface = (DrawableSurface) findViewById(R.id.drawablesurface);
        testButton = (Button) findViewById(R.id.testBtn);

        drawingSurface.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // ... Respond to touch events
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("DEBUG", "CHECK:" + event.getX() + "," + event.getY());
                    if (drawingSurface.insideButton((int) event.getX(), (int) event.getY())) {
                        //Make HTTP Request for menu items
                        Toast.makeText(context, "Open Menu", Toast.LENGTH_SHORT).show();
                        new HTTPMenuRequest().execute(lastUUIDSeen);
                    }
                }
                return true;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    class HTTPMenuRequest extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... UUID) {
            HttpURLConnection urlConnection = null;
            URL url = null;
            String strFileContents = "";
            try {
                url = new URL("http://localhost:8000/?UUID="+UUID[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                byte[] contents = new byte[1024];

                int bytesRead=0;
                while( (bytesRead = in.read(contents)) != -1){
                    strFileContents = new String(contents, 0, bytesRead);
                }
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strFileContents;
        }

        protected void onPostExecute(String data) {
            // TODO: Make Pop Up Menu On QR Code
            Log.d("DEBUG", data);
            String[] menuItems = data.split(",");
            PopupWindow popup = new PopupWindow(context);
            PopupMenu popupMenu = new PopupMenu(context, mContentView);
            for(String s:menuItems){
                popupMenu.getMenu().add(s);
            }
            //popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    new SendHttpRequestTask().execute(lastUUIDSeen, item.toString());
                    Log.d("DEBUG",item.toString());
                    return true;
                }
            });
            popupMenu.show();
        }
    }

    void sendHttpRequest(String UUID,String COMMAND) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        String strFileContents = "";
        try {
            url = new URL("http://localhost:8000");
            urlConnection = (HttpURLConnection) url.openConnection();

//            String urlParameters  = "UUID="+UUID+"&COMMAND="+COMMAND;
//            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
//            int    postDataLength = postData.length;
//            urlConnection.setInstanceFollowRedirects(false);
//            urlConnection.setRequestMethod( "POST" );
//            urlConnection.setRequestProperty( "Content-Type", "text/plain");
//            urlConnection.setRequestProperty( "charset", "utf-8");
//            urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
//            urlConnection.setUseCaches( false );
//            try( DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream())) {
//                wr.write( postData );
//            }
//            urlConnection.connect();

            String urlParameters  = "UUID="+UUID+"&COMMAND="+COMMAND;
            Log.d("DEBUG","URLPARAMS:"+urlParameters);
            urlConnection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

            writer.write(urlParameters);
            writer.flush();

            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
            }
            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String uuid = params[0];
            String command = params[1];
            try {
                sendHttpRequest(uuid, command);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //AFter command is sent
        }
    }


        @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        lastSeen = new GregorianCalendar();
        drawingSurface.setButtonState(true);
        myTextView.setText(text);
        lastUUIDSeen = text;
        //Log.d("DEBUG", "" + points[0] + "," + points[1] + "::" + points[2] + "," + points[3]);
        //testButton.setVisibility(Button.VISIBLE);
        //testButton.setX(points[0].x);
        //testButton.setY(points[0].y);
        //Paint paint = new Paint();
        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.WHITE);
        drawingSurface.setPoints(points);
        drawingSurface.invalidate();
    }

    // Called when your device have no camera
    @Override
    public void cameraNotFound() {

    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {
        if (lastSeen != null) {
            GregorianCalendar comparator = new GregorianCalendar();
            GregorianCalendar tempSeen = (GregorianCalendar) lastSeen.clone();
            tempSeen.add(Calendar.SECOND, 1);
            if (comparator.after(tempSeen)) {
                testButton.setVisibility(Button.INVISIBLE);
                drawingSurface.setButtonState(false);
            }
        }
        drawingSurface.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Fullscreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.alexa.objsense/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Fullscreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.alexa.objsense/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
