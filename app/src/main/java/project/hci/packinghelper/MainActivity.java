package project.hci.packinghelper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    Button[] buttonSet = new Button[7];
    int today;
    RelativeLayout layoutRoot;
    final static String DefaultUrl = "http://61.72.174.90/hci/dbAccess.php";
    private ArrayList<PackingItem> arrayListPackingItem = new ArrayList<>();
    class PackingItem {
        PackingItem ( String itemName, int day, boolean isChecked) {
            this.itemName = itemName;
            this.day = day;
            this.isChecked = isChecked;
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
        buttonSet[0] = (Button)findViewById(R.id.buttonDay0);
        buttonSet[1] = (Button)findViewById(R.id.buttonDay1);
        buttonSet[2] = (Button)findViewById(R.id.buttonDay2);
        buttonSet[3] = (Button)findViewById(R.id.buttonDay3);
        buttonSet[4] = (Button)findViewById(R.id.buttonDay4);
        buttonSet[5] = (Button)findViewById(R.id.buttonDay5);
        buttonSet[6] = (Button)findViewById(R.id.buttonDay6);
        for (Button b : buttonSet)
            b.setOnClickListener(dayButtonListner);

        today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); // 0~6 MON~SUN
        if ( today == 1 )
            today = 6;
        else
            today -=2;
        buttonSet[today].setSelected(true);
        registBroadcastReceiver();
        getInstanceIdToken();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        updateItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode==1 && resultCode == RESULT_OK) {
            String itemName = data.getExtras().getString("itemName");
            boolean[] arrayBooleanDay = data.getExtras().getBooleanArray("arrayBooleanDay");
            int lastTrue = 7;
            for ( int i=0; i<7; i++ ) {
                if ( arrayBooleanDay[i] ) {
                    lastTrue = i;
                }
            }
            for ( int i=0; i<7; i++ ) {
                if ( i == lastTrue )
                    addToServer(itemName, i, true);
                else if ( arrayBooleanDay[i] ) {
                    addToServer(itemName, i);
                }
            }
/*
            Set<String> strSet = new HashSet();
            SharedPreferences sp = getSharedPreferences("my", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            sp.getStringSet("itemList", strSet).add(itemName);
            editor.putStringSet("itemList", sp.getStringSet("itemList", strSet));*/
        }
    }
    View.OnClickListener dayButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v.isSelected() )
                ;
            else {
                v.setSelected(true);
                for ( int i=0; i<7; i++ ) {
                    if ( v.getId() == buttonSet[i].getId() ) {
                        today = i;
                        updateItem();
                    }
                    else
                        buttonSet[i].setSelected(false);
                }
            }
        }
    };

    private void showList() {
        if ( arrayListPackingItem.isEmpty() ) {
            Log.d("Jebum","Empty arrayListPackingItem");
            return;
        }
        else {
            LinearLayout linearLayoutColFirst = (LinearLayout)findViewById(R.id.linearLayoutColFirst);
            LinearLayout linearLayoutColSecond = (LinearLayout)findViewById(R.id.linearLayoutColSecond);
            LinearLayout linearLayoutColThird = (LinearLayout)findViewById(R.id.linearLayoutColThird);
            linearLayoutColFirst.removeAllViews();
            linearLayoutColSecond.removeAllViews();
            linearLayoutColThird.removeAllViews();
            // Layout 마다 4개짜리 array제공?
            int rotator = 0;
            for ( PackingItem p: arrayListPackingItem ) {
                LinearLayout linearLayout=null;
                if ( p.day != today)
                    continue;
                switch ( rotator++ ) {
                    case 0:linearLayout=linearLayoutColFirst;break;
                    case 1:linearLayout=linearLayoutColSecond;break;
                    case 2:linearLayout=linearLayoutColThird;rotator=0;break;
                }
                ItemBlock itemBlock = new ItemBlock(this);
                linearLayout.addView(itemBlock);
                itemBlock.setText(p.itemName);
                itemBlock.setImage(R.drawable.ic_item_books);
                if (p.isChecked)
                    itemBlock.setColorFilter(R.color.colorPrimary);
                itemBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItemBlock i = (ItemBlock)v;
                        final String id = i.getText();

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        dialogBuilder.setTitle(id);
                        dialogBuilder.setNegativeButton("Fix",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "Item Fixed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        dialogBuilder.setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteItem(id);
                                        Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        AlertDialog dialogServerSetting = dialogBuilder.create();
                        dialogServerSetting.show();
                    }
                });
            }
        }
    }

    private void updateItem () {
        String url = DefaultUrl + "?cmd=getList";
        TransferThread thread = new TransferThread( url );
        thread.start();
    }
    private void addToServer ( String itemName, int day ) {
        addToServer(itemName, day, false);
    }

    private void addToServer ( String itemName, int day, boolean updating ) {
        // 요일 넣어야함.
        try {
            String url = DefaultUrl + "?cmd=add&id=" + URLEncoder.encode(itemName, "UTF-8") + "&day=" + day;
            TransferThread thread = new TransferThread( url, updating );
            thread.start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 일단은 싸그리 삭제~
    private void deleteItem ( String itemName ) {
        try {
            String url = DefaultUrl + "?cmd=delete&id=" + URLEncoder.encode(itemName, "UTF-8");
            TransferThread thread = new TransferThread( url, true );
            thread.start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // url 로 http connection
    class TransferThread extends Thread {
        String httpResponse = "";
        String targetURL;
        boolean updating = false;
        public void setUrl ( String url ) {
            targetURL = url;
        }
        public TransferThread ( String url ) {
            this.targetURL = url;
        }
        public TransferThread ( String url, boolean updating ) {
            this.targetURL = url;
            this.updating = updating;
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
                httpResponse = "";
                while( scanner.hasNext() ) {
                    String response = scanner.nextLine();
                    httpResponse += response;
                }
                Log.i("Jebum", httpResponse);
                if ( httpResponse.contains("Error"))
                    Toast.makeText(MainActivity.this, "SQL Syntax Error: "+httpResponse, Toast.LENGTH_LONG);
                else{
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(httpResponse);
                        JSONArray result = jsonObject.getJSONArray("result");
                        if ( result.length() == 0 )
                            ;
                        else if (result.get(0).equals("true") || result.get(0).equals("false"))
                            Log.d("Jebum", "beforeUp");
                            if ( updating ) {  // Case of Add, Delete item
                                updateItem();
                                Log.d("Jebum", "afterUp");
                            }
                        else {  // Select, getList, getitem
                            arrayListPackingItem.clear();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject j = result.getJSONObject(i);
                                boolean isChecked;
                                if ( Integer.parseInt(j.getString("isChecked")) == 0 )
                                    isChecked = false;
                                else
                                    isChecked = true;
                                arrayListPackingItem.add(new PackingItem(
                                                j.getString("id"),
                                                Integer.parseInt(j.getString("day")),
                                                isChecked
                                ));
                            }
                                Log.d("Jebum","ping");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showList();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
