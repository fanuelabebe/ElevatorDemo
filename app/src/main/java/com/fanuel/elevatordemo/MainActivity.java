package com.fanuel.elevatordemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fanuel.elevatordemo.dbmodels.DaoMaster;
import com.fanuel.elevatordemo.dbmodels.DaoSession;
import com.fanuel.elevatordemo.dbmodels.ElevatorLog;
import com.fanuel.elevatordemo.docontrollers.InsertOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements KeyPadDialog.DialogKeyClick{

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
    private static Handler elevatorMoveHandler;
    private static Handler elevatorOpenHandler;
    private static Handler keyPadHandler;
    private static Handler maintenanceHandler;

    List<Integer> callUpQue;
    List<Integer> callDownQue;

    public int ELEVATOR_DEFAULT_RES_ID = android.R.color.white;
    public int ELEVATOR_MOVING_RES_ID = android.R.color.holo_green_light;
    public int ELEVATOR_STOPPED_RES_ID = android.R.color.holo_purple;
    public int ELEVATOR_BROKEN_RES_ID = android.R.color.holo_red_dark;

    public String ELEVATOR_MOVING_STATUS = "Moving";
    public String ELEVATOR_STOPPED_STATUS = "Stopped";
    public String ELEVATOR_BROKEN_STATUS = "Broken";
    public String ELEVATOR_REPAIRED_STATUS = "Repaired";

    public int ELEVATOR_MOVING_TIME_OUT = 2000;
    public int ELEVATOR_STOPPED_TIME_OUT = 3000;

    TextView elevatorStatusTextView;
    TextView currentStackUpTextView;
    TextView currentStackDownTextView;
    TextView maintenanceCountTextView;
    int maintenanceCount = 0;
    int maintenanceMax = 30;
    boolean isMoving = false;
    KeyPadDialog keyPadDialog;

    Button repairButton;

    public static Random randomNumb = new Random();
    DatabaseUpgradeHelperCommon helper;
    SQLiteDatabase db;
    DaoMaster daoMaster;
    DaoSession daoSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDaoSession();
        keyPadDialog = new KeyPadDialog(this);
        keyPadDialog.dialogKeyClick = this;
        initViews();
        initQueLists();
        initHandler();
//        startElevator(0);

    }

    public void initDaoSession(){
        helper = new DatabaseUpgradeHelperCommon(this, "model", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

    }

    public void initQueLists(){
        callDownQue = new ArrayList<>();
        callUpQue = new ArrayList<>();
    }

    public void startElevator(int callLevel){
        isGoingUp = callLevel > currentLevel;
        isMoving = true;
        ElevatorThread elevatorThread = new ElevatorThread();
        isThreadRunning = true;
        elevatorThread.start();
    }

    public void initHandler(){
        elevatorMoveHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Pair<View,View> message = ( Pair<View,View>) msg.obj;
                if(message != null){
                    if(message.first != null) {
                        setBackGroundColorOfView(message.first, ELEVATOR_MOVING_RES_ID);
                        updateElevatorStatus(ELEVATOR_MOVING_STATUS);
                        updateStackUI();
                        updateMaintenanceCount(String.valueOf(maintenanceCount));

                    }
                    if(message.second != null) {
                        setBackGroundColorOfView(message.second, ELEVATOR_DEFAULT_RES_ID);
                    }
                }

            }
        };

        elevatorOpenHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Pair<View,Integer> message = ( Pair<View,Integer>) msg.obj;
                if(message.first != null){
                    setBackGroundColorOfView(message.first, message.second);
                    updateElevatorStatus(ELEVATOR_STOPPED_STATUS);
                    updateStackUI();
                }

            }
        };

        keyPadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Pair<Integer,Boolean> message = ( Pair<Integer,Boolean>) msg.obj;
                if(message.first != null){
                    startKeyPadDialog(message.first,message.second);
                }

            }
        };
        maintenanceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Toast.makeText(MainActivity.this,"Elevator Broken needs maintenance",Toast.LENGTH_LONG).show();
                doMaintenance(true);

            }
        };
    }

    public void updateStackUI(){
        updateCurrentUpStackListUI();
        updateCurrentDownStackListUI();
    }

    public void initViews(){

        elevatorStatusTextView = findViewById(R.id.elevStatusTV);
        currentStackUpTextView = findViewById(R.id.currUpStackTV);
        currentStackDownTextView = findViewById(R.id.currDownStackTV);
        maintenanceCountTextView = findViewById(R.id.maintainCountTV);
        repairButton = findViewById(R.id.repairB);

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

    public void updateElevatorStatus(String status){
        elevatorStatusTextView.setText(status);
    }

    public void updateMaintenanceCount(String count){
        maintenanceCountTextView.setText(count);
    }

    public void updateCurrentUpStackListUI(){
        if(callUpQue != null && !callUpQue.isEmpty()){

            currentStackUpTextView.setText(Util.concatenatedString(callUpQue));
        }else{
            String emptyList = "[]";
            currentStackUpTextView.setText(emptyList);
        }
    }
    public void updateCurrentDownStackListUI(){
        if(callDownQue != null && !callDownQue.isEmpty()){

            currentStackDownTextView.setText(Util.concatenatedString(callDownQue));
        }else{
            String emptyList = "[]";
            currentStackDownTextView.setText(emptyList);
        }
    }



    @Override
    public void OnDialogKeyClicked(int value,boolean isUp) {
        Log.v(TAG,"Item CLicked: "+isUp);
        isMoving = true;
        dismissKeyPad();
        if(isUp && !callUpQue.contains(value)){
            callUpQue.add(value);
        }else if (!callDownQue.contains(value)){
            callDownQue.add(value);
        }
    }

    public void dismissKeyPad(){
        if(keyPadDialog != null && keyPadDialog.isDialogShowing()){
            keyPadDialog.dismissDialog();
        }
    }


    private class ElevatorThread extends Thread{

        @Override
        public void run() {
            while(isThreadRunning){
                try {
                    moveElevatorInCurrentDirection();
                    timeOutThread(ELEVATOR_MOVING_TIME_OUT);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeLevelAndChangeButtonState(int currentLevel,boolean isUp){
        if(isUp){
            removeValueFromUpQue(currentLevel);
        }else{
            removeValueFromDownQue(currentLevel);
        }
        ConstraintLayout currentLayout = layoutMap.get(currentLevel);
        View view;
        if(isUp) {
            view = getChildUpElevButtonViewFromParent(currentLayout);
        }else{
            view = getChildDownElevButtonViewFromParent(currentLayout);
        }
        updateElevatorOpenUiFromThread(view,ELEVATOR_DEFAULT_RES_ID);
    }

    public void updateStoppedElevatorUI(int currentLevel){
        ConstraintLayout currentLayout = layoutMap.get(currentLevel);
        View childView = getChildElevViewFromParent(currentLayout);
        setCurrentLevelBackground();
        updateElevatorOpenUiFromThread(childView,ELEVATOR_STOPPED_RES_ID);

    }

    public void moveElevatorInCurrentDirection(){
        elevatorMax = Util.getMaxOfQue(callUpQue,callDownQue);
        elevatorMin = Util.getMinOfQue(callUpQue,callDownQue);
        Log.v(TAG, "CurrentFloor: " + currentLevel);
        Log.v(TAG, "MaxFloor: " + elevatorMax);
        Log.v(TAG, "MinFloor: " + elevatorMin);
        if(isGoingUp){

            Log.v(TAG, "Contains callUpQue: " + callUpQue.contains(currentLevel));
            if(callUpQue.contains(currentLevel)) {
                Log.v(TAG, "StopElevator: Up Called" );

                //find elevator UI View and change state

                updateStoppedElevatorUI(currentLevel);
                timeOutThread(ELEVATOR_MOVING_TIME_OUT);


                //turn off going up button for current level and empty list from upQue
                removeLevelAndChangeButtonState(currentLevel,true);
                timeOutThread(ELEVATOR_STOPPED_TIME_OUT);

                //show key Pad and wait 10 seconds for the response otherwise continue
                waitForKeyPadResponse(true);

            }else{
                Log.v(TAG, "NextElevator: Up Called");
                   if(elevatorMax == -1 && elevatorMin == -1) {
                        isThreadRunning = false;
                        return;
                    }
                if (currentLevel >= elevatorMax) {
                    updateStoppedElevatorUI(currentLevel);

                    isGoingUp = false;

                } else { maintenanceCount++;
                    setCurrentLevelBackground();
                    maintenanceCheck();
                    currentLevel++;
                }
            }
        }else{
            if(callDownQue.contains(currentLevel)) {
                Log.v(TAG, "StopElevator: Down Called" );

                updateStoppedElevatorUI(currentLevel);
                timeOutThread(ELEVATOR_MOVING_TIME_OUT);

                //turn off going up button for current level and empty list from downQue
                removeLevelAndChangeButtonState(currentLevel,false);
                timeOutThread(ELEVATOR_STOPPED_TIME_OUT);

                //show key Pad and wait 10 seconds for the response otherwise continue
                waitForKeyPadResponse(false);

            }else {
                Log.v(TAG, "NextElevator: Down Called");
                if(elevatorMin == -1 && elevatorMax == -1) {
                    isThreadRunning = false;
                    return;
                }
                if (currentLevel <= elevatorMin) {
                    updateStoppedElevatorUI(currentLevel);

                    isGoingUp = true;
                } else {
                    maintenanceCount++;
                    setCurrentLevelBackground();

                    maintenanceCheck();
                    currentLevel--;
                }
            }
        }

    }
    public void maintenanceCheck(){
        if(maintenanceCount > maintenanceMax){
            Message msg = maintenanceHandler.obtainMessage();
            msg.obj = "broken";
            maintenanceHandler.sendMessage(msg);
        }
    }
    public void doMaintenance(boolean isBroken){
        dismissKeyPad();
        if(isBroken){
            saveElevatorLog(ELEVATOR_BROKEN_STATUS);
            dismissKeyPad();
            isThreadRunning = false;
            repairButton.setVisibility(View.VISIBLE);
        }else{
            saveElevatorLog(ELEVATOR_REPAIRED_STATUS);
            maintenanceCount = 0;
            repairButton.setVisibility(View.GONE);
        }
    }

    public void OnRepairClick(View v){
        doMaintenance(false);
    }

    public void waitForKeyPadResponse(boolean isGoingUp){
        int timeoutCount = 0;
        isMoving = false;
        showKeyPad(isGoingUp,currentLevel);
        while (isMoving == false){
            if(timeoutCount == 5){
                if(keyPadDialog.isDialogShowing()){
                    keyPadDialog.dismissDialog();
                }
                break;
            }
            timeOutThread(ELEVATOR_MOVING_TIME_OUT);
            timeoutCount++;
        }
    }

    public void removeValueFromUpQue(int value){
        if(callUpQue != null && !callUpQue.isEmpty() ){
            callUpQue.remove((Integer) value);

        }

    }

    public void removeValueFromDownQue(int value){
        if(callDownQue != null && !callDownQue.isEmpty() ){
            callDownQue.remove((Integer) value);
        }

    }

    public void timeOutThread(int timeOut){
        try {
            Thread.sleep(timeOut);
        }catch (Exception e){
            e.printStackTrace();
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
        View firstView = getChildElevViewFromParent(currentLayout);
        View secondView = getChildElevViewFromParent(previousLayout);

        updateMovingUIFromThread(firstView,secondView);
    }

    public View getChildElevViewFromParent(ConstraintLayout parent){
        if(parent != null) {
            CardView cardView = parent.findViewById(R.id.elevCV);
            if (cardView != null) {
                return cardView;
            }
        }
        return null;
    }

    public View getChildUpElevButtonViewFromParent(ConstraintLayout parent){
        if(parent != null) {

            CardView cardView = parent.findViewById(R.id.upAndDownCV);
            if (cardView != null) {
                ConstraintLayout constraintLayout = cardView.findViewById(R.id.upAndDownCL);
                if(constraintLayout != null){
                    CardView cardView1 = constraintLayout.findViewById(R.id.upAr);
                    if(cardView1 != null) {
                        return cardView1;
                    }
                }

            }
        }
        return null;
    }

    public View getChildDownElevButtonViewFromParent(ConstraintLayout parent){
        if(parent != null) {
            CardView cardView = parent.findViewById(R.id.upAndDownCV);
            if (cardView != null) {
                ConstraintLayout constraintLayout = cardView.findViewById(R.id.upAndDownCL);
                if(constraintLayout != null){
                    CardView cardView1 = constraintLayout.findViewById(R.id.downAr);
                    if(cardView1 != null) {
                        return cardView1;
                    }
                }

            }
        }
        return null;
    }

    public void updateMovingUIFromThread(View currentView,View previousView){
        Message msg = elevatorMoveHandler.obtainMessage();
        msg.obj = new Pair<>(currentView,previousView);
        elevatorMoveHandler.sendMessage(msg);

    }

    public void updateElevatorOpenUiFromThread(View viewToBeUpdated,int resId){
        Message msg = elevatorOpenHandler.obtainMessage();
        msg.obj = new Pair<>(viewToBeUpdated,resId);
        elevatorOpenHandler.sendMessage(msg);
    }



    public void addToQueAndStartElevator(View view,boolean isUp,int value){
        if(isUp){
            if(callUpQue != null && !callUpQue.contains(value) && currentLevel != value){
                callUpQue.add(value);
                updateCurrentUpStackListUI();
                setBackGroundColorOfView(view, ELEVATOR_MOVING_RES_ID);
            }
        }else{
            if(callDownQue != null && !callDownQue.contains(value) && currentLevel != value){
                callDownQue.add(value);
                updateCurrentDownStackListUI();
                setBackGroundColorOfView(view, ELEVATOR_MOVING_RES_ID);
            }

        }
        if(!isThreadRunning){
            startElevator(value);
        }
    }
    public void OnZeroUpClick(View zu){
        addToQueAndStartElevator(zu,true,0);

    }

    public void OnFirstUpClick(View fu){
        addToQueAndStartElevator(fu,true,1);
    }

    public void OnFirstDownClick(View fd){
        addToQueAndStartElevator(fd,false,1);
    }

    public void OnSecondUpClick(View zu){
        addToQueAndStartElevator(zu,true,2);
    }


    public void OnSecondDownClick(View zu){
        addToQueAndStartElevator(zu,false,2);
    }

    public void OnThirdUpClick(View tu){
        addToQueAndStartElevator(tu,true,3);
    }

    public void OnThirdDownClick(View td){
        addToQueAndStartElevator(td,false,3);
    }
    public void OnFourthUpClick(View fu){
        addToQueAndStartElevator(fu,true,4);
    }

    public void OnFourthDownClick(View fd){
        addToQueAndStartElevator(fd,false,4);
    }

    public void OnFifthDownClick(View fd){
        addToQueAndStartElevator(fd,false,5);
    }

    public void startKeyPadDialog(int currentLevel,boolean isUp){
        keyPadDialog.startKeyPadDialog();
        keyPadDialog.setOnClickListeners(currentLevel,isUp);
    }

    public void showKeyPad(boolean isUp,int currentLevel){
//        List<String> removedKeyPadArray = keyPadArray;
//        removedKeyPadArray.remove(String.valueOf(currentLevel));
//        Collections.sort(removedKeyPadArray, new Comparator< String >() {
//            @Override public int compare(String p1, String p2) {
//                return  Integer.parseInt(p1) - Integer.parseInt(p2); // Ascending
//            }
//        });
        Message msg = keyPadHandler.obtainMessage();
        msg.obj = new Pair<>(currentLevel,isUp);
        keyPadHandler.sendMessage(msg);
    }

    public void saveElevatorLog(String status){
        ElevatorLog elevatorLog = new ElevatorLog();
        elevatorLog.setCode("Elev"+randomNumb.nextInt());
        elevatorLog.setDate(new Date());
        elevatorLog.setStatus(status);
        InsertOperations.InsertElevatorLog(elevatorLog,daoSession);
    }

}