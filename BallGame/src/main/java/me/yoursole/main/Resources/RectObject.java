package me.yoursole.main.Resources;

import java.util.function.Consumer;

public class RectObject{
    private int[] xyLower;
    private int[] xyUpper;

    private float bouncyness;
    private boolean isWinner;
    private boolean isVisible;
    private int sizeSet;
    private boolean kills;
    private boolean isCollidable;

    private Runnable r;

    public RectObject(int x1, int y1, int x2, int y2, float bouncyness, boolean isWinner, boolean isVisible, int sizeSet, boolean kills, boolean isCollidable, Runnable custom){
        this.xyLower = new int[]{x1, y1};
        this.xyUpper = new int[]{x2, y2};

        this.bouncyness = bouncyness;
        this.isVisible = isVisible;
        this.sizeSet = sizeSet;
        this.isWinner = isWinner;
        this.kills = kills;
        this.isCollidable = isCollidable;

        this.r = custom;
    }

    public int[] getXyLower(){
        return this.xyLower;
    }

    public int[] getXyUpper() {
        return this.xyUpper;
    }

    public float getBouncyness() {
        return this.bouncyness;
    }

    public boolean isWinner() {
        return this.isWinner;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public int getSizeSet() {
        return this.sizeSet;
    }

    public boolean isKills() {
        return kills;
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public void runFunc(){
        r.run();
    }
}