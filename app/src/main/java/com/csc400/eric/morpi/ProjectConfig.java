package com.csc400.eric.morpi;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.constraint.ConstraintLayout;
import android.widget.Button;


import com.csc400.eric.morpi.Hardware.HardwareComponentProperties;
import com.csc400.eric.morpi.Wiring.WireEndCoordinates;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProjectConfig
{
    private static WeakReference<MainActivity> mainActivityWeakReference;

    public static String[] projectTemplates;
    public static String[] userProjects;

    public static final String whiteWireEnd1 = "whiteWireEnd1";
    public static final String whiteWireEnd2 = "whiteWireEnd2";
    public static final String blackWireEnd1 = "blackWireEnd1";
    public static final String blackWireEnd2 = "blackWireEnd2";
    public static final String redWireEnd1 = "redWireEnd1";
    public static final String redWireEnd2 = "redWireEnd2";
    public static final String blueWireEnd1 = "blueWireEnd1";
    public static final String blueWireEnd2 = "blueWireEnd2";
    public static final String yellowWireEnd1 = "yellowWireEnd1";
    public static final String yellowWireEnd2 = "yellowWireEnd2";

    public static final String componentDescriptionResistor220 = "Resistor220";
    public static final String componentDescriptionRedLED = "RedLED";

    public static final String pathToProjectTemplates = "/home/pi/MoRPi/ProjectTemplates/";
    public static final String pathToUserProjects = "/home/pi/MoRPi/UserProjects/";

    public static final String pathToPinIDsList = "/PinConfig/gpioPinIDs.bin";
    public static final String pathToPinholeToParentIDsMap = "/PinConfig/pinholeIdsToParentIDs.bin";
    public static final String pathToPinholeIDsToWireDescriptionMap = "/PinConfig/idsToWireDescription.bin";

    public static final String pathToWhiteWireEnd1IDsList = "/WiringConfig/IDs/whiteWireEnd1IDs.bin";
    public static final String pathToWhiteWireEnd2IDsList = "/WiringConfig/IDs/whiteWireEnd2IDs.bin";
    public static final String pathToBlackWireEnd1IDsList = "/WiringConfig/IDs/blackWireEnd1IDs.bin";
    public static final String pathToBlackWireEnd2IDsList = "/WiringConfig/IDs/blackWireEnd2IDs.bin";
    public static final String pathToRedWireEnd1IDsList = "/WiringConfig/IDs/redWireEnd1IDs.bin";
    public static final String pathToRedWireEnd2IDsList = "/WiringConfig/IDs/redWireEnd2IDs.bin";
    public static final String pathToBlueWireEnd1IDsList = "/WiringConfig/IDs/blueWireEnd1IDs.bin";
    public static final String pathToBlueWireEnd2IDsList = "/WiringConfig/IDs/blueWireEnd2IDs.bin";
    public static final String pathToYellowWireEnd1IDsList = "/WiringConfig/IDs/yellowWireEnd1IDs.bin";
    public static final String pathToYellowWireEnd2IDsList = "/WiringConfig/IDs/yellowWireEnd2IDs.bin";

    public static final String pathToWhiteWireEnd1CoordinatesList = "/WiringConfig/Coordinates/whiteWireEnd1Coordinates.bin";
    public static final String pathToWhiteWireEnd2CoordinatesList = "/WiringConfig/Coordinates/whiteWireEnd2Coordinates.bin";
    public static final String pathToBlackWireEnd1CoordinatesList = "/WiringConfig/Coordinates/blackWireEnd1Coordinates.bin";
    public static final String pathToBlackWireEnd2CoordinatesList = "/WiringConfig/Coordinates/blackWireEnd2Coordinates.bin";
    public static final String pathToRedWireEnd1CoordinatesList = "/WiringConfig/Coordinates/redWireEnd1Coordinates.bin";
    public static final String pathToRedWireEnd2CoordinatesList = "/WiringConfig/Coordinates/redWireEnd2Coordinates.bin";
    public static final String pathToBlueWireEnd1CoordinatesList = "/WiringConfig/Coordinates/blueWireEnd1Coordinates.bin";
    public static final String pathToBlueWireEnd2CoordinatesList = "/WiringConfig/Coordinates/blueWireEnd2Coordinates.bin";
    public static final String pathToYellowWireEnd1CoordinatesList = "/WiringConfig/Coordinates/yellowWireEnd1Coordinates.bin";
    public static final String pathToYellowWireEnd2CoordinatesList = "/WiringConfig/Coordinates/yellowWireEnd2Coordinates.bin";

    public static final String pathToDynamicCodeList = "/python/dynamicCode.bin";

    public static final String pathTo220ohmResistorProperties = "/HardwareConfig/Resistors/220ohmProperties/220ohmProperties.bin";
    public static final String pathToRedLedProperties = "/HardwareConfig/LEDs/RedLEDs/redLEDsProperties.bin";

    /** Pin IDs */
    public static List<Integer> pins = new ArrayList<>();

    /** Pinhole ID Mappings */
    public static Map<Integer, Integer> pinholeIdToParentId = new HashMap<>();
    public static Map<Integer, String> pinholeIdToWireDescription = new HashMap<>();
    public static Map<Integer, LayerDrawable> pinholeIdToPinholeLayerDrawable = new HashMap<>();

    /** Wire IDs */
    public static Map<String, List<Integer>> wireDescriptionToIdMap = new HashMap<>();
    private static List<Integer> whiteWireEnd1IdsList = new ArrayList<>();
    private static List<Integer> whiteWireEnd2IdsList = new ArrayList<>();
    private static List<Integer> blackWireEnd1IdsList = new ArrayList<>();
    private static List<Integer> blackWireEnd2IdsList = new ArrayList<>();
    private static List<Integer> redWireEnd1IdsList = new ArrayList<>();
    private static List<Integer> redWireEnd2IdsList = new ArrayList<>();
    private static List<Integer> blueWireEnd1IdsList = new ArrayList<>();
    private static List<Integer> blueWireEnd2IdsList = new ArrayList<>();
    private static List<Integer> yellowWireEnd1IdsList = new ArrayList<>();
    private static List<Integer> yellowWireEnd2IdsList = new ArrayList<>();

    /** Wire coordinates for display */
    public static List[] wireEndPointsList = new List[10];

    /** Wire coordinates Config */
    public static Map<String, List<WireEndCoordinates>> wireDescriptionToCoordinatesMap = new HashMap<>();
    private static List<WireEndCoordinates> whiteWireEnd1CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> whiteWireEnd2CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> blackWireEnd1CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> blackWireEnd2CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> redWireEnd1CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> redWireEnd2CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> blueWireEnd1CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> blueWireEnd2CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> yellowWireEnd1CoordinatesList = new ArrayList<>();
    private static List<WireEndCoordinates> yellowWireEnd2CoordinatesList = new ArrayList<>();

    /** Hardware Components */
    public static Map<String, List<HardwareComponentProperties>> componentDescriptionToPropertiesMap = new HashMap<>();
    private static List<HardwareComponentProperties> resistor220PropertiesList = new ArrayList<>();
    private static List<HardwareComponentProperties> redLedPropertiesList = new ArrayList<>();

    ProjectConfig()
    {
        initializeMapWireDescriptionsToWireIdLists();
        initializeMapWireDescriptionsToWirePointLists();
        initializeWireEndpoints();
        initializeMapComponentDescriptionToProperties();
    }

    public static void initializeWireEndpoints()
    {
        wireEndPointsList[0] = wireDescriptionToCoordinatesMap.get(whiteWireEnd1);
        wireEndPointsList[1] = wireDescriptionToCoordinatesMap.get(whiteWireEnd2);
        wireEndPointsList[2] = wireDescriptionToCoordinatesMap.get(blackWireEnd1);
        wireEndPointsList[3] = wireDescriptionToCoordinatesMap.get(blackWireEnd2);
        wireEndPointsList[4] = wireDescriptionToCoordinatesMap.get(redWireEnd1);
        wireEndPointsList[5] = wireDescriptionToCoordinatesMap.get(redWireEnd2);
        wireEndPointsList[6] = wireDescriptionToCoordinatesMap.get(blueWireEnd1);
        wireEndPointsList[7] = wireDescriptionToCoordinatesMap.get(blueWireEnd2);
        wireEndPointsList[8] = wireDescriptionToCoordinatesMap.get(yellowWireEnd1);
        wireEndPointsList[9] = wireDescriptionToCoordinatesMap.get(yellowWireEnd2);
    }

    static void init()
    {
        initializeMapWireDescriptionsToWireIdLists();
        initializeMapWireDescriptionsToWirePointLists();
        initializeWireEndpoints();
        initializeMapComponentDescriptionToProperties();
    }

    static void clearProjectConfiguration(MainActivity activity)
    {
        mainActivityWeakReference = new WeakReference<>(activity);

        clearPinConfiguration();
        clearPinholeMappings();
        clearWiringConfiguration();
        clearHardwareConfiguration();
        mainActivityWeakReference.get().parentHardwareLayout.removeAllViews();
    }

    private static void clearPinConfiguration()
    {
        int outlineWidth = 2;
        int textColorGray = 0xFF808080;
        int pinholeDefault = 0xFFdfdfdf;
        int pinholeDefaultOutline = 0xFF808080;

        String off = "off";

        ConstraintLayout constraintLayout;
        LayerDrawable layerDrawable;
        GradientDrawable pinhole;
        Drawable pin_default = mainActivityWeakReference.get().getDrawable(R.drawable.pin_default);

        for (Map.Entry<Integer, Integer> entry : pinholeIdToParentId.entrySet())
        {
            constraintLayout = mainActivityWeakReference.get().findViewById(entry.getValue());
            layerDrawable = (LayerDrawable) constraintLayout.getBackground();
            pinhole = (GradientDrawable) layerDrawable.findDrawableByLayerId(entry.getKey());

            pinhole.setStroke(outlineWidth, pinholeDefaultOutline);
            pinhole.setColor(pinholeDefault);
        }

        for (int pinID : ProjectConfig.pins)
        {
            Button pin = mainActivityWeakReference.get().findViewById(pinID);
            pin.setBackground(pin_default);
            pin.setTextColor(textColorGray);
            pin.setContentDescription(off);
        }

        pins.clear();
    }

    private static void clearPinholeMappings()
    {
        pinholeIdToParentId.clear();
        pinholeIdToWireDescription.clear();
        pinholeIdToPinholeLayerDrawable.clear();
    }

    private static void clearWiringConfiguration()
    {
        for (Map.Entry<String, List<Integer>> entry : wireDescriptionToIdMap.entrySet())
        {
            entry.getValue().clear();
        }

        for (Map.Entry<String, List<WireEndCoordinates>> entry : wireDescriptionToCoordinatesMap.entrySet())
        {
            entry.getValue().clear();
        }
    }

    private static void clearHardwareConfiguration()
    {
        for (Map.Entry<String, List<HardwareComponentProperties>> entry : componentDescriptionToPropertiesMap.entrySet())
        {
            entry.getValue().clear();
        }
    }

    private static void initializeMapWireDescriptionsToWireIdLists()
    {
        wireDescriptionToIdMap.put(ProjectConfig.whiteWireEnd1, whiteWireEnd1IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.whiteWireEnd2, whiteWireEnd2IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.blackWireEnd1, blackWireEnd1IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.blackWireEnd2, blackWireEnd2IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.redWireEnd1, redWireEnd1IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.redWireEnd2, redWireEnd2IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.blueWireEnd1, blueWireEnd1IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.blueWireEnd2, blueWireEnd2IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.yellowWireEnd1, yellowWireEnd1IdsList);
        wireDescriptionToIdMap.put(ProjectConfig.yellowWireEnd2, yellowWireEnd2IdsList);
    }

    private static void initializeMapWireDescriptionsToWirePointLists()
    {
        wireDescriptionToCoordinatesMap.put(whiteWireEnd1, whiteWireEnd1CoordinatesList);
        wireDescriptionToCoordinatesMap.put(whiteWireEnd2, whiteWireEnd2CoordinatesList);
        wireDescriptionToCoordinatesMap.put(blackWireEnd1, blackWireEnd1CoordinatesList);
        wireDescriptionToCoordinatesMap.put(blackWireEnd2, blackWireEnd2CoordinatesList);
        wireDescriptionToCoordinatesMap.put(redWireEnd1, redWireEnd1CoordinatesList);
        wireDescriptionToCoordinatesMap.put(redWireEnd2, redWireEnd2CoordinatesList);
        wireDescriptionToCoordinatesMap.put(blueWireEnd1, blueWireEnd1CoordinatesList);
        wireDescriptionToCoordinatesMap.put(blueWireEnd2, blueWireEnd2CoordinatesList);
        wireDescriptionToCoordinatesMap.put(yellowWireEnd1, yellowWireEnd1CoordinatesList);
        wireDescriptionToCoordinatesMap.put(yellowWireEnd2, yellowWireEnd2CoordinatesList);
    }

    private static void initializeMapComponentDescriptionToProperties()
    {
        componentDescriptionToPropertiesMap.put(componentDescriptionResistor220, resistor220PropertiesList);
        componentDescriptionToPropertiesMap.put(componentDescriptionRedLED, redLedPropertiesList);
    }

}
