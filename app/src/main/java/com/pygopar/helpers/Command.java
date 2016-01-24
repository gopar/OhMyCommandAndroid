package com.pygopar.helpers;

import com.orm.SugarRecord;

/**
 * Created by daniel on 1/21/16.
 */
public class Command extends SugarRecord{
    public String command;
    public String os;
    public String version;
    public String note;

    public Command() {

    }

    public Command(String command, String os, String version, String note) {
        this.command = command;
        this.os = os;
        this.version = version;
        this.note = note;
    }
}
