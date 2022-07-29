package com.fanuel.elevatordemo.docontrollers;

import com.fanuel.elevatordemo.dbmodels.DaoSession;
import com.fanuel.elevatordemo.dbmodels.ElevatorLog;
import com.fanuel.elevatordemo.dbmodels.ElevatorLogDao;

public class InsertOperations {
    public static String InsertElevatorLog(ElevatorLog elevatorLog, DaoSession daoSession)
    {

        ElevatorLogDao elevatorLogDao = daoSession.getElevatorLogDao();
        elevatorLogDao.insertOrReplace(elevatorLog);
        return elevatorLog.getCode();
    }
}
