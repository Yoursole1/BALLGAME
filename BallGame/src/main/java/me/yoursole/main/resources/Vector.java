package me.yoursole.main.resources;

public class Vector {
    private float x;
    private float y;

    public Vector(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }

    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
}
