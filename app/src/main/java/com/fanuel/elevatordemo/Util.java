package com.fanuel.elevatordemo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {
    public static String concatenatedString(List<Integer> queList){
        Collections.sort(queList, new Comparator< Integer >() {
            @Override public int compare(Integer p1, Integer p2) {
                return p2 - p1; // Descending
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i <= queList.size();i++){
            if(i == 0){
                stringBuilder.append("[").append(queList.get(i));
            }else if(i == queList.size()){
                stringBuilder.append("]");
            }
            else{
                stringBuilder.append(",").append(queList.get(i));
            }
        }
        return stringBuilder.toString();
    }
    public static int getMinOfQue(List<Integer> callUpQue,List<Integer> callDownQue){
        int upQueMin = -1;
        int downQueMin = -1;
        if(callUpQue != null && !callUpQue.isEmpty()){
            upQueMin = Collections.min(callUpQue);
        }
        if(callDownQue != null && !callDownQue.isEmpty()){
            downQueMin = Collections.min(callDownQue);

        }
        if(upQueMin != -1 && downQueMin != -1){
            return getMin(upQueMin,downQueMin);
        }else{
            if(upQueMin != - 1){
                return upQueMin;
            }else{
                return downQueMin;
            }
        }
    }

    public static int getMaxOfQue(List<Integer> callUpQue,List<Integer> callDownQue){
        int upQueMax = -1;
        int downQueMax = -1;
        if(callUpQue != null && !callUpQue.isEmpty()){
            upQueMax = Collections.max(callUpQue);
        }
        if(callDownQue != null && !callDownQue.isEmpty()){
            downQueMax = Collections.max(callDownQue);

        }
        if(upQueMax != -1 && downQueMax != -1){
            return getMax(upQueMax,downQueMax);
        }else{
            if(upQueMax != - 1){
                return upQueMax;
            }else{
                return downQueMax;
            }
        }
    }

    public static int getMax (int first,int second){
        return Math.max(first, second);
    }
    public static int getMin (int first,int second){
        return Math.min(first, second);
    }
}
