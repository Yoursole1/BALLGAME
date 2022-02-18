package me.yoursole.main.game;

import me.yoursole.main.game.gameValues.GameData;
import me.yoursole.main.resources.Level;
import me.yoursole.main.resources.Levels;
import me.yoursole.main.resources.RectObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class GameFrame extends JFrame {


    GameFrameMainPanel p;
    public GameFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        p = new GameFrameMainPanel();
        this.add(p);
        this.pack();
        p.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        p.setBackground(Color.GRAY);
    }
}

class GameFrameMainPanel extends GamePanel{
    double compression;
    public GameFrameMainPanel(){
        super(50, 0,0);


        if(!((double)(this.dimx)/(double)(this.dimy)==(double)(this.l.getDimxI())/(double)(this.l.getDimyI())) &&
                (this.l.getDimxI() > this.dimx || this.l.getDimyI() > this.dimy)){
            //if aspect ratio isn't correct for whatever reason

            if(this.l.getDimyI()>this.dimy){
                this.compression = (double) (this.dimy) / (double) (this.l.getDimyI());
            }else{ //why are you having an issue on the x direction??? Whatever ill fix it but cmon how does this happen
                this.compression = (double) (this.dimx) / (double) (this.l.getDimxI());
            }
            this.dimx *= this.compression;
            this.dimy *= this.compression;


            for(RectObject r : this.l.getObjects()){ //scale rectangles to match screensize
                r.setXys(
                        (int)(r.getXyLower()[0]*((double)this.dimx/(double)this.l.getDimxI())),
                        (int)(r.getXyLower()[1]*((double)this.dimy/(double)this.l.getDimyI())),
                        (int)(r.getXyUpper()[0]*((double)this.dimx/(double)this.l.getDimxI())),
                        (int)(r.getXyUpper()[1]*((double)this.dimy/(double)this.l.getDimyI()))
                );
            }
        }
        this.setPreferredSize(new Dimension(this.dimx, this.dimy));


        super.startTick();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        super.dimx = this.dimx;
        super.dimy = this.dimy;

        Graphics2D g2 = (Graphics2D) g;


        for(RectObject r : this.l.getObjects()){
            if(r.isVisible()){
                Rectangle2D.Double rect = new Rectangle2D.Double(r.getXyLower()[0],r.getXyLower()[1],r.getXyUpper()[0]-r.getXyLower()[0],r.getXyUpper()[1]-r.getXyLower()[1]);
                BufferedImage b = null;

                try {
                    b = ImageIO.read(r.getTexture());
                } catch (IOException | IllegalArgumentException ignored) {}

                Paint paint = b!=null?new TexturePaint(b, rect):new GradientPaint(0,0,Color.BLACK,1,1,r.isKills()?Color.RED:Color.BLUE);
                g2.setPaint(paint);

                g2.fill(rect);
                g2.setPaint(null);
                g2.setColor(Color.BLACK);

            }

        }

        float dist = distance(this.rel.x, this.rel.y, GameData.p.getLocx(), GameData.p.getLocy());
        float red = (float) (-1/(1+Math.pow(Math.E,dist/50-10)))+1;
        float green = (float) (1/(1+Math.pow(Math.E,dist/25-10)));

        g2.setColor(new Color(red, green, .5f));

        Ellipse2D.Double circle = new Ellipse2D.Double((int)(GameData.p.getLocx()-(GameData.p.getSize()/2)), (int)(GameData.p.getLocy()-(GameData.p.getSize()/2)), GameData.p.getSize(), GameData.p.getSize());
        g2.fill(circle);

        g2.setColor(Color.BLACK);

        g2.drawString(String.valueOf(this.levelTime/100f),5,15);

    }


    //LEVEL INFO --------------------------------------------------------
    private Level l = Levels.LEVEL1.getLevel(); //set to lvl 1


    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int dimx = screenSize.width;
    private int dimy = screenSize.height;

    //Player size set to 50
    //playerSize is up in the constructor under the super call
    //LEVEL INFO --------------------------------------------------------

    private boolean isPaused = false;
    private boolean isPlaying = false;

    private long time = 0;
    private long levelTime = 0;

    Point rel = new Point();
    @Override
    public void onTick() {

        if(!this.isPaused)this.levelTime++;

        this.time++;

        this.repaint();


        GameData.p.setLocx((int) (GameData.p.getLocx()+GameData.p.getX()));
        GameData.p.setLocy((int) (GameData.p.getLocy()+GameData.p.getY()));

        Point mouse = MouseInfo.getPointerInfo().getLocation();

        Point window = new Point();
        try{
            window = this.getLocationOnScreen();
        }catch(IllegalComponentStateException ignored){}



        this.rel = new Point(mouse.x-window.x, mouse.y-window.y);
        double centerX = GameData.p.getLocx();
        double centerY = GameData.p.getLocy();

        //is cursor in circle
        float dist = distance(centerX, centerY, rel.getX(),rel.getY());
        if(dist < GameData.p.getSize()/2f){
            if(this.isPaused){
                this.isPaused = false;
            }

            if (isPlaying && this.l.isPlayerKills()){
                killPlayer();
                return;
            }


        }else if(isPaused){
            return;
        }else{
            isPlaying = true;
        }

        if(inWindow(rel)){
            applyMovement(rel);
        }else{
            killPlayer();
            return;
        }

        collideWithWall();

        for(RectObject r : this.l.getObjects()){
            if (collideWithBoxes(r)) return;
        }


    }

