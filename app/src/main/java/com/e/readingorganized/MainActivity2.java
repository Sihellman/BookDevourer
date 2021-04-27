package com.e.readingorganized;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class MainActivity2 extends AppCompatActivity implements SimpleCountDownTimer.OnCountDownListener {
    public static BigDecimal checkFrequency;

    public static final int TEXT_REQUEST = 1;
    public static NotificationManager mNotifyManager;
    public static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private BigDecimal frequency;
    private BigDecimal pagesLeft;
    private SimpleCountDownTimer simpleCountDownTimer;
    private int chosenMin;
    private int chosenSec;
    private int secPerPage;
    private int minPerPage;
    private TextView viewPages;
    private TextView inquiry;
    private TextView textView;
    private ImageButton start;
    private ImageButton pause;
    private Button resume;
    private Button submit;
    private ImageView settings1;
    private ImageView settings2;
    private Button settings;
    private ImageView edit;
    private EditText pagesRead;
    public static String replyPages = "reply pages";
    public static String replyFrequency = "reply frequency";
    private BigDecimal pagesHolder;
    private BigDecimal freqHolder;
    public static final String EXTRA_PAGES = "pages";
    public static final String EXTRA_MIN = "min";
    public static final String EXTRA_SEC = "sec";
    public static final String EXTRA_FREQ = "freq";

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        inquiry = findViewById(R.id.inquiry);//the question: how many pages did you read?
        pagesRead = findViewById(R.id.pages_read);//the number entered in response
        submit = findViewById(R.id.submit_pages);//submit button for how many pages you read
        settings1 = findViewById(R.id.settings1);
        settings2 = findViewById(R.id.settings2);

        edit = findViewById(R.id.edit);
        Intent intent = getIntent();
        String pagesStr = intent.getStringExtra(MainActivity.pageMessage);
        pagesLeft = new BigDecimal(pagesStr);

        viewPages = findViewById(R.id.pagesLeft);
        if (pagesLeft.compareTo(BigDecimal.ONE) == 0){
            viewPages.setText(String.format("%s page left", pagesStr));
        }
        else{
            viewPages.setText(String.format("%s pages left", pagesStr));
        }

        pagesHolder = pagesLeft;
        String frequencyString = intent.getStringExtra(MainActivity.frequencyMessage);
        frequency = new BigDecimal(frequencyString);
        freqHolder = frequency;//place holder for how often you would like a notification
        checkFrequency = frequency;//how often you would like a notification
        String minuteStr = intent.getStringExtra(MainActivity.minMessage);//
        chosenMin = parseInt(minuteStr);
        minPerPage = chosenMin;
        String secondStr = intent.getStringExtra(MainActivity.secMessage);
        chosenSec = parseInt(secondStr);
        secPerPage = chosenSec;
        BigDecimal multipliedMin = frequency.multiply(new BigDecimal(chosenMin));
        BigDecimal multipliedSec = frequency.multiply(new BigDecimal(chosenSec));
        BigDecimal START_TIME_IN_MILLIS = (multipliedSec.multiply(new BigDecimal(1000))).add(multipliedMin.multiply(new BigDecimal(1000)).multiply(new BigDecimal(60)));
//START_TIME_IN_MILLIS is the time it takes you to read a page times the number of pages you would like to be notified
        //after

        BigDecimal rounded = START_TIME_IN_MILLIS.setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedDouble = rounded.doubleValue();
        chosenMin = (int) (((roundedDouble / 1000) / 60) % 60);
        chosenSec = (int) (roundedDouble / 1000) % 60;
        simpleCountDownTimer = new SimpleCountDownTimer(chosenMin, chosenSec, 1, this);
        textView = findViewById(R.id.tv);//this is the view of the countdown timer
        textView.setText(simpleCountDownTimer.getCountDownTime());


        start = findViewById(R.id.startBtn);
        resume = findViewById(R.id.resumeBtn);
        pause = findViewById(R.id.pauseBtn);

        resume.setEnabled(false);

        start.setOnClickListener(view -> {
            simpleCountDownTimer.start(false);//will not resume from where the timer is paused

            start.setEnabled(false);

        });

        resume.setOnClickListener(view -> {
            simpleCountDownTimer.start(true);
            simpleCountDownTimer.runOnBackgroundThread();
        });
        pause.setOnClickListener(view -> {
            simpleCountDownTimer.pause();
            simpleCountDownTimer.setTimerPattern("s");
            resume.setEnabled(true);
        });
        submit.setOnClickListener(view -> {
            if (pagesRead.getText().toString().matches("")) {

            } else {
                pagesLeft = pagesLeft.subtract(new BigDecimal(pagesRead.getText().toString()));

            }
            if (pagesLeft.compareTo(BigDecimal.ONE) > 0){
                viewPages.setText(String.format("%s pages left", pagesLeft));
            }
            else if(pagesLeft.compareTo(BigDecimal.ONE) == 0){
                viewPages.setText(String.format("%s page left!!! :)", pagesLeft));
            }


            if (pagesLeft.compareTo(BigDecimal.ZERO) <= 0) {
                launchCongratulations();
            } else {
                //Toast.makeText(this, "else reached", Toast.LENGTH_SHORT).show();
                textView.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);
                resume.setVisibility(View.VISIBLE);
                pause.setVisibility(View.VISIBLE);
                viewPages.setVisibility(View.VISIBLE);
                inquiry.setVisibility(View.INVISIBLE);
                pagesRead.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);
            }
        });
        settings1.setClickable(true);
        settings1.setOnClickListener(view -> {

            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_FREQ, frequencyString);
            replyIntent.putExtra(EXTRA_PAGES, pagesStr);
            replyIntent.putExtra(EXTRA_MIN, minuteStr);
            replyIntent.putExtra(EXTRA_SEC, secondStr);

            setResult(RESULT_OK,replyIntent);
            finish();
        });
        settings2.setClickable(true);
        settings2.setOnClickListener(view -> {

            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_FREQ, frequencyString);
            replyIntent.putExtra(EXTRA_PAGES, pagesStr);
            replyIntent.putExtra(EXTRA_MIN, minuteStr);
            replyIntent.putExtra(EXTRA_SEC, secondStr);

            setResult(RESULT_OK,replyIntent);
            finish();
        });
        edit.setClickable(true);
        edit.setOnClickListener(view -> {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "This is a message displayed in a Toast",
                    Toast.LENGTH_SHORT);

            toast.show();
        });


    }

    @Override
        public void onBackPressed(){
        moveTaskToBack(true);

        Intent replyIntent = new Intent();
        replyIntent.putExtra(replyPages, pagesHolder);
        replyIntent.putExtra(replyFrequency, freqHolder);
        setResult(RESULT_OK,replyIntent);

    }


    @Override
    public void onCountDownActive(String time) {

        textView.post(() -> textView.setText(time));

        Toast.makeText(this, "Seconds = " + simpleCountDownTimer.getSecondsTillCountDown() + " Minutes=" + simpleCountDownTimer.getMinutesTillCountDown(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCountDownFinished() {
        textView.post(() -> {
            //textView.setText("Finished");
            start.setEnabled(true);
            resume.setEnabled(false);
            simpleCountDownTimer = new SimpleCountDownTimer(chosenMin, chosenSec, 1, this);

            textView.setText(simpleCountDownTimer.getCountDownTime());
            textView.setVisibility(View.INVISIBLE);
            start.setVisibility(View.INVISIBLE);
            resume.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.INVISIBLE);
            viewPages.setVisibility(View.INVISIBLE);
            inquiry.setVisibility(View.VISIBLE);
            pagesRead.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
            createNotification();


        });


    }


    public void launchCongratulations() {
        Intent intent = new Intent(this, Congratulations.class);
        startActivity(intent);
    }

    private void createNotification() {
        mNotifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Reading Rhythm Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Reading Rhythm Setter");
            mNotifyManager.createNotificationChannel(notificationChannel);

        }
        Intent notificationIntent = new Intent(this, MainActivity2.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, 0);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)

                .setContentText("Check your progress")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_book);
        if (checkFrequency.compareTo(BigDecimal.ONE) == 1) {
            notifyBuilder.setContentTitle("your allotted time to read " + checkFrequency + " pages has run out");
        } else {
            notifyBuilder.setContentTitle("your allotted time to read the page has run out");
        }
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());


    }
}

