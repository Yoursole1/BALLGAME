package me.yoursole.main.game;

import me.yoursole.main.resources.Player;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public abstract class GamePanel extends JPanel {
    transient protected Player player;
    protected int dimx;
    protected int dimy;

    public GamePanel(int playerSize, int playerX, int playerY){
        super();
        this.player = new Player(0,0,playerSize, playerX, playerY,playerSize);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                onTick();
            }
        },10, 10);
    }


    public abstract void onTick();
}
