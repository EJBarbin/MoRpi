package com.csc400.eric.morpi.Hardware;

import android.content.Context;

import com.csc400.eric.morpi.Breadboard.BreadboardUtil;
import com.csc400.eric.morpi.MainActivity;
import com.csc400.eric.morpi.R;

public class Resistor extends HardwareComponent
{
    private CharSequence displayText;
    private String componentDescription;


    public Resistor(Context context)
    {
        super(context);
    }

    public Resistor(Context context, CharSequence ohms, String description)
    {
        super(context);

        displayText = ohms;
        componentDescription = description;
    }

    @Override
    public void create(MainActivity activity)
    {
        int defaultXinDp = 218;
        int defaultYinDp = 834;

        int topLeftX = BreadboardUtil.convertDensityPixelsToScreenPixels(defaultXinDp);
        int topLeftY = BreadboardUtil.convertDensityPixelsToScreenPixels(defaultYinDp);

        initCommonDefaults();
        initComponentProperties(topLeftX, topLeftY, displayText, componentDescription);
        addPropertiesToComponentPropertiesList();
        createComponent();
        addComponentAndSetListener(activity);

    }

    @Override
    public void restore(MainActivity activity, HardwareComponentProperties properties)
    {
        initCommonDefaults();
        setComponentProperties(properties);
        createComponent();
        addComponentAndSetListener(activity);
    }

    @Override
    void initCommonDefaults()
    {
        int defaultWidthInDp = 76;
        int defaultHeightInDp = 20;

        int defaultWidthInDpRotated = 20;
        int defaultHeightInDpRotated = 100;

        setHeightAndWidth(defaultWidthInDp, defaultHeightInDp);
        setHeightAndWidthRotated(defaultWidthInDpRotated, defaultHeightInDpRotated);

        setConfigOptions(getResources().getStringArray(R.array.resistor_config_options));
    }

    @Override
    void executeComponentSpecificModification(String configOption)
    {
        // Resistor has no unique modifications.
    }

    @Override
    public void modifyComponentFunctionality(String value)
    {
        // Resistor functionality cannot be changed.
    }

    @Override
    public void modifySourceCodeSnippet(String projectSourceCode)
    {
        // Resistor has no corresponding source code.
    }

    @Override
    public void addComponentCodeToProjectSourceCode()
    {
        // Resistor has no corresponding source code.
    }

}
