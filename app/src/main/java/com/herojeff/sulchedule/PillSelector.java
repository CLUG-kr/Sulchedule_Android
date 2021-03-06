package com.herojeff.sulchedule;

public class PillSelector {
    public static final int whom = 1;
    public static final int where = 2;
    public static final int expense = 3;
    int mode;

    public PillSelector(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getModeString() {
        switch (mode) {
            case 1:
                return "누구와";
            case 2:
                return "어디서";
            case 3:
                return "얼마";
        }
        return "";
    }

}
