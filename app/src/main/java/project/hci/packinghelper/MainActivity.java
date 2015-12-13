package project.hci.packinghelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Snackbar를 사용하자 ~~ Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    RelativeLayout layoutRoot;
    final static String DefaultUrl = "http://61.72.174.90/hci/dbAccess.php";
    private ArrayList<PackingItem> arrayListPackingItem = new ArrayList<>();
    class PackingItem {
        PackingItem ( String itemName, int day) {
            this.itemName = itemName;
            this.day = day;
        }
        String itemName;
        int day;
        boolean isChecked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutRoot = (RelativeLayout)findViewById(R.id.layoutRoot);
        registBroadcastReceiver();
        getInstanceIdToken();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode==1 && resultCode == RESULT_OK) {
            String itemName = data.getExtras().getString("itemName");
            boolean[] arrayBooleanDay = data.getExtras().getBooleanArray("arrayBooleanDay");
            for ( int i=0; i<7; i++ ) {
                if ( arrayBooleanDay[i] )
                    addToServer(itemName, i);
            }

            Set<String> strSet = new HashSet();
            SharedPreferences sp = getSharedPreferences("my", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            sp.getStringSet("itemList", strSet).add(itemName);
            editor.putStringSet("itemList", sp.getStringSet("itemList", strSet));
        }
    }

    private void showList() {
        if ( arrayListPackingItem.isEmpty() ) {
            Log.d("Jebum","Empty arrayListPackingItem");
            return;
        }
        else {
            LinearLayout linearLayoutColFirst = (LinearLayout)findViewById(R.id.linearLayoutColFirst);
            LinearLayout linearLayoutColSecond = (LinearLayout)findViewById(R.id.linearLayoutColSecond);
            LinearLayout linearLayoutColThird = (LinearLayout)findViewById(R.id.linearLayoutColThird);
            LayoutInflater inflater = LayoutInflater.from(this);
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Layout 마다 4개짜리 array제공?
            ItemBlock itemBlock = new ItemBlock(this, null);
            itemBlock.setText("books");
            itemBlock.setImage(R.drawable.ic_item_books);
            linearLayoutColFirst.addView(itemBlock);
        }
    }

    private void getItem () {
        String url = DefaultUrl + "?cmd=getList";
        TransferThread thread = new TransferThread( url );
        thread.start();
    }
    private void addToServer ( String itemName, int day ) {
        // 요일 넣어야함.
        try {
            String url = DefaultUrl + "?cmd=add&id=" + URLEncoder.encode(itemName, "UTF-8") + "day=" + day;
            TransferThread thread = new TransferThread( url );
            thread.start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    String httpResponse = "";
    class TransferThread extends Thread {
        String targetURL;
        public void setUrl ( String url ) {
            targetURL = url;
        }
        public TransferThread ( String url ) {
            this.targetURL = url;
        }
        public void run() {
            try {
                URL url;
                byte[] unitByte;
                url = new URL(targetURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);

                InputStream inputStream = conn.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while( scanner.hasNext() ) {
                    String response = scanner.nextLine();
                    httpResponse += response;
                }
                Log.d("httpResponseResultJebum", httpResponse);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(httpResponse);
                    JSONArray result= jsonObject.getJSONArray("result");
                    if ( result.get(0).equals("true") || result.get(0).equals("false") )
                        ;   // Case of Add, Delete item
                    else {
                        arrayListPackingItem.clear();
                        for ( int i=0; i<result.length(); i++ ) {
                            arrayListPackingItem.add(new PackingItem(
                                            result.getJSONObject(i).getString("id"),
                                            Integer.parseInt(result.getJSONObject(i).getString("day")))
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showList();
                            Snackbar.make(layoutRoot, httpResponse, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                httpResponse = "";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ////@@@@@ Google push Id token @@@@@////
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    Log.i(TAG, "regestrationReady");
                    // 액션이 READY일 경우
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    Log.i(TAG, "regestrationGenerating");
                    // 액션이 GENERATING일 경우
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    Log.i(TAG, "regestrationComplete");
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                }
            }
        };
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent( this, SettingActivity.class );
            startActivityForResult(i, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