    private void applyMovement(Point rel) {
        float dx = GameData.p.getX();
        float dy = GameData.p.getY();

        dx += (distancex(GameData.p.getLocx(), GameData.p.getLocy(), rel.x, rel.y)/100);
        dy += (distancey(GameData.p.getLocx(), GameData.p.getLocy(), rel.x, rel.y)/100);

        dx/=1.1;
        dy/=1.1;

        GameData.p.setX(dx);
        GameData.p.setY(dy);
    }

    private boolean collideWithBoxes(RectObject r) {
        if(withinX(r, 0, GameData.p.getLocx())){
            if(withinY(r.getXyLower(), 1, GameData.p.getLocy())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    GameData.p.setY(GameData.p.getY()*-1* r.getBouncyness());
                    GameData.p.setLocy((int) (r.getXyLower()[1]-GameData.p.getSize()/2f-this.compression));
                }

            }
            if(withinY(r.getXyUpper(), 1, GameData.p.getLocy())){
                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    GameData.p.setY(GameData.p.getY()*-1* r.getBouncyness());
                    GameData.p.setLocy((int) (r.getXyUpper()[1]+GameData.p.getSize()/2f+this.compression));
                }

            }
        }


        if(withinX(r, 1, GameData.p.getLocy())){
            if(withinY(r.getXyLower(), 0, GameData.p.getLocx())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    GameData.p.setX(GameData.p.getX()*-1* r.getBouncyness());
                    GameData.p.setLocx((int) (r.getXyLower()[0]-GameData.p.getSize()/2f-this.compression));
                }


            }
            if(withinY(r.getXyUpper(), 0, GameData.p.getLocx())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    GameData.p.setX(GameData.p.getX()*-1* r.getBouncyness());
                    GameData.p.setLocx((int) (r.getXyUpper()[0]+GameData.p.getSize()/2f + this.compression));
                }


            }
        }
        return false;
    }

    private boolean boxFunctions(RectObject r) {
        if (r.isWinner()) {
            this.isPaused = true;
            GameData.p.setX(0);
            GameData.p.setY(0);

            if(enumContains("LEVEL"+(this.l.getStage()+1))){
                this.l = Levels.valueOf("LEVEL"+(this.l.getStage()+1)).getLevel();
                killPlayer();
                return true;
            }
        }
        if (r.getSizeSet() != -1) {
            GameData.p.setSize((int) (r.getSizeSet() * this.compression));
        }
        if (r.isKills()) {
            killPlayer();
            return true;
        }
        r.runFunc();
        return false;
    }

    private boolean withinY(int[] xyLower, int i, double locx) {
        return Math.abs(xyLower[i] - locx) < GameData.p.getSize() / 2f;
    }

    private boolean withinX(RectObject r, int i, double locx) {
        return withinY(r.getXyLower(), i, locx) || locx > r.getXyLower()[i] && locx < r.getXyUpper()[i];
    }

    private void collideWithWall() {
        float halfSize = GameData.p.getSize()/2f;
        if(GameData.p.getLocx()+halfSize>=this.dimx || GameData.p.getLocx() - halfSize < 0){ //side wall
            GameData.p.setX(GameData.p.getX()*-1);
            GameData.p.setLocx(GameData.p.getLocx()<this.dimx/2f? (int) (GameData.p.getSize() / 2f+(this.compression)) : (int) (this.dimx - GameData.p.getSize() / 2f-(this.compression)));
        }

        if(GameData.p.getLocy()+halfSize>=this.dimy || GameData.p.getLocy() - halfSize < 0){ //top/bottom wall
            GameData.p.setY(GameData.p.getY()*-1);
            GameData.p.setLocy((int) (GameData.p.getLocy()<this.dimy/2f?GameData.p.getSize()/2f+(this.compression):this.dimy-GameData.p.getSize()/2f-(this.compression)));
        }
    }


    private void killPlayer(){
        GameData.p.setLocx(this.l.getRespawn().x);
        GameData.p.setLocy(this.l.getRespawn().y);
        GameData.p.setX(0);
        GameData.p.setY(0);
        this.isPaused = true;
        this.isPlaying = false;
        this.levelTime = 0;

        GameData.p.setSize((int) (GameData.p.getDefaultSize() * this.compression));
    }

    private static boolean enumContains(String input) {
        for (Levels r : Levels.values()) {
            if (r.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    private float distance(double x, double y, double x2, double y2){
        return (float) Math.sqrt(Math.pow(y2-y,2)+Math.pow(x2-x,2));
    }

    private float distancex(double x, double y, double x2, double y2){
        return (float) (x2-x);
    }
    private float distancey(double x, double y, double x2, double y2){
        return (float) (y2-y);
    }

    private boolean inWindow(Point p){
        return (p.getX()>=0 && p.getX()<this.dimx)&&(p.getY()>=0 && p.getY()<this.dimy);
    }
}
