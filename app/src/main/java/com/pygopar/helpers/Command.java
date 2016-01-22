package com.pygopar.helpers;

/**
 * Created by daniel on 1/21/16.
 */
public class Command {
    String command;
    String os;
    String version;
    String note;

    public Command(String command, String os, String version, String note) {
        this.command = command;
        this.os = os;
        this.version = version;
        this.note = note;
    }
}
