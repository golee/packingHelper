package project.hci.packinghelper;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class InsertActivity extends Activity {

    Button[] buttonSet = new Button[7];
    CheckBox checkBoxEveryday;
    CheckBox checkBoxWeekdays;
    CheckBox checkBoxWeekend;

    Spinner spinnerIconSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        final EditText editTextItemName = (EditText) findViewById(R.id.editTextItemName);
        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonSet[0] = (Button)findViewById(R.id.buttonDay0);
        buttonSet[1] = (Button)findViewById(R.id.buttonDay1);
        buttonSet[2] = (Button)findViewById(R.id.buttonDay2);
        buttonSet[3] = (Button)findViewById(R.id.buttonDay3);
        buttonSet[4] = (Button)findViewById(R.id.buttonDay4);
        buttonSet[5] = (Button)findViewById(R.id.buttonDay5);
        buttonSet[6] = (Button)findViewById(R.id.buttonDay6);
        checkBoxEveryday = (CheckBox)findViewById(R.id.checkboxEveryday);
        checkBoxWeekdays = (CheckBox)findViewById(R.id.checkboxWeekdays);
        checkBoxWeekend = (CheckBox)findViewById(R.id.checkboxWeekend);

        checkBoxEveryday.setOnClickListener(dayButtonListner);
        checkBoxWeekdays.setOnClickListener(dayButtonListner);
        checkBoxWeekend.setOnClickListener(dayButtonListner);

        spinnerIconSelect = (Spinner)findViewById(R.id.spinnerIconSelect);
        ArrayAdapter<?> sAdpapter = ArrayAdapter.createFromResource(
                this, R.array.spinnerItems, R.layout.spinner_item);
        sAdpapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIconSelect.setAdapter(sAdpapter);


        spinnerIconSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (Button b : buttonSet)
            b.setOnClickListener(dayButtonListner);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = editTextItemName.getText().toString();

               /*
                1. 서버로 보냄(아이템 이름만)
                2. 성공시 앱에도 추가
                    앱 저장소는  SharedPreferences 이용.
                3.
                 */
            }
        });
    }

    View.OnClickListener dayButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v.getId() == R.id.checkboxEveryday ) {
                if ( checkBoxEveryday.isChecked() )
                    for ( Button b: buttonSet )
                        b.setSelected(true);
                else
                    for ( Button b: buttonSet )
                        b.setSelected(false);
            }
            else if ( v.getId() == R.id.checkboxWeekdays ) {
                if ( checkBoxWeekdays.isChecked() )
                    for ( int i=0; i<5; i++ )
                        buttonSet[i].setSelected(true);
                else
                    for ( int i=0; i<5; i++ )
                        buttonSet[i].setSelected(false);
            }
            else if ( v.getId() == R.id.checkboxWeekend ) {
                if ( checkBoxWeekend.isChecked() )
                    for ( int i=5; i<7; i++ )
                        buttonSet[i].setSelected(true);
                else
                    for ( int i=5; i<7; i++ )
                        buttonSet[i].setSelected(false);
            }
            else {
                if  ( v.isSelected() )
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        }
    };
}

