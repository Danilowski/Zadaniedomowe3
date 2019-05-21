package com.example.zadaniedomowe3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int FADE_DURATION = 1500;
    public static final int START_OFFSET = 1000;
    public static final int THRESHOLD = 240;
    public static final int SHAKE_COUNT = 2;
    private static Random RANDOM = new Random();
    private SensorManager sensorManager;
    private Sensor sensor;
    private float lastX, lastY, lastZ;
    private int shakeCount = 0;
    private TextView msgTv;
    private ImageView ball;
    private Animation ballAnimation;
    private ArrayList<String> answers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ball = (ImageView) findViewById(R.id.ball);
        msgTv = (TextView) findViewById(R.id.msgTv);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ballAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        answers = loadAnswers();
    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        showAnswer(getString(R.string.shake_me), false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if (isShakeEnough(event.values[0], event.values[1], event.values[2])){
                showAnswer(getAnswer(), false);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isShakeEnough(float x, float y, float z) {
        double force = 0d;

        force += Math.pow((x - lastX) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((y - lastY) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((z - lastZ) / SensorManager.GRAVITY_EARTH, 2.0);

        force = Math.sqrt(force);

        lastX = x;
        lastY = y;
        lastZ = z;

        if (force > ((float) THRESHOLD / 150.0f)){
            ball.startAnimation(ballAnimation);
            shakeCount++;

            if (shakeCount > SHAKE_COUNT) {
                shakeCount = 0;
                lastX = 0;
                lastY = 0;
                lastZ = 0;
                int shakecoeff = (int) (abs(x + y + z) % answers.size());
                return true;
            }
        }
        return false;
    }
    private void showAnswer(String answer, boolean withAnim) {
        if (withAnim) {
            ball.startAnimation(ballAnimation);
        }

        msgTv.setVisibility(View.INVISIBLE);
        msgTv.setText(answer);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setStartOffset(START_OFFSET);
        msgTv.setVisibility(View.VISIBLE);
        animation.setDuration(FADE_DURATION);

        msgTv.startAnimation(animation);
    }
    private String getAnswer() {
        int randomInt = RANDOM.nextInt(answers.size());
        return answers.get(randomInt);
    }
    public ArrayList<String> loadAnswers() {
        ArrayList<String> list = new ArrayList<>();
        String[] tab = getResources().getStringArray(R.array.answers);

        if (tab != null && tab.length > 0){
            for (String str : tab){
                list.add(str);
            }
        }
        return list;
    }
}