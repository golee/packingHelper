package project.hci.packinghelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class InsertActivity extends Activity {

    Button[] buttonSet = new Button[7];
    CheckBox checkBoxEveryday;
    CheckBox checkBoxWeekdays;
    CheckBox checkBoxWeekend;
    AutoCompleteTextView autoCompleteTextViewItemName;
    Button buttonAdd;

    ImageView imageViewIconPreview;
    Spinner spinnerIconSelect;
    LinearLayout layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        autoCompleteTextViewItemName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewItemName);
        autoCompleteTextViewItemName.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.recommanded)));
        autoCompleteTextViewItemName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoCompleteTextViewItemName.showDropDown();
                return false;
            }
        });

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
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

        checkBoxEveryday.performClick();

        imageViewIconPreview = (ImageView)findViewById(R.id.imageViewIconPreview);
        spinnerIconSelect = (Spinner)findViewById(R.id.spinnerIconSelect);
        ArrayAdapter<?> sAdpapter = ArrayAdapter.createFromResource(
                this, R.array.icons, R.layout.spinner_item);
        sAdpapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIconSelect.setAdapter(sAdpapter);
        spinnerIconSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) spinnerIconSelect.getSelectedItem();
                switch (selectedItem) {
                    case "Tea": imageViewIconPreview.setImageResource(R.drawable.ic_item_tea);break;
                    case "Books": imageViewIconPreview.setImageResource(R.drawable.ic_item_books);break;
                    case "Pencil": imageViewIconPreview.setImageResource(R.drawable.ic_item_edit);break;
                    case "Literature": imageViewIconPreview.setImageResource(R.drawable.ic_item_literature);break;
                    case "Mac": imageViewIconPreview.setImageResource(R.drawable.ic_item_mac_os);break;
                    case "Paper": imageViewIconPreview.setImageResource(R.drawable.ic_item_paper);break;
                    case "Star": imageViewIconPreview.setImageResource(R.drawable.ic_item_star);break;
                    case "Umbrella": imageViewIconPreview.setImageResource(R.drawable.ic_item_umbrella);break;
                    case "Usb": imageViewIconPreview.setImageResource(R.drawable.ic_item_usb_connected);break;
                    default:imageViewIconPreview.setImageResource(R.drawable.ic_item_tea);
                }
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
                String itemName = autoCompleteTextViewItemName.getText().toString();
                if (itemName.equals("")) {
                    Toast.makeText(InsertActivity.this, "Empty text input", Toast.LENGTH_SHORT).show();
                    return;
                }
               /*
                1. 서버로 보냄(아이템 이름만)
                2. 성공시 앱에도 추가
                    앱 저장소는  SharedPreferences 이용.
                3.
                 */
                boolean[] arrayBooleanDay = new boolean[7];
                for (int i = 0; i < 7; i++) {
                    arrayBooleanDay[i] = buttonSet[i].isSelected();
                }
                if (!arrayBooleanDay[0] && !arrayBooleanDay[1] && !arrayBooleanDay[2] && !arrayBooleanDay[3] && !arrayBooleanDay[4] && !arrayBooleanDay[5] && !arrayBooleanDay[6])
                    for (int i = 0; i < 7; i++) {
                        arrayBooleanDay[i] = true;
                    }

                int icon = spinnerIconSelect.getSelectedItemPosition();
                Intent intent = new Intent();
                intent.putExtra("itemName", itemName);
                intent.putExtra("arrayBooleanDay", arrayBooleanDay);
                intent.putExtra("icon", icon);
                setResult(RESULT_OK, intent);
                finish();
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

