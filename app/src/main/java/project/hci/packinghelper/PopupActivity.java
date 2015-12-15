package project.hci.packinghelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

// on service detect the suspicious downloaded file.
public class PopupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final String message = getIntent().getExtras().getString("message");

        dialogBuilder.setTitle("PackingHelper");
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialogServerSetting = dialogBuilder.create();
        dialogServerSetting.show();
    }

}
