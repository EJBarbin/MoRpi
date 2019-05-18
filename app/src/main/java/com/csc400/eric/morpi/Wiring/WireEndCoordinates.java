package com.csc400.eric.morpi.Wiring;

import java.io.Serializable;

public class WireEndCoordinates implements Serializable
{
    private float xCoord;
    private float yCoord;

    public WireEndCoordinates()
    {
        xCoord = 0;
        yCoord = 0;
    }

    public WireEndCoordinates(float x, float y)
    {
        xCoord = x;
        yCoord = y;
    }

    public float getWireEndX()
    {
        return xCoord;
    }

    public float getWireEndY()
    {
        return yCoord;
    }

    public void setWireEndX(float x)
    {
        xCoord = x;
    }

    public void setWireEndY(float y)
    {
        yCoord = y;
    }

}
