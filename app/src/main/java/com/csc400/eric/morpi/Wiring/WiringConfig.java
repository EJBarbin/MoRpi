package com.csc400.eric.morpi.Wiring;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Toast;

import com.csc400.eric.morpi.Breadboard.BreadboardUtil;
import com.csc400.eric.morpi.MainActivity;
import com.csc400.eric.morpi.ProjectConfig;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class WiringConfig
{
    private WeakReference<MainActivity> mainActivityWeakReference;

    private final int negativeOne = -1;

    // Needed for Wiring Config
    private boolean changeWireColorFlag = false;

    private GradientDrawable pinhole;
    private int pinholeId;
    private int pinholeParentId;
    private int secondPinholeId;
    private int indexContainingNegativeOne;
    private WireEndCoordinates centerOfPinhole;
    private WireEndCoordinates centerOfSecondPinhole;
    private View currentView;
    private String singleWireEndDescription = "";

    private static final int blackOutline = 0xFF000000;
    private static final int outlineWidth = 2;
    private final String emptyString = "";
    private static int pinholeDefault = 0xFFdfdfdf;
    private final int pinholeDefaultOutline = 0xFF808080;


    public void respondToPinholeTouchEvents(ConstraintLayout parentConstraintLayout, LayerDrawable pinholeLayerDrawable,
                                            int[][] pinholeIndices, int parentId, int xTouch, int yTouch, int xTrans, int yTrans, MainActivity activity)
    {
        setPinholeParentId(parentId);
        setCurrentView(parentConstraintLayout);

        int pinholeIndex = BreadboardUtil.computePinholeIndex(xTouch, yTouch, pinholeIndices);

        int[] centerCoordinates = BreadboardUtil.computePinholeCenterCoordinates(xTouch, yTouch, xTrans, yTrans);

        setPinholeCenterPoint(centerCoordinates[0], centerCoordinates[1]);

        if(pinholeIndex != negativeOne)
        {
            int selectedPinholeId = pinholeLayerDrawable.getId(pinholeIndex);
            setSelectedPinholeId(selectedPinholeId);

            GradientDrawable selectedPinhole = (GradientDrawable) pinholeLayerDrawable.findDrawableByLayerId(selectedPinholeId);
            setPinholeDrawable(selectedPinhole);

            // If there is a wire plugged into the selected pinhole...
            if (ProjectConfig.pinholeIdToWireDescription.containsKey(selectedPinholeId))
            {
                // Wire End Description of selected pinhole.
                String descriptionOfWireEnd = ProjectConfig.pinholeIdToWireDescription.get(selectedPinholeId);
                // Generated Wire End Description of corresponding pinhole.
                String descriptionOfOtherEnd = getDescriptionOfCorrespondingPinholeListFromPinholeId(selectedPinholeId);

                // ID list which holds the ID of the selected pinhole
                List<Integer> wireEnd = ProjectConfig.wireDescriptionToIdMap.get(descriptionOfWireEnd);
                // ID list which may hold the ID of the corresponding pinhole
                List<Integer> otherEnd = ProjectConfig.wireDescriptionToIdMap.get(descriptionOfOtherEnd);

                int indexOfPinhole = wireEnd.indexOf(selectedPinholeId);

                // If only one end of the wire is plugged in...
                // If the list holding the ID of the selected pinhole is larger than the other list,
                // or if the other list contains a -1 at the index of the selected pinhole,
                // only one end of the wire is plugged in.
                if (wireEnd.size() > otherEnd.size() | (wireEnd.size() == otherEnd.size() && otherEnd.get(indexOfPinhole) == negativeOne))
                {
                    mainActivityWeakReference = new WeakReference<>(activity);
                    mainActivityWeakReference.get().showWireEndConfigOptionsDialog(parentConstraintLayout);
                }
                else
                    // If both ends of the wire are plugged in...
                    // If the lists are the same size and the other list does not have a -1
                    // at the index of the selected pinhole, both wire ends are plugged in.
                    if (wireEnd.size() == otherEnd.size() && otherEnd.get(indexOfPinhole) != negativeOne)
                    {
                        mainActivityWeakReference = new WeakReference<>(activity);
                        mainActivityWeakReference.get().showFullWireConfigOptionsDialog(parentConstraintLayout);
                    }
            }
            else
                // Only one end of the wire is plugged in, ALlow USer to touch another pinhole and draw the wire..
                if (!ProjectConfig.pinholeIdToWireDescription.containsKey(selectedPinholeId) && !getSingleWireEndDescription().isEmpty())
                {
                    String descriptionOfOtherEnd = getDescriptionOfCorrespondingPinholeListFromDescription(getSingleWireEndDescription());

                    // ID list which holds the ID of the selected pinhole
                    List<Integer> wireEnd = ProjectConfig.wireDescriptionToIdMap.get(getSingleWireEndDescription());

                    List<Integer> otherEnd = ProjectConfig.wireDescriptionToIdMap.get(descriptionOfOtherEnd);
                    List<WireEndCoordinates> otherEndCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(descriptionOfOtherEnd);

                    if (wireEnd.size() == otherEnd.size())
                    {
                        otherEnd.set(getIndexContainingNegativeOne(), getSelectedPinholeId());
                        otherEndCoordinates.set(getIndexContainingNegativeOne(), getPinholeCenterPoint());
                    }
                    else
                    {
                        otherEnd.add(getSelectedPinholeId());
                        otherEndCoordinates.add(getPinholeCenterPoint());
                    }

                    ProjectConfig.pinholeIdToParentId.put(getSelectedPinholeId(), getPinholeParentId());
                    ProjectConfig.pinholeIdToWireDescription.put(getSelectedPinholeId(), descriptionOfOtherEnd);
                    ProjectConfig.pinholeIdToPinholeLayerDrawable.put(getSelectedPinholeId(), pinholeLayerDrawable);

                    selectedPinhole.setStroke(outlineWidth, blackOutline);

                    setSingleWireEndDescription(emptyString);

                    mainActivityWeakReference = new WeakReference<>(activity);
                    mainActivityWeakReference.get().mWiringConfigurationDisplay.draw();
                }
                // There is no wire plugged into the selected pinhole.
                else
                {
                    ProjectConfig.pinholeIdToPinholeLayerDrawable.put(getSelectedPinholeId(), pinholeLayerDrawable);

                    mainActivityWeakReference = new WeakReference<>(activity);
                    mainActivityWeakReference.get().showWireColorSelectionDialog(parentConstraintLayout);
                }
        }

    }

    public void createNewWire(String color)
    {
        //setUserSelectedOption(optionSelected);
        String wireEnd1 = "WireEnd1";
        String wireEnd2 = "WireEnd2";

        int indexOfNegativeOne;
        int selectedPinholeId = getSelectedPinholeId();
        int selectedPinholeParentId = getPinholeParentId();

        GradientDrawable selectedPinholeDrawable = getPinholeDrawable();

        WireEndCoordinates pinholeCenterPoint = getPinholeCenterPoint();

        color = color.toLowerCase();

        // Set wire end descriptions
        String wireDescriptionPinholeEnd1 = color + wireEnd1;
        String wireDescriptionPinholeEnd2 = color + wireEnd2;

        // Lists that hold selected pinhole Ids
        List<Integer> wireEnd1PinholeIds;
        List<Integer> wireEnd2PinholeIds;

        // Lists that hold the selected pinhole center coordinates
        List<WireEndCoordinates> wireEnd1PinholeCenterCoordinates;
        List<WireEndCoordinates> wireEnd2PinholeCenterCoordinates;

        // Set black outline around selected pinhole
        selectedPinholeDrawable.setStroke(outlineWidth, blackOutline);

        // Map the selected pinhole Id to the parent container Id
        ProjectConfig.pinholeIdToParentId.put(selectedPinholeId, selectedPinholeParentId);

        // Lists that hold the pinhole Ids of the selected wire color
        wireEnd1PinholeIds = ProjectConfig.wireDescriptionToIdMap.get(wireDescriptionPinholeEnd1);
        wireEnd2PinholeIds = ProjectConfig.wireDescriptionToIdMap.get(wireDescriptionPinholeEnd2);

        // Lists that hold the pinhole coordinates of the selected wire color
        wireEnd1PinholeCenterCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(wireDescriptionPinholeEnd1);
        wireEnd2PinholeCenterCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(wireDescriptionPinholeEnd2);

        if (changeWireColorFlag)
        {
            wireEnd1PinholeIds.add(getSelectedPinholeId());
            wireEnd1PinholeCenterCoordinates.add(getPinholeCenterPoint());

            wireEnd2PinholeIds.add(getCorrespondingPinholeId());
            wireEnd2PinholeCenterCoordinates.add(getCorrespondingPinholeCenterPoint());

            ProjectConfig.pinholeIdToWireDescription.put(getSelectedPinholeId(), wireDescriptionPinholeEnd1);
            ProjectConfig.pinholeIdToWireDescription.put(getCorrespondingPinholeId(), wireDescriptionPinholeEnd2);

            changeWireColorFlag = false;
        }
        else
        {
            // If the lists holding pinhole IDs of the selected color are the same size...
            //
            // This means 1 of three things:
            //                                 1. Both lists are emptyString and there is no wire of the selected color
            //                                 2. Both lists contain n IDs and there are n wires of the selected color
            //                                 3. One list contains n IDs and the other contains n-1 IDs and a -1, meaning
            //                                    that there are n-1 wires and one end has been removed from one of the wires
            //
            if (wireEnd1PinholeIds != null && wireEnd2PinholeIds != null && wireEnd1PinholeIds.size() == wireEnd2PinholeIds.size())
            {
                // If the Pinhole ID representing the first endpoint of a wire has been removed,
                // add the Pinhole Id of the selected pinhole to the index which corresponds
                // to the Pinhole ID representing the second endpoint in the second endpoint
                // list of the selected color.
                if (wireEnd1PinholeIds.contains(negativeOne))
                {
                    indexOfNegativeOne = wireEnd1PinholeIds.indexOf(negativeOne);

                    wireEnd1PinholeIds.set(indexOfNegativeOne, selectedPinholeId);
                    wireEnd1PinholeCenterCoordinates.set(indexOfNegativeOne, pinholeCenterPoint);

                    ProjectConfig.pinholeIdToWireDescription.put(selectedPinholeId, wireDescriptionPinholeEnd1);
                }
                else
                    // If the Pinhole ID representing the second endpoint of a wire has been removed,
                    // add the Pinhole Id of the selected pinhole to the index which corresponds
                    // to the Pinhole ID representing the first endpoint in the first endpoint
                    // list of the selected color.
                    if (wireEnd2PinholeIds.contains(negativeOne))
                    {
                        indexOfNegativeOne = wireEnd2PinholeIds.indexOf(negativeOne);

                        wireEnd2PinholeIds.set(indexOfNegativeOne, selectedPinholeId);
                        wireEnd2PinholeCenterCoordinates.set(indexOfNegativeOne, pinholeCenterPoint);

                        ProjectConfig.pinholeIdToWireDescription.put(selectedPinholeId, wireDescriptionPinholeEnd2);
                    }
                    else
                    // The lists are the same size and neither contain -1.
                    // Either no wires exist or all wires are complete.
                    //
                    // Add the selected Pinhole ID to the first endpoint
                    // list of the selected color.
                    {
                        wireEnd1PinholeIds.add(selectedPinholeId);
                        wireEnd1PinholeCenterCoordinates.add(pinholeCenterPoint);

                        ProjectConfig.pinholeIdToWireDescription.put(selectedPinholeId, wireDescriptionPinholeEnd1);

                        setSingleWireEndDescription(ProjectConfig.pinholeIdToWireDescription.get(selectedPinholeId));

                        //Toast.makeText(this, "Touch Another Pinhole to draw the " + color + " wire!", Toast.LENGTH_SHORT).show();
                    }
            }
            else
            {
                // If the endpoint lists of the selected color are not the same size,
                // the first endpoint list contains the Pinhole ID of a selected pinhole
                // representing the first endpoint of a new wire.

                // Add the Pinhole Id and coordinates of the selected Pinhole to the second endpoint list
                // of the selected color to complete the wire.
                if (wireEnd2PinholeIds != null)
                {
                    wireEnd2PinholeIds.add(selectedPinholeId);
                    wireEnd2PinholeCenterCoordinates.add(pinholeCenterPoint);

                    ProjectConfig.pinholeIdToWireDescription.put(selectedPinholeId, wireDescriptionPinholeEnd2);
                }
            }
        }

    }

    public void configureFullWire(String wireConfigOption, MainActivity activity)
    {
        String unplugEnd = "Unplug End";
        String removeWire = "Remove Wire";
        String changeWireColor = "Change Wire Color";

        int indexOfIdToReset = 0;
        int selectedPinholeId = getSelectedPinholeId();

        GradientDrawable selectedPinholeDrawable = getPinholeDrawable();

        // Lists that hold selected pinhole Ids
        List<Integer> wireEnd1PinholeIds;
        List<Integer> wireEnd2PinholeIds;

        // Lists that hold the selected pinhole center coordinates
        List<WireEndCoordinates> wireEnd1PinholeCenterCoordinates;
        List<WireEndCoordinates> wireEnd2PinholeCenterCoordinates;

        // Get the description of the selected endpoint Pinhole to Unplug
        String pinholeDescription = ProjectConfig.pinholeIdToWireDescription.get(selectedPinholeId);
        String descriptionOfOtherEnd = getDescriptionOfCorrespondingPinholeListFromPinholeId(selectedPinholeId);

        // Lists that hold the pinhole Ids of the selected wire color
        wireEnd1PinholeIds = ProjectConfig.wireDescriptionToIdMap.get(pinholeDescription);
        wireEnd2PinholeIds = ProjectConfig.wireDescriptionToIdMap.get(descriptionOfOtherEnd);

        // Lists that hold the pinhole coordinates of the selected wire color
        wireEnd1PinholeCenterCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(pinholeDescription);
        wireEnd2PinholeCenterCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(descriptionOfOtherEnd);

        if(wireEnd1PinholeIds != null)
        {
            // Get the index of the endpoint Pinhole being unplugged
            indexOfIdToReset = wireEnd1PinholeIds.indexOf(selectedPinholeId);
        }

        // If the user opts to remove one end of the wire
        if (wireConfigOption.equals(unplugEnd))
        {
            selectedPinholeDrawable.setStroke(outlineWidth, pinholeDefaultOutline);
            selectedPinholeDrawable.setColor(pinholeDefault);

            if (wireEnd1PinholeIds != null)
            {
                wireEnd1PinholeIds.set(indexOfIdToReset, negativeOne);
                wireEnd1PinholeCenterCoordinates.set(indexOfIdToReset, null);
            }

            setIndexContainingNegativeOne(indexOfIdToReset);

            ProjectConfig.pinholeIdToParentId.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToWireDescription.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToPinholeLayerDrawable.remove(selectedPinholeId);

            setSingleWireEndDescription(descriptionOfOtherEnd);
        }
        else
        if (wireConfigOption.equals(removeWire))
        {
            selectedPinholeDrawable.setStroke(outlineWidth, pinholeDefaultOutline);
            selectedPinholeDrawable.setColor(pinholeDefault);

            int otherPinholeId = wireEnd2PinholeIds.get(indexOfIdToReset);

            LayerDrawable otherPinholeLayer = ProjectConfig.pinholeIdToPinholeLayerDrawable.get(otherPinholeId);
            GradientDrawable otherPinhole = (GradientDrawable) otherPinholeLayer.findDrawableByLayerId(otherPinholeId);

            otherPinhole.setStroke(outlineWidth, pinholeDefaultOutline);
            otherPinhole.setColor(pinholeDefault);

            ProjectConfig.pinholeIdToParentId.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToParentId.remove(otherPinholeId);

            ProjectConfig.pinholeIdToWireDescription.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToWireDescription.remove(otherPinholeId);

            ProjectConfig.pinholeIdToPinholeLayerDrawable.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToPinholeLayerDrawable.remove(otherPinholeId);


            wireEnd1PinholeIds.remove(indexOfIdToReset);
            wireEnd1PinholeCenterCoordinates.remove(indexOfIdToReset);

            wireEnd2PinholeIds.remove(indexOfIdToReset);
            wireEnd2PinholeCenterCoordinates.remove(indexOfIdToReset);
        }
        else
        if (wireConfigOption.equals(changeWireColor))
        {
            setCorrespondingPinholeId(wireEnd2PinholeIds.get(indexOfIdToReset));
            setCorrespondingPinholeCenterPoint(wireEnd2PinholeCenterCoordinates.get(indexOfIdToReset));

            ProjectConfig.pinholeIdToWireDescription.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToWireDescription.remove(wireEnd2PinholeIds.get(indexOfIdToReset));

            wireEnd1PinholeIds.remove(indexOfIdToReset);
            wireEnd1PinholeCenterCoordinates.remove(indexOfIdToReset);

            wireEnd2PinholeIds.remove(indexOfIdToReset);
            wireEnd2PinholeCenterCoordinates.remove(indexOfIdToReset);

            changeWireColorFlag = true;

            mainActivityWeakReference = new WeakReference<>(activity);
            mainActivityWeakReference.get().showWireColorSelectionDialog(getCurrentView());
        }




    }

    public void configureWireEnd(String wireEndConfigOption, MainActivity activity)
    {
        String unplug = "Unplug";
        String changeColor = "Change Color";

        int indexOfIdToReset = 0;
        int selectedPinholeId = getSelectedPinholeId();

        GradientDrawable selectedPinholeDrawable = getPinholeDrawable();

        // Lists that hold selected pinhole Ids
        List<Integer> wireEnd1PinholeIds;
        List<Integer> wireEnd2PinholeIds;

        // Lists that hold the selected pinhole center coordinates
        List<WireEndCoordinates> wireEnd1PinholeCenterCoordinates;
        List<WireEndCoordinates> wireEnd2PinholeCenterCoordinates;

        // Get the description of the selected endpoint Pinhole to Unplug
        String pinholeDescription = ProjectConfig.pinholeIdToWireDescription.get(selectedPinholeId);
        String descriptionOfOtherEnd = getDescriptionOfCorrespondingPinholeListFromPinholeId(selectedPinholeId);

        // Lists that hold the pinhole Ids of the selected wire color
        wireEnd1PinholeIds = ProjectConfig.wireDescriptionToIdMap.get(pinholeDescription);
        wireEnd2PinholeIds = ProjectConfig.wireDescriptionToIdMap.get(descriptionOfOtherEnd);

        // Lists that hold the pinhole coordinates of the selected wire color
        wireEnd1PinholeCenterCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(pinholeDescription);
        wireEnd2PinholeCenterCoordinates = ProjectConfig.wireDescriptionToCoordinatesMap.get(descriptionOfOtherEnd);

        if(wireEnd1PinholeIds != null)
        {
            // Get the index of the endpoint Pinhole being unplugged
            indexOfIdToReset = wireEnd1PinholeIds.indexOf(selectedPinholeId);
        }

        // If the user opts to remove one end of the wire
        if (wireEndConfigOption.equals(unplug))
        {
            if(wireEnd1PinholeIds.size() == wireEnd2PinholeIds.size())
            {
                wireEnd2PinholeIds.remove(indexOfIdToReset);
                wireEnd2PinholeCenterCoordinates.remove(indexOfIdToReset);
            }

            selectedPinholeDrawable.setStroke(outlineWidth, pinholeDefaultOutline);
            selectedPinholeDrawable.setColor(pinholeDefault);

            wireEnd1PinholeIds.remove(indexOfIdToReset);
            wireEnd1PinholeCenterCoordinates.remove(indexOfIdToReset);

            ProjectConfig.pinholeIdToParentId.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToWireDescription.remove(selectedPinholeId);
            ProjectConfig.pinholeIdToPinholeLayerDrawable.remove(selectedPinholeId);

            setSingleWireEndDescription(emptyString);
        }
        else
        if (wireEndConfigOption.equals(changeColor))
        {
            if(wireEnd1PinholeIds.size() == wireEnd2PinholeIds.size())
            {
                wireEnd2PinholeIds.remove(indexOfIdToReset);
                wireEnd2PinholeCenterCoordinates.remove(indexOfIdToReset);
            }

            wireEnd1PinholeIds.remove(indexOfIdToReset);
            wireEnd1PinholeCenterCoordinates.remove(indexOfIdToReset);

            ProjectConfig.pinholeIdToWireDescription.remove(selectedPinholeId);

            mainActivityWeakReference = new WeakReference<>(activity);
            mainActivityWeakReference.get().showWireColorSelectionDialog(getCurrentView());
        }
    }

    private static String getDescriptionOfCorrespondingPinholeListFromDescription(String pinholeDescription)
    {
        return getCorrespondingPinholeDescription(pinholeDescription);
    }

    private static String getDescriptionOfCorrespondingPinholeListFromPinholeId(int pinholeIdNumber)
    {
        String pinholeDescription = ProjectConfig.pinholeIdToWireDescription.get(pinholeIdNumber);
        return getCorrespondingPinholeDescription(pinholeDescription);
    }

    private static String getCorrespondingPinholeDescription(String pinholeDescription)
    {
        String one = "1";
        String two = "2";

        String descriptionOfOtherEnd = "";

        if (Objects.nonNull(pinholeDescription))
        {
            descriptionOfOtherEnd = pinholeDescription.substring(0, pinholeDescription.length() - 1);
            String lastChar = pinholeDescription.substring(pinholeDescription.length() - 1);

            if (lastChar.equals(one))
            {
                descriptionOfOtherEnd = descriptionOfOtherEnd + two;
            }

            if (lastChar.equals(two))
            {
                descriptionOfOtherEnd = descriptionOfOtherEnd + one;
            }
        }

        return descriptionOfOtherEnd;

    }

    private String getSingleWireEndDescription()
    {
        return singleWireEndDescription;
    }

    private int getSelectedPinholeId()
    {
        return pinholeId;
    }

    private int getCorrespondingPinholeId()
    {
        return secondPinholeId;
    }

    private int getPinholeParentId()
    {
        return pinholeParentId;
    }

    private GradientDrawable getPinholeDrawable()
    {
        return pinhole;
    }

    private WireEndCoordinates getPinholeCenterPoint()
    {
        return centerOfPinhole;
    }

    private WireEndCoordinates getCorrespondingPinholeCenterPoint()
    {
        return centerOfSecondPinhole;
    }

    private View getCurrentView()
    {
        return currentView;
    }

    private int getIndexContainingNegativeOne()
    {
        return indexContainingNegativeOne;
    }

    private void setSelectedPinholeId(int id)
    {
        pinholeId = id;
    }

    private void setSingleWireEndDescription(String description)
    {
        singleWireEndDescription = description;
    }

    private void setCorrespondingPinholeId(int id)
    {
        secondPinholeId = id;
    }

    private void setPinholeParentId(int id)
    {
        pinholeParentId = id;
    }

    private void setPinholeDrawable(GradientDrawable phole)
    {
        pinhole = phole;
    }

    private void setPinholeCenterPoint(int x, int y)
    {
        centerOfPinhole = new WireEndCoordinates(x,y);
    }

    private void setCorrespondingPinholeCenterPoint(WireEndCoordinates secondCenter)
    {
        centerOfSecondPinhole = secondCenter;
    }

    private void setCurrentView(View cView)
    {
        currentView = cView;
    }

    private void setIndexContainingNegativeOne(int index)
    {
        indexContainingNegativeOne = index;
    }
}
