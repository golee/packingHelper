package project.hci.packinghelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends Activity {
    Button buttonImportantItemNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        buttonImportantItemNoti = (Button)findViewById(R.id.buttonImportantItemNoti);
    }

    View.OnClickListener mListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ( v.getId() ) {
                case R.id.buttonImportantItemNoti:
                    Intent intent = new Intent(SettingActivity.this, ThemeActivity.class);
                    startActivity(intent);
            }
        }
    };
}
