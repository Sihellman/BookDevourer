package com.e.readingorganized;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.math.BigDecimal;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {
    private Button set;
    private EditText pageNumber;
    private EditText frequency;
    public static String pageMessage  = "page message";
    public static String frequencyMessage = "frequency message";
    public static String minMessage = "minute message";
    public static String secMessage = "second message";
    private NumberPicker picker1;
    private NumberPicker picker2;
    private NumberPicker picker3;
    private NumberPicker picker4;
    private String[] pickerVals1;
    private String[] pickerVals2;
    private String valPicker1 = "0";
    private String valPicker2 = "0";
    private String valPicker3 = "0";
    private String valPicker4 = "0";
    public static final int TEXT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        set = findViewById(R.id.set); //button to set settings
        pageNumber = findViewById(R.id.page_number); //total number of pages
        frequency = findViewById(R.id.frequency); //how often you would like a notification
        picker1 = findViewById(R.id.minPick);
        picker2 = findViewById(R.id.minPick2);
        picker3 = findViewById(R.id.secPick);
        picker4 = findViewById(R.id.secPick2);
        picker1.setMaxValue(5);
        picker1.setMinValue(0);
        picker2.setMaxValue(9);
        picker2.setMinValue(0);
        picker3.setMaxValue(5);
        picker3.setMinValue(0);
        picker4.setMaxValue(9);
        picker4.setMinValue(0);
        pickerVals1  = new String[] {"0", "1", "2", "3", "4", "5"};
        picker1.setDisplayedValues(pickerVals1);
        pickerVals2 = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        picker2.setDisplayedValues(pickerVals2);
        picker3.setDisplayedValues(pickerVals1);
        picker4.setDisplayedValues(pickerVals2);
        picker4.setValue(1);
        valPicker4 = "1";
        picker1.setOnValueChangedListener((numberPicker, i, i1) -> {
            int valuePicker1 = picker1.getValue();
            valPicker1 = pickerVals1[valuePicker1];
            if((valPicker1+picker2.getValue()+picker3.getValue()+picker4.getValue()).equals("0000")
                    ){
                picker4.setValue(1);
                valPicker4 = "1";
            }
            Log.d("picker value", valPicker1);
        });

        picker2.setOnValueChangedListener((numberPicker, i, i1) -> {
            int valuePicker1 = picker2.getValue();
            valPicker2 = pickerVals2[valuePicker1];
            if((valPicker2+picker1.getValue()+picker3.getValue()+picker4.getValue()).equals("0000") ){
                picker4.setValue(1);
                valPicker4 = "1";
            }
        });
        picker3.setOnValueChangedListener((numberPicker, i, i1) -> {
            int valuePicker1 = picker3.getValue();
            valPicker3 = pickerVals1[valuePicker1];
            if((valPicker3+picker2.getValue()+picker1.getValue()+picker4.getValue()).equals("0000") ){
                picker4.setValue(1);
                valPicker4 = "1";
            }
        });
        picker4.setOnValueChangedListener((numberPicker, i, i1) -> {
            int valuePicker1 = picker4.getValue();
            valPicker4 = pickerVals2[valuePicker1];
            if((valPicker4+picker2.getValue()+picker3.getValue()+picker1.getValue()).equals("0000")){
                picker4.setValue(1);
                valPicker4 = "1";
            }
        });
        set.setOnClickListener(v->{


            if(pageNumber.getText().toString().matches("")){
                //pageNumber.requestFocus();
                pageNumber.setError("required field");

            }

            else{
                BigDecimal pageDecimal = new BigDecimal(pageNumber.getText().toString());
                if (pageDecimal.compareTo(BigDecimal.ZERO) <= 0){
                   // pageNumber.requestFocus();
                    pageNumber.setError("HEY, NOTHING TO READ  ");

                }
                if(frequency.getText().toString().matches("")){
                    //frequency.requestFocus();
                    frequency.setError("required field");

                }else if((new BigDecimal(frequency.getText().toString()).compareTo(BigDecimal.ZERO) <= 0)){
                   // frequency.requestFocus();
                    frequency.setError("hey, you gotta have notifications");
                }

                else{
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra(pageMessage, pageNumber.getText().toString());
                    intent.putExtra(frequencyMessage, frequency.getText().toString());
                    intent.putExtra(minMessage, valPicker1+valPicker2);
                    intent.putExtra(secMessage, valPicker3+valPicker4);
                    startActivityForResult(intent, TEXT_REQUEST);
                }
            }



        });

    }
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String pages = data.getStringExtra(MainActivity2.EXTRA_PAGES);
                String minutes = data.getStringExtra(MainActivity2.EXTRA_MIN);
                String seconds = data.getStringExtra(MainActivity2.EXTRA_SEC);
                String freq = data.getStringExtra(MainActivity2.EXTRA_FREQ);



                pageNumber.setText(pages);
                picker1.setValue(parseInt(minutes.charAt(0)+""));
                picker2.setValue(parseInt(minutes.charAt(1)+""));
                picker3.setValue(parseInt(seconds.charAt(0)+""));
                picker4.setValue(parseInt(seconds.charAt(1)+""));
                frequency.setText(freq);
                pageNumber.requestFocus();
                pageNumber.setSelection(pageNumber.getText().length());

            }
        } else {
            Log.d("ImplicitIntents", "Can't handle this!");
        }

    }

    @Override
    public void onResume(){
        super.onResume();

    }
}