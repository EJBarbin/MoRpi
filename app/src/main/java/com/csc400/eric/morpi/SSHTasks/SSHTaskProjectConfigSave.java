package com.csc400.eric.morpi.SSHTasks;

import android.widget.Toast;

import com.csc400.eric.morpi.MainActivity;
import com.csc400.eric.morpi.PiProjectCodeBuilder;
import com.csc400.eric.morpi.ProjectConfig;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class SSHTaskProjectConfigSave extends SSHTask
{
    private WeakReference<MainActivity> mainActivityWeakReference;

    public SSHTaskProjectConfigSave(MainActivity activity)
    {
        mainActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void doInBackgroundImpl()
    {
        saveProjectConfigToPi();
    }

    @Override
    public void onPostExecuteImpl()
    {
        MainActivity activity = mainActivityWeakReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }

        getProjects();

        Toast.makeText(activity, getProjectName() + " has been saved to the Pi.", Toast.LENGTH_SHORT).show();
    }

    private void getProjects()
    {
        SSHTaskProjectNameRetrieval projectRetrieval = new SSHTaskProjectNameRetrieval();
        projectRetrieval.setPiUsername(getPiUsername());
        projectRetrieval.setPiPassword(getPiPassword());
        projectRetrieval.setPiIpAddress(getPiIpAddress());
        projectRetrieval.setIsSftp(true);
        projectRetrieval.execute();
    }

    private void saveProjectConfigToPi()
    {
    // Save Pin Config

        // Save GPIO Pin IDs
        createObjectOutputStream(ProjectConfig.pins, ProjectConfig.pathToPinIDsList);
        // Save Pinhole IDs to Parent IDs
        createObjectOutputStream(ProjectConfig.pinholeIdToParentId, ProjectConfig.pathToPinholeToParentIDsMap);
        // Save Pinhole ID to wire description
        createObjectOutputStream(ProjectConfig.pinholeIdToWireDescription, ProjectConfig.pathToPinholeIDsToWireDescriptionMap);

    // Save Wire IDs Config

        // White IDs
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.whiteWireEnd1), ProjectConfig.pathToWhiteWireEnd1IDsList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.whiteWireEnd2), ProjectConfig.pathToWhiteWireEnd2IDsList);
        // Black IDs
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.blackWireEnd1), ProjectConfig.pathToBlackWireEnd1IDsList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.blackWireEnd2), ProjectConfig.pathToBlackWireEnd2IDsList);
        // Red IDs
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.redWireEnd1), ProjectConfig.pathToRedWireEnd1IDsList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.redWireEnd2), ProjectConfig.pathToRedWireEnd2IDsList);
        // Blue IDs
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.blueWireEnd1), ProjectConfig.pathToBlueWireEnd1IDsList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.blueWireEnd2), ProjectConfig.pathToBlueWireEnd2IDsList);
        // Yellow IDs
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.yellowWireEnd1), ProjectConfig.pathToYellowWireEnd1IDsList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToIdMap.get(ProjectConfig.yellowWireEnd2), ProjectConfig.pathToYellowWireEnd2IDsList);

    // Save Wire Coordinates Config

        // White Coordinate
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.whiteWireEnd1), ProjectConfig.pathToWhiteWireEnd1CoordinatesList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.whiteWireEnd2), ProjectConfig.pathToWhiteWireEnd2CoordinatesList);
        // Black Coordinates
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.blackWireEnd1), ProjectConfig.pathToBlackWireEnd1CoordinatesList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.blackWireEnd2), ProjectConfig.pathToBlackWireEnd2CoordinatesList);
        // Red Coordinates
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.redWireEnd1), ProjectConfig.pathToRedWireEnd1CoordinatesList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.redWireEnd2), ProjectConfig.pathToRedWireEnd2CoordinatesList);
        // Blue Coordinates
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.blueWireEnd1), ProjectConfig.pathToBlueWireEnd1CoordinatesList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.blueWireEnd2), ProjectConfig.pathToBlueWireEnd2CoordinatesList);
        // Yellow Coordinates
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.yellowWireEnd1), ProjectConfig.pathToYellowWireEnd1CoordinatesList);
        createObjectOutputStream(ProjectConfig.wireDescriptionToCoordinatesMap.get(ProjectConfig.yellowWireEnd2), ProjectConfig.pathToYellowWireEnd2CoordinatesList);

    // Save project Python code
        createObjectOutputStream(PiProjectCodeBuilder.projectSourceCode, ProjectConfig.pathToDynamicCodeList);

    // Hardware components

        // Resistors
        createObjectOutputStream(ProjectConfig.componentDescriptionToPropertiesMap.get(ProjectConfig.componentDescriptionResistor220), ProjectConfig.pathTo220ohmResistorProperties);

        // LEDs
        createObjectOutputStream(ProjectConfig.componentDescriptionToPropertiesMap.get(ProjectConfig.componentDescriptionRedLED), ProjectConfig.pathToRedLedProperties);
    }

    private void createObjectOutputStream(Object obj, String configFilePath)
    {
        String absPath = getProjectPath() + configFilePath;

        try
        {
            OutputStream outputStream = channelSftp.put(absPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            outputStream.close();
        }
        catch(IOException | SftpException ioe)
        {
            ioe.printStackTrace();
        }
    }

}
