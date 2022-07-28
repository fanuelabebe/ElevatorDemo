package com.fanuel.elevatorormtool;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class ElevatorORM {
    public static void main(String args[]) throws Exception {
        Schema model = new Schema(1, "com.fanuel.elevatordemo.dbmodels");

        // Table ElevatorLog
        Entity ElevatorLog = model.addEntity("ElevatorLog");
        ElevatorLog.addIdProperty().autoincrement().unique();
        ElevatorLog.addStringProperty("code");
        ElevatorLog.addDateProperty("date");
        ElevatorLog.addStringProperty("status");
        ElevatorLog.addStringProperty("remark");

        new DaoGenerator().generateAll(model, "./app/src/main/java");
    }
}