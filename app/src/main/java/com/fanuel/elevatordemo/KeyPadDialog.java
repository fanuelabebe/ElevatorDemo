package com.fanuel.elevatordemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;

public class KeyPadDialog implements ElevatorButtonAdapter.ItemClicked{
    Activity activity;
    boolean isUp = false;
    AlertDialog alertDialog;
    TextView titleText;
    ElevatorButtonAdapter elevatorButtonAdapter;
    DialogKeyClick dialogKeyClick;

    CardView zeroCardView;
    CardView firstCardView;
    CardView secondCardView;
    CardView thirdCardView;
    CardView fourthCardView;
    CardView fifthCardView;


    interface DialogKeyClick{
        void OnDialogKeyClicked(int value,boolean isUp);
    }
    public KeyPadDialog(Activity activity) {
        this.activity = activity;

    }

    public void startKeyPadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View root = inflater.inflate(R.layout.key_pad_lay,null);
        initViews(root);
        builder.setView(root);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }
    public void initViews(View root){
        titleText = root.findViewById(R.id.lTItleTV);
        zeroCardView = root.findViewById(R.id.zeroPadCV);
        firstCardView = root.findViewById(R.id.firstPadCV);
        secondCardView = root.findViewById(R.id.secondPadCV);
        thirdCardView = root.findViewById(R.id.thirdPadCV);
        fourthCardView = root.findViewById(R.id.fourthPadCV);
        fifthCardView = root.findViewById(R.id.fifthPadCV);

    }
    public void setVisibilityToPad(int currentLevel){
        switch (currentLevel){
            case 0:
                zeroCardView.setVisibility(View.INVISIBLE);
                zeroCardView.setEnabled(false);
                break;
            case 1:
                firstCardView.setVisibility(View.INVISIBLE);
                firstCardView.setEnabled(false);
                break;
            case 2:
                secondCardView.setVisibility(View.INVISIBLE);
                secondCardView.setEnabled(false);
                break;
            case 3:
                thirdCardView.setVisibility(View.INVISIBLE);
                thirdCardView.setEnabled(false);
                break;
            case 4:
                fourthCardView.setVisibility(View.INVISIBLE);
                fourthCardView.setEnabled(false);
                break;
            case 5:
                fifthCardView.setVisibility(View.INVISIBLE);
                fifthCardView.setEnabled(false);
                break;
        }
    }

    public void setOnClickListeners(int currentLevel,boolean isUp){
        this.isUp = isUp;
        setVisibilityToPad(currentLevel);
        zeroCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogKeyClick.OnDialogKeyClicked(0,isUp);
            }
        });
        firstCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogKeyClick.OnDialogKeyClicked(1,isUp);
            }
        });
        secondCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogKeyClick.OnDialogKeyClicked(2,isUp);
            }
        });
        thirdCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogKeyClick.OnDialogKeyClicked(3,isUp);
            }
        });
        fourthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogKeyClick.OnDialogKeyClicked(4,isUp);
            }
        });
        fifthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogKeyClick.OnDialogKeyClicked(5,isUp);
            }
        });
    }

//    public void updateGrid(List<String> keyPadList,boolean isUp){
//        this.isUp = isUp;
//        elevatorButtonAdapter = new ElevatorButtonAdapter(activity,keyPadList);
//        elevatorButtonAdapter.itemClicked = this;
//        gridView.setAdapter(elevatorButtonAdapter);
//    }

    public boolean isDialogShowing(){
        if(alertDialog != null && alertDialog.isShowing()){
            return true;
        }else{
            return false;
        }
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

    public void setTitle(String title){
        titleText.setText(title);
    }

    @Override
    public void OnItemClicked(int value) {
        dialogKeyClick.OnDialogKeyClicked(value,isUp);
    }

    public void OnFifthPadClick(View fp){

    }
}
