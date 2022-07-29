package com.fanuel.elevatordemo.docontrollers;

import com.fanuel.elevatordemo.dbmodels.DaoSession;
import com.fanuel.elevatordemo.dbmodels.ElevatorLog;
import com.fanuel.elevatordemo.dbmodels.ElevatorLogDao;

public class UpdateOperations {
    public static void UpdateElevatorLog(ElevatorLog elevatorLog, DaoSession daoSession)
    {
        ElevatorLogDao fiveBirrDao = daoSession.getElevatorLogDao();
        fiveBirrDao.update(elevatorLog);
    }
}
