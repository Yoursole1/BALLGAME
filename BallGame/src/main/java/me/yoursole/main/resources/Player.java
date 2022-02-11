package me.yoursole.main.resources;

public class Player extends Vector{
    private int size;
    private double locx;
    private double locy;
    private int defaultSize;
    public Player(float x, float y, int size, int locx, int locy, int defaultSize) {
        super(x, y);
        this.size = size;
        this.defaultSize = defaultSize;

        this.locx = locx;
        this.locy = locy;
    }

    public int getSize(){
        return this.size;
    }

    public double getLocx(){
        return this.locx;
    }
    public double getLocy(){
        return this.locy;
    }

    public void setLocx(int locx){
        this.locx = locx;
    }
    public void setLocy(int locy){
        this.locy = locy;
    }
    public void setSize(int size){
        this.size = size;
    }
    public int getDefaultSize(){
        return this.defaultSize;
    }
}
