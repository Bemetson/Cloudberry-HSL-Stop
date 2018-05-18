package com.bemetson.cloudberryhslstop;

import java.io.Serializable;

public class BusStopData implements Serializable {

    private String stopName;
    private boolean hasIcon;
    private boolean addIcon;
    private int iconType;

    public BusStopData(String stopName, boolean addIcon, boolean hasIcon, int iconType) {
        this.stopName = stopName;
        this.addIcon = addIcon;
        this.hasIcon = hasIcon;
        this.iconType = iconType;
    }

    public String getStopName() {
        return this.stopName;
    }

    public boolean getAddIcon() {
        return this.addIcon;
    }

    public int getIconType() {
        return this.iconType;
    }

    public boolean getHasIcon() {
        return this.hasIcon;
    }

    public void setIcon(int value) {
        this.addIcon = true;
        this.iconType = value;
    }

    public void reset() {
        this.addIcon = false;

    }

}
