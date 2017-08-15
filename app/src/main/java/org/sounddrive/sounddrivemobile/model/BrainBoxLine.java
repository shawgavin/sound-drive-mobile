package org.sounddrive.sounddrivemobile.model;

import android.util.Log;

public class BrainBoxLine {
    private float xCenter;
    private float yCenter;
    private float xPos;
    private float yPos;
    private float xMax;
    private float yMax;

    public static BrainBoxLine fromLine(String line) {
        if ((line == null) || ("".equals(line.trim())))
            return null;

        String[] split = line.trim().split(",");

        if (split.length < 6)
            return null;

        BrainBoxLine brainBoxLine = new BrainBoxLine();
        try {
            brainBoxLine.setxCenter(Float.valueOf(split[0]));
            brainBoxLine.setyCenter(Float.valueOf(split[1]));
            brainBoxLine.setxPos(Float.valueOf(split[2]));
            brainBoxLine.setyPos(Float.valueOf(split[3]));
            brainBoxLine.setxMax(Float.valueOf(split[4]));
            brainBoxLine.setyMax(Float.valueOf(split[5]));

        } catch (Exception ex) {
            Log.w(BrainBoxLine.class.getName(), "Failed to parse line string: " + line);
            return brainBoxLine;
        }

        return brainBoxLine;
    }

    public float getxCenter() {
        return xCenter;
    }

    public void setxCenter(float xCenter) {
        this.xCenter = xCenter;
    }

    public float getyCenter() {
        return yCenter;
    }

    public void setyCenter(float yCenter) {
        this.yCenter = yCenter;
    }

    public float getxPos() {
        return xPos;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public float getxMax() {
        return xMax;
    }

    public void setxMax(float xMax) {
        this.xMax = xMax;
    }

    public float getyMax() {
        return yMax;
    }

    public void setyMax(float yMax) {
        this.yMax = yMax;
    }

}
