package com.fanuel.elevatordemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getName();
    public static boolean isThreadRunning = false;
    int elevatorMin = 0;
    int currentLevel = 0;
    int elevatorMax = 5;
    boolean isGoingUp = true;
    ConstraintLayout levelFiveConstraintLay;
    ConstraintLayout levelFourConstraintLay;
    ConstraintLayout levelThreeConstraintLay;
    ConstraintLayout levelTwoConstraintLay;
    ConstraintLayout levelOneConstraintLay;
    ConstraintLayout levelZeroConstraintLay;
    HashMap<Integer,ConstraintLayout> layoutMap;
    private static Handler handler;
    View viewToBeUpdated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initHandler();
        startElevator();

    }

    public void startElevator(){
        ElevatorThread elevatorThread = new ElevatorThread();
        isThreadRunning = true;
        elevatorThread.start();
    }

    public void initHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Pair<View,View> message = ( Pair<View,View>) msg.obj;
                if(message != null){
                    if(message.first != null) {
                        setBackGroundColorOfView(message.first, android.R.color.holo_green_light);
                    }
                    if(message.second != null) {
                        setBackGroundColorOfView(message.second, android.R.color.white);
                    }
                }

            }
        };
    }

    public void initViews(){
        levelFiveConstraintLay = findViewById(R.id.levelFiveLay);
        levelFourConstraintLay = findViewById(R.id.levelFourLay);
        levelThreeConstraintLay = findViewById(R.id.levelThreeLay);
        levelTwoConstraintLay = findViewById(R.id.levelTwoLay);
        levelOneConstraintLay = findViewById(R.id.levelOneLay);
        levelZeroConstraintLay = findViewById(R.id.levelZeroLay);
        layoutMap = new HashMap<>();
        layoutMap.put(0,levelZeroConstraintLay);
        layoutMap.put(1,levelOneConstraintLay);
        layoutMap.put(2,levelTwoConstraintLay);
        layoutMap.put(3,levelThreeConstraintLay);
        layoutMap.put(4,levelFourConstraintLay);
        layoutMap.put(5,levelFiveConstraintLay);

    }

    public void setBackGroundColorOfView(View view,int resId){
        view.setBackgroundResource(resId);
    }



    private class ElevatorThread extends Thread{

        @Override
        public void run() {
            while(isThreadRunning){
                try {
                    moveElevatorInCurrentDirection();
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void moveElevatorInCurrentDirection(){
        if(isGoingUp){
            if(currentLevel >= elevatorMax){
                setCurrentLevelBackground();
                isGoingUp = false;
            }else {

                Log.v(TAG, "CurrentFloor: " + currentLevel);
                setCurrentLevelBackground();
                currentLevel++;
            }
        }else{
            if(currentLevel <= elevatorMin){
                setCurrentLevelBackground();
                isGoingUp = true;
            }else {
                setCurrentLevelBackground();
                Log.v(TAG, "CurrentFloor: " + currentLevel);
                currentLevel--;
            }
        }

    }

    public void setCurrentLevelBackground(){
        ConstraintLayout currentLayout = layoutMap.get(currentLevel);
        ConstraintLayout previousLayout;
        if(isGoingUp){
            previousLayout = layoutMap.get(currentLevel - 1);
        }else{
            previousLayout = layoutMap.get(currentLevel + 1);
        }
        setBackgroundOfChildLayout(currentLayout,previousLayout);
    }

    public void setBackgroundOfChildLayout(ConstraintLayout currentLayout,ConstraintLayout previousLayout){
        View firstView = null;
        View secondView = null;
        if(currentLayout != null) {
            CardView cardView = currentLayout.findViewById(R.id.elevCV);
            if (cardView != null) {
                firstView = cardView;
            }
        }

        if(previousLayout != null) {
            CardView cardView = previousLayout.findViewById(R.id.elevCV);
            if (cardView != null) {
                secondView = cardView;
            }
        }

        updateUIFromThread(firstView,secondView);
    }

    public void updateUIFromThread(View currentView,View previousView){
        Message msg = handler.obtainMessage();
        msg.obj = new Pair<>(currentView,previousView);
        handler.sendMessage(msg);
    }
}