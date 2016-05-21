package com.pygopar.helpers;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by daniel on 1/21/16.
 */
public class Command extends SugarRecord{
    public String command;
    public String os;
    public String version;
    public String note;
    /*@SerializedName("pk")*/
    public int myId;

    public Command() {

    }

    public Command(String command, String os, String version, String note) {
        this.command = command;
        this.os = os;
        this.version = version;
        this.note = note;
        this.myId = -1;
    }

    public Command(String command, String os, String version, String note, int id) {
        this.command = command;
        this.os = os;
        this.version = version;
        this.note = note;
        this.myId = id;
    }
}
