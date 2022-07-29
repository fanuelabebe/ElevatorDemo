package com.fanuel.elevatordemo.docontrollers;

import com.fanuel.elevatordemo.dbmodels.DaoSession;
import com.fanuel.elevatordemo.dbmodels.ElevatorLog;
import com.fanuel.elevatordemo.dbmodels.ElevatorLogDao;

public class SelectOperations {
    public static ElevatorLog SelectElevatorLogById(DaoSession daoSession, String status){
        ElevatorLogDao elevatorLogDao = daoSession.getElevatorLogDao();
        return elevatorLogDao.queryBuilder().where(ElevatorLogDao.Properties.Status.eq(status)).unique();
    }
}
