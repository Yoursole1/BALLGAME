package me.yoursole.main.resources;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Level implements Serializable {
    private ArrayList<RectObject> objects;
    private Point respawn;
    private boolean playerKills;
    private int dimxI;

    public ArrayList<RectObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<RectObject> objects) {
        this.objects = objects;
    }

    public Point getRespawn() {
        return respawn;
    }

    public void setRespawn(Point respawn) {
        this.respawn = respawn;
    }

    public boolean isPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(boolean playerKills) {
        this.playerKills = playerKills;
    }

    public int getDimxI() {
        return dimxI;
    }

    public void setDimxI(int dimxI) {
        this.dimxI = dimxI;
    }

    public int getDimyI() {
        return dimyI;
    }

    public void setDimyI(int dimyI) {
        this.dimyI = dimyI;
    }

    private int dimyI;

    public Level(ArrayList<RectObject> objects, Point respawn, boolean playerKills, int dimxI, int dimyI){
        this.objects = objects;
        this.respawn = respawn;
        this.playerKills = playerKills;
        this.dimxI = dimxI;
        this.dimyI = dimyI;
    }
}
