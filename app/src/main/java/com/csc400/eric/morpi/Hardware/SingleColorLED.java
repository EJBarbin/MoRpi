package com.csc400.eric.morpi.Hardware;

import android.content.Context;

import com.csc400.eric.morpi.Breadboard.BreadboardUtil;
import com.csc400.eric.morpi.MainActivity;
import com.csc400.eric.morpi.PiProjectCodeBuilder;
import com.csc400.eric.morpi.Pins.PinFragment;
import com.csc400.eric.morpi.R;

public class SingleColorLED extends HardwareComponent
{
    int ledFunction;

    private String componentDescription;

    public SingleColorLED(Context context)
    {
        super(context);
    }

    public SingleColorLED(Context context, String description)
    {
        super(context);
        componentDescription = description;
    }

    @Override
    public void create(MainActivity activity)
    {
        int defaultXinDp = 232;
        int defaultYinDp = 834;

        int topLeftX = BreadboardUtil.convertDensityPixelsToScreenPixels(defaultXinDp);
        int topLeftY = BreadboardUtil.convertDensityPixelsToScreenPixels(defaultYinDp);

        CharSequence displayText = "LED";

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
        int defaultWidthInDp = 48;
        int defaultHeightInDp = 20;

        int defaultWidthInDpRotated = 20;
        int defaultHeightInDpRotated = 60;

        setHeightAndWidth(defaultWidthInDp, defaultHeightInDp);
        setHeightAndWidthRotated(defaultWidthInDpRotated, defaultHeightInDpRotated);
        setConfigOptions(getResources().getStringArray(R.array.led_config_options));
    }

    @Override
    void executeComponentSpecificModification(String configOption)
    {
        MainActivity activity = getMainActivityWeakRef().get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }

        if(configOption.equals("Modify On Interval"))
        {
            ledFunction = 0;
            activity.setSourceCodeToEdit(PiProjectCodeBuilder.projectSourceCode.get(3).get(3));
            activity.showComponentFunctionalityModifierDialog("Enter On Interval in Seconds");
        }
        else
        if(configOption.equals("Modify Off Interval"))
        {
            ledFunction = 1;
            activity.setSourceCodeToEdit(PiProjectCodeBuilder.projectSourceCode.get(3).get(5));
            activity.showComponentFunctionalityModifierDialog("Enter Off Interval in Seconds");
        }
        else
        if(configOption.equals("Change Number of Blinks"))
        {
            ledFunction = 2;
            activity.setSourceCodeToEdit(PiProjectCodeBuilder.projectSourceCode.get(3).get(1));
            activity.showComponentFunctionalityModifierDialog("Enter Number of Blinks");
        }
    }

    public void modifyComponentFunctionality(String value)
    {
        if(ledFunction == 0)
        {
            PiProjectCodeBuilder.projectSourceCode.get(3).set(3, "        time.sleep(" + value + ")");
        }
        else
        if(ledFunction == 1)
        {
            PiProjectCodeBuilder.projectSourceCode.get(3).set(5, "        time.sleep(" + value + ")");
        }
        else
        if(ledFunction == 2)
        {
            PiProjectCodeBuilder.projectSourceCode.get(3).set(1, "    for i in range(0, " + value + "):");
        }
    }

    @Override
    public void modifySourceCodeSnippet(String projectSourceCode)
    {

        if(ledFunction == 0)
        {
            PiProjectCodeBuilder.projectSourceCode.get(3).set(3, projectSourceCode);
        }
        else
        if(ledFunction == 1)
        {
            PiProjectCodeBuilder.projectSourceCode.get(3).set(5, projectSourceCode);
        }
        else
        if(ledFunction == 2)
        {
            PiProjectCodeBuilder.projectSourceCode.get(3).set(1, projectSourceCode);
        }

    }

    @Override
    public void addComponentCodeToProjectSourceCode()
    {
        // Build project source code based on characteristics of hardware component.
        String import_time = "import time";

        System.out.println("Contains time import: " + PiProjectCodeBuilder.projectSourceCode.get(0).contains(import_time));
        if(! PiProjectCodeBuilder.projectSourceCode.get(0).contains(import_time))
        {
            PiProjectCodeBuilder.projectSourceCode.get(0).add(import_time);
        }

        PiProjectCodeBuilder.projectSourceCode.get(2).add("    GPIO.setup(" + PinFragment.pinToSet + ", GPIO.OUT)");
        PiProjectCodeBuilder.projectSourceCode.get(2).add("    GPIO.output(" +PinFragment. pinToSet + ", GPIO.HIGH)");

        PiProjectCodeBuilder.projectSourceCode.get(3).add("    for i in range(0, 10):");
        PiProjectCodeBuilder.projectSourceCode.get(3).add("        GPIO.output(" + PinFragment.pinToSet + ", GPIO.LOW)");
        PiProjectCodeBuilder.projectSourceCode.get(3).add("        time.sleep(0.5)");
        PiProjectCodeBuilder.projectSourceCode.get(3).add("        GPIO.output(" + PinFragment.pinToSet + ", GPIO.HIGH)");
        PiProjectCodeBuilder.projectSourceCode.get(3).add("        time.sleep(0.5)");

        PiProjectCodeBuilder.projectSourceCode.get(4).add("    GPIO.output(" + PinFragment.pinToSet + ", GPIO.HIGH)");
    }

}
