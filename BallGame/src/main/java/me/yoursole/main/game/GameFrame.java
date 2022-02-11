package me.yoursole.main.game;

import me.yoursole.main.resources.Level;
import me.yoursole.main.resources.RectObject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
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





    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        super.dimx = this.dimx;
        super.dimy = this.dimy;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color((float) (.5*Math.sin(this.time/100f)+.5), (float) (.5*Math.cos(this.time/100f)+.5),1 ));

        Ellipse2D.Double circle = new Ellipse2D.Double((int)(player.getLocx()-(player.getSize()/2)), (int)(player.getLocy()-(player.getSize()/2)), player.getSize(), player.getSize());
        g2.fill(circle);

        g2.setColor(Color.BLACK);

        g2.drawString(String.valueOf(this.levelTime/100f),5,15);

        for(RectObject r : this.l.getObjects()){
            if(r.isVisible()){
                if(r.isWinner()){
                    g2.setColor(Color.GREEN);
                    g2.drawRect(r.getXyLower()[0],r.getXyLower()[1],r.getXyUpper()[0]-r.getXyLower()[0],r.getXyUpper()[1]-r.getXyLower()[1]);
                    g2.setColor(Color.BLACK);
                }else{
                    g2.drawRect(r.getXyLower()[0],r.getXyLower()[1],r.getXyUpper()[0]-r.getXyLower()[0],r.getXyUpper()[1]-r.getXyLower()[1]);
                }

            }

        }

    }

    private Color c = Color.BLACK;

    //LEVEL INFO --------------------------------------------------------
    private Level l = new Level(new ArrayList<>(){{
        add(new RectObject(0,100,600,150,0f, false, true, 50, false, true,()->{

        }));
        add(new RectObject(620,100,1400,150,0f, false, true, 50, false, true,()->{

        }));
        add(new RectObject(1300,150,1400,1000,0f, false, true, 50, false, true,()->{

        }));

        add(new RectObject(1400,900,1500,1000,0f, false, false, 15, false, false,()->{
            player.setY(player.getY()+5);
        }));

        add(new RectObject(500,500,600,600,0f, true, true, 15, false, true,()->{

        }));
    }},new Point(50,50),false,1500,1000);


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
    @Override
    public void onTick() {

        if(!this.isPaused)this.levelTime++;

        this.time++;

        this.repaint();


        this.player.setLocx((int) (this.player.getLocx()+this.player.getX()));
        this.player.setLocy((int) (this.player.getLocy()+this.player.getY()));

        Point mouse = MouseInfo.getPointerInfo().getLocation();

        Point window = new Point();
        try{
            window = this.getLocationOnScreen();
        }catch(IllegalComponentStateException ignored){}



        Point rel = new Point(mouse.x-window.x, mouse.y-window.y);
        double centerX = this.player.getLocx();
        double centerY = this.player.getLocy();

        //is cursor in circle
        float dist = distance(centerX, centerY, rel.getX(),rel.getY());
        if(dist < this.player.getSize()/2f){
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
        float dx = this.player.getX();
        float dy = this.player.getY();

        dx += (distancex(this.player.getLocx(), this.player.getLocy(), rel.x, rel.y)/100);
        dy += (distancey(this.player.getLocx(), this.player.getLocy(), rel.x, rel.y)/100);

        dx/=1.1;
        dy/=1.1;

        this.player.setX(dx);
        this.player.setY(dy);
    }

    private boolean collideWithBoxes(RectObject r) {
        if(withinX(r, 0, this.player.getLocx())){
            if(withinY(r.getXyLower(), 1, this.player.getLocy())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    this.player.setY(this.player.getY()*-1* r.getBouncyness());
                    this.player.setLocy((int) (r.getXyLower()[1]-this.player.getSize()/2f));
                }

            }
            if(withinY(r.getXyUpper(), 1, this.player.getLocy())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    this.player.setY(this.player.getY()*-1* r.getBouncyness());
                    this.player.setLocy((int) (r.getXyUpper()[1]+this.player.getSize()/2f));
                }

            }
        }


        if(withinX(r, 1, this.player.getLocy())){
            if(withinY(r.getXyLower(), 0, this.player.getLocx())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    this.player.setX(this.player.getX()*-1* r.getBouncyness());
                    this.player.setLocx((int) (r.getXyLower()[0]-this.player.getSize()/2f-this.compression));
                }


            }
            if(withinY(r.getXyUpper(), 0, this.player.getLocx())){

                if (boxFunctions(r)) return true;

                if(r.isCollidable()){
                    this.player.setX(this.player.getX()*-1* r.getBouncyness());
                    this.player.setLocx((int) (r.getXyUpper()[0]+this.player.getSize()/2f + this.compression));
                }


            }
        }
        return false;
    }

    private boolean boxFunctions(RectObject r) {
        if (r.isWinner()) {
            this.isPaused = true;
            this.player.setX(0);
            this.player.setY(0);
        }
        if (r.getSizeSet() != -1) {
            this.player.setSize((int) (r.getSizeSet() * this.compression));
        }
        if (r.isKills()) {
            killPlayer();
            return true;
        }
        r.runFunc();
        return false;
    }

    private boolean withinY(int[] xyLower, int i, double locx) {
        return Math.abs(xyLower[i] - locx) < this.player.getSize() / 2f;
    }

    private boolean withinX(RectObject r, int i, double locx) {
        return withinY(r.getXyLower(), i, locx) || locx > r.getXyLower()[i] && locx < r.getXyUpper()[i];
    }

    private void collideWithWall() {
        float halfSize = player.getSize()/2f;
        if(this.player.getLocx()+halfSize>=this.dimx || this.player.getLocx() - halfSize < 0){ //side wall
            this.player.setX(this.player.getX()*-1);
            this.player.setLocx(this.player.getLocx()<this.dimx/2f? (int) (this.player.getSize() / 2f) : (int) (this.dimx - this.player.getSize() / 2f));
        }

        if(this.player.getLocy()+halfSize>=this.dimy || this.player.getLocy() - halfSize < 0){ //top/bottom wall
            this.player.setY(this.player.getY()*-1);
            this.player.setLocy((int) (this.player.getLocy()<this.dimy/2f?this.player.getSize()/2f+(this.compression):this.dimy-this.player.getSize()/2f-(this.compression)));
        }
    }


    private void killPlayer(){
        this.player.setLocx(this.l.getRespawn().x);
        this.player.setLocy(this.l.getRespawn().y);
        this.player.setX(0);
        this.player.setY(0);
        this.isPaused = true;
        this.isPlaying = false;
        this.levelTime = 0;

        this.player.setSize((int) (this.player.getDefaultSize() * this.compression));
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
