package com.csc400.eric.morpi.SSHTasks;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.constraint.ConstraintLayout;
import android.widget.Button;

import com.csc400.eric.morpi.Hardware.HardwareComponentProperties;
import com.csc400.eric.morpi.Hardware.Resistor;
import com.csc400.eric.morpi.Hardware.SingleColorLED;
import com.csc400.eric.morpi.MainActivity;
import com.csc400.eric.morpi.PiProjectCodeBuilder;
import com.csc400.eric.morpi.Pins.PinFragment;
import com.csc400.eric.morpi.ProjectConfig;
import com.csc400.eric.morpi.R;
import com.csc400.eric.morpi.Wiring.WireEndCoordinates;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSHTaskProjectConfigFileRetrieval extends SSHTask
{
    private WeakReference<MainActivity> mainActivityWeakReference;




    public SSHTaskProjectConfigFileRetrieval(MainActivity activity)
    {
        mainActivityWeakReference = new WeakReference<>(activity);
    }


    @Override
    public void doInBackgroundImpl()
    {
        readInProjectConfigFiles();
    }

    @Override
    public void onPostExecuteImpl()
    {
        MainActivity activity = mainActivityWeakReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }

        // Open project pin Configuration
        openPinConfig(activity);

        // Open project wiring configuration
        openWiringConfig(activity);

        // Open project hardware configuration
        openResistor220Config(activity);
        openRedLedConfig(activity);
    }

    private void readInProjectConfigFiles()
    {
    // Pins

        // Open Pin IDs
        ProjectConfig.pins = (ArrayList<Integer>)getObjectFromObjectInputStream(ProjectConfig.pathToPinIDsList);
        // Open Pinhole Id to Parent ID map
        ProjectConfig.pinholeIdToParentId = (HashMap<Integer, Integer>)getObjectFromObjectInputStream(ProjectConfig.pathToPinholeToParentIDsMap);
        // Open Pinhole ID to Wire Description map
        ProjectConfig.pinholeIdToWireDescription = (HashMap<Integer, String>)getObjectFromObjectInputStream(ProjectConfig.pathToPinholeIDsToWireDescriptionMap);

    // Wiring

        // IDs

        // White IDs
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.whiteWireEnd1, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToWhiteWireEnd1IDsList));
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.whiteWireEnd2, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToWhiteWireEnd2IDsList));
        // Black IDs
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.blackWireEnd1, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToBlackWireEnd1IDsList));
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.blackWireEnd2, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToBlackWireEnd2IDsList));
        // Red IDs
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.redWireEnd1, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToRedWireEnd1IDsList));
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.redWireEnd2, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToRedWireEnd2IDsList));
        // Blue IDs
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.blueWireEnd1, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToBlueWireEnd1IDsList));
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.blueWireEnd2, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToBlueWireEnd2IDsList));
        // Yellow IDs
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.yellowWireEnd1, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToYellowWireEnd1IDsList));
        ProjectConfig.wireDescriptionToIdMap.put(ProjectConfig.yellowWireEnd2, (ArrayList<Integer>) getObjectFromObjectInputStream(ProjectConfig.pathToYellowWireEnd2IDsList));

        // Coordinates

        // White Coordinates
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.whiteWireEnd1, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToWhiteWireEnd1CoordinatesList));
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.whiteWireEnd2, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToWhiteWireEnd2CoordinatesList));
        // Black Coordinates
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.blackWireEnd1, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToBlackWireEnd1CoordinatesList));
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.blackWireEnd2, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToBlackWireEnd2CoordinatesList));
        // Red Coordinates
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.redWireEnd1, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToRedWireEnd1CoordinatesList));
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.redWireEnd2, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToRedWireEnd2CoordinatesList));
        // Blue Coordinates
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.blueWireEnd1, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToBlueWireEnd1CoordinatesList));
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.blueWireEnd2, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToBlueWireEnd2CoordinatesList));
        // Yellow Coordinates
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.yellowWireEnd1, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToYellowWireEnd1CoordinatesList));
        ProjectConfig.wireDescriptionToCoordinatesMap.put(ProjectConfig.yellowWireEnd2, (ArrayList<WireEndCoordinates>) getObjectFromObjectInputStream(ProjectConfig.pathToYellowWireEnd2CoordinatesList));

    // Project python code
        PiProjectCodeBuilder.projectSourceCode = (List<List<String>>) getObjectFromObjectInputStream(ProjectConfig.pathToDynamicCodeList);

    // Hardware Components

        // 220 ohm Resistors
        ProjectConfig.componentDescriptionToPropertiesMap.put(ProjectConfig.componentDescriptionResistor220, (ArrayList<HardwareComponentProperties>) getObjectFromObjectInputStream(ProjectConfig.pathTo220ohmResistorProperties));

        // Red LEDs
        ProjectConfig.componentDescriptionToPropertiesMap.put(ProjectConfig.componentDescriptionRedLED, (ArrayList<HardwareComponentProperties>) getObjectFromObjectInputStream(ProjectConfig.pathToRedLedProperties));
    }



    private Object getObjectFromObjectInputStream(String configFilePath)
    {
        String absolutePath = getProjectPath() + configFilePath;

        Object obj = null;

        try
        {
            ObjectInputStream oisGpioPinIDs = new ObjectInputStream(channelSftp.get(absolutePath));
            obj = oisGpioPinIDs.readObject();
            oisGpioPinIDs.close();

        }
        catch(IOException | ClassNotFoundException | SftpException ioe)
        {
            ioe.printStackTrace();
        }

        return obj;
    }


    private void openPinConfig(MainActivity activity)
    {
        Button pin;

        for (int pinID : ProjectConfig.pins)
        {
            pin = activity.findViewById(pinID);

            pin.setBackground(PinFragment.pin_default_pressed);
            pin.setTextColor(0xFF000000);
            pin.setContentDescription("on");
        }
    }


    private void openWiringConfig(MainActivity activity)
    {
        ConstraintLayout parentPinholeLayout;
        LayerDrawable layerDrawable;
        GradientDrawable pinhole;

        int outlineWidth = 2;
        int blackOutline = 0xFF000000;
        int pinholeDefault = 0xFFdfdfdf;

        // Open Wiring Configuration.
        for (Map.Entry<Integer, Integer> entry : ProjectConfig.pinholeIdToParentId.entrySet())
        {
            parentPinholeLayout = activity.findViewById(entry.getValue());
            layerDrawable = (LayerDrawable) parentPinholeLayout.getBackground();
            pinhole = (GradientDrawable) layerDrawable.findDrawableByLayerId(entry.getKey());

            ProjectConfig.pinholeIdToPinholeLayerDrawable.put(entry.getKey(), layerDrawable);

            pinhole.setStroke(outlineWidth, blackOutline);
            pinhole.setColor(pinholeDefault);
        }
        activity.mWiringConfigurationDisplay.draw();
    }

    private void openResistor220Config(MainActivity activity)
    {
        List<HardwareComponentProperties> res220props = ProjectConfig.componentDescriptionToPropertiesMap.get(ProjectConfig.componentDescriptionResistor220);

        // Open Hardware Configuration - Resistors.
        for (HardwareComponentProperties propertiesRes220 : res220props)
        {
            Resistor resistor = new Resistor(activity);
            resistor.setCustomBackground(activity.getDrawable(R.drawable.resistor_background));
            resistor.restore(activity, propertiesRes220);
        }
    }

    private void openRedLedConfig(MainActivity activity)
    {
        // Open Hardware Configuration - Red LED.
        List<HardwareComponentProperties> redLedProps = ProjectConfig.componentDescriptionToPropertiesMap.get(ProjectConfig.componentDescriptionRedLED);

        for (HardwareComponentProperties propertiesRedLed : redLedProps)
        {
            SingleColorLED redLed = new SingleColorLED(activity);
            redLed.setCustomBackground(activity.getDrawable(R.drawable.red_led_background));
            redLed.restore(activity, propertiesRedLed);
        }
    }

}
