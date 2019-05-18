package com.csc400.eric.morpi.Hardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.csc400.eric.morpi.Breadboard.BreadboardUtil;
import com.csc400.eric.morpi.MainActivity;
import com.csc400.eric.morpi.ProjectConfig;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class HardwareComponent extends AppCompatButton
{
    private WeakReference<MainActivity> mainActivityWeakReference;

    int widthInLayout;
    int heightInLayout;

    int widthInLayoutRotated;
    int heightInLayoutRotated;

    Drawable customBackground;

    HardwareComponentProperties componentProperties;

    String[] configOptions;

    abstract void executeComponentSpecificModification(String configOption);
    public abstract void modifyComponentFunctionality(String value);
    public abstract void modifySourceCodeSnippet(String projectSourceCode);
    public abstract void addComponentCodeToProjectSourceCode();

    public abstract void create(MainActivity activity);
    public abstract void restore(MainActivity activity, HardwareComponentProperties properties);
    abstract void initCommonDefaults();

    public HardwareComponent(Context context)
    {
        super(context);
    }

    void initComponentProperties(float topLeftX, float topLeftY, CharSequence displayText, String componentDescription)
    {
        componentProperties = new HardwareComponentProperties(topLeftX, topLeftY, widthInLayout, heightInLayout, displayText, componentDescription);
        componentProperties.setDisplayTextVertical(getDisplayTextVertical(displayText));
    }

    void addPropertiesToComponentPropertiesList()
    {
        ProjectConfig.componentDescriptionToPropertiesMap.get(componentProperties.getComponentDescription()).add(componentProperties);
    }

    void createComponent()
    {
        setPadding(0, 0, 0, 0);
        setTranslationX(getComponentProperties().getTopLeftX());
        setTranslationY(getComponentProperties().getTopLeftY());
        setBackground(customBackground);
        setTextSize(12f);
        setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        if(componentProperties.isRotated())
        {
            setText(getComponentProperties().getDisplayTextVertical());
        }
        else
        {
            setText(getComponentProperties().getDisplayText());
        }
    }

    public void executeComponentConfigModification(String configOption)
    {
        String changeLocation = "Change Location";
        String changeOrientation = "Change Orientation";
        String remove = "Remove";

        MainActivity activity = getMainActivityWeakRef().get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }

        if(configOption.equals(changeLocation))
        {
            changeOnBoardComponentLocation(activity, componentProperties);
        }
        else
        if(configOption.equals(changeOrientation))
        {
            changeOrientationOfComponent(activity);
        }
        else
        if(configOption.equals(remove))
        {
            removeComponentFromProject();
        }
        else
        {
            executeComponentSpecificModification(configOption);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public void changeOnBoardComponentLocation(final MainActivity activity, final HardwareComponentProperties properties)
    {
        activity.getParentHardwareLayout().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int[] coordinates;

                int xCoord = (int)properties.getTopLeftX();
                int yCoord = (int)properties.getTopLeftY();

                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    coordinates = BreadboardUtil.computeComponentCoordinatesOfChangedLocation(properties.isRotated(),
                            (int)event.getX(), (int)event.getY(), xCoord, yCoord);

                    properties.setTopLeftX(coordinates[0]);
                    properties.setTopLeftY(coordinates[1]);

                    xCoord = coordinates[0];
                    yCoord = coordinates[1];

                    // Set new position of resistor.
                    setTranslationX(xCoord);
                    setTranslationY(yCoord);

                    killParentHardwareLayoutListener(activity);
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void killParentHardwareLayoutListener(final MainActivity activity)
    {
        activity.getParentHardwareLayout().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                System.out.println("Done Listening.");
                return false;
            }
        });
    }

    public void changeOrientationOfComponent(MainActivity activity)
    {
        int x = (int)componentProperties.getTopLeftX();
        int y = (int)componentProperties.getTopLeftY();

        if(componentProperties.isRotated())
        {
            if ((x >= BreadboardUtil.leftPinholesStartX && x < BreadboardUtil.leftPinholesXCutoff) || (x >= BreadboardUtil.rightPinholesStartX && x < BreadboardUtil.rightPinholesXCutoff))
            {
                getComponentProperties().setIsRotated(false);
                rotateComponent(activity, widthInLayout, heightInLayout, getComponentProperties().getDisplayText().toString(), getComponentProperties().isRotated());
            }
        }
        else
        {
            if(y < BreadboardUtil.yCutoffHardwareRotated)
            {
                getComponentProperties().setIsRotated(true);
                rotateComponent(activity, widthInLayoutRotated, heightInLayoutRotated, getComponentProperties().getDisplayTextVertical().toString(), getComponentProperties().isRotated());
            }
        }
    }

    public void rotateComponent(MainActivity activity, int width, int height, String displayText, boolean isRotated)
    {
        // Can't change width and height of
        // HardwareComponent so remove it.
        remove(this);

        componentProperties.setWidth(width);
        componentProperties.setHeight(height);
        componentProperties.setIsRotated(isRotated);

        setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setText(displayText);


        activity.getParentHardwareLayout().addView(this, width, height);
        activity.setComponentListener(this);
    }

    public void removeComponentFromProject()
    {
        // Remove from properties list
        ProjectConfig.componentDescriptionToPropertiesMap.get(componentProperties.getComponentDescription()).remove(componentProperties);

        //propertyList.remove(componentIndex);
        remove(this);
    }

    public void remove(HardwareComponent component)
    {
        ((ViewGroup)component.getParent()).removeView(component);
    }

    public void addComponentAndSetListener(MainActivity activity)
    {
        setMainActivityWeakRef(activity);
        getMainActivityWeakRef().get().getParentHardwareLayout().addView(this, componentProperties.getWidth(), componentProperties.getHeight());
        getMainActivityWeakRef().get().setComponentListener(this);
    }

    String getDisplayTextVertical(CharSequence text)
    {
        char[] chars;

        StringBuilder verticalText = new StringBuilder();

        String[] split = text.toString().split(" ");

        for(String string : split)
        {
            chars = string.toCharArray();

            for(char chr : chars)
            {
                verticalText.append(chr);
                verticalText.append(System.getProperty("line.separator"));
            }

            verticalText.append(System.getProperty("line.separator"));
        }

        return verticalText.toString().trim();
    }

    WeakReference<MainActivity> getMainActivityWeakRef()
    {
        return mainActivityWeakReference;
    }

    public String[] getConfigOptions()
    {
        return configOptions;
    }

    public HardwareComponentProperties getComponentProperties()
    {
        return componentProperties;
    }

    void setHeightAndWidth(int widthDp, int heightDp)
    {
        widthInLayout = BreadboardUtil.convertDensityPixelsToScreenPixels(widthDp);
        heightInLayout = BreadboardUtil.convertDensityPixelsToScreenPixels(heightDp);
    }

    void setHeightAndWidthRotated(int widthRotDp, int heightRotDp)
    {
        widthInLayoutRotated = BreadboardUtil.convertDensityPixelsToScreenPixels(widthRotDp);
        heightInLayoutRotated = BreadboardUtil.convertDensityPixelsToScreenPixels(heightRotDp);
    }

    public void setMainActivityWeakRef(MainActivity activity)
    {
        mainActivityWeakReference = new WeakReference<>(activity);
    }

    void setConfigOptions(String[] options)
    {
        configOptions = options;
    }

    public void setComponentProperties(HardwareComponentProperties properties)
    {
        componentProperties = properties;
    }

    public void setCustomBackground(Drawable background)
    {
        customBackground = background;
    }

}
