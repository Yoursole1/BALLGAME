package me.yoursole.main.resources;

import java.io.File;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

public class RectObject implements Serializable {
    private int[] xyLower;
    private int[] xyUpper;

    private float bouncyness;
    private boolean isWinner;
    private boolean isVisible;
    private int sizeSet;
    private boolean kills;
    private boolean isCollidable;

    private Runnable r;
    private File texture;

    public RectObject(int x1, int y1, int x2, int y2, float bouncyness, boolean isWinner, boolean isVisible, int sizeSet, boolean kills, boolean isCollidable, Runnable custom, String texturePath){
        this.xyLower = new int[]{x1, y1};
        this.xyUpper = new int[]{x2, y2};

        this.bouncyness = bouncyness;
        this.isVisible = isVisible;
        this.sizeSet = sizeSet;
        this.isWinner = isWinner;
        this.kills = kills;
        this.isCollidable = isCollidable;

        this.r = custom;

        if(!texturePath.equals(""))
            this.texture = new File(texturePath);
        else
            this.texture = null;
    }

    public void setXys(int x1, int y1, int x2, int y2){
        this.xyLower = new int[]{x1, y1};
        this.xyUpper = new int[]{x2, y2};
    }
    public File getTexture(){
        return this.texture;
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
