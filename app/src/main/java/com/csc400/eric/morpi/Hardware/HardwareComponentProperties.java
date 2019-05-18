package com.csc400.eric.morpi.Hardware;

import java.io.Serializable;


public class HardwareComponentProperties implements Serializable
{
    private float topLeftX;
    private float topLeftY;
    private int width;
    private int height;

    private boolean isRotated = false;

    private CharSequence displayText;
    private CharSequence displayTextVertical;

    private String componentDescription;

    public HardwareComponentProperties(float x, float y, int w, int h, CharSequence text, String description)
    {
        topLeftX = x;
        topLeftY = y;
        width = w;
        height = h;
        displayText = text;
        componentDescription = description;
    }

    public float getTopLeftX()
    {
        return topLeftX;
    }

    public float getTopLeftY()
    {
        return topLeftY;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean isRotated()
    {
        return isRotated;
    }

    public CharSequence getDisplayText()
    {
        return displayText;
    }

    public CharSequence getDisplayTextVertical()
    {
        return displayTextVertical;
    }

    public String getComponentDescription()
    {
        return componentDescription;
    }


    public void setTopLeftX(float x)
    {
        topLeftX = x;
    }

    public void setTopLeftY(float y)
    {
        topLeftY = y;
    }

    public void setWidth(int w)
    {
        width = w;
    }

    public void setHeight(int h)
    {
        height = h;
    }

    public void setIsRotated(Boolean rotated)
    {
        isRotated = rotated;
    }

    public void setDisplayText(CharSequence display)
    {
        displayText = display;
    }

    public void setDisplayTextVertical(CharSequence display)
    {
        displayTextVertical = display;
    }

    public void setComponentDescription(String description)
    {
        componentDescription = description;
    }
}
