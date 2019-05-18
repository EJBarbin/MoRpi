package com.csc400.eric.morpi.SSHTasks;

import android.widget.Toast;

import com.csc400.eric.morpi.MainActivity;
import com.jcraft.jsch.SftpException;

import java.lang.ref.WeakReference;

public class SSHTaskExecShellCmd extends SSHTask
{
    private WeakReference<MainActivity> mainActivityWeakReference;

    public SSHTaskExecShellCmd(MainActivity activity)
    {
        mainActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void doInBackgroundImpl()
    {
        MainActivity activity = mainActivityWeakReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }

        if(getCommand().equals("pwd"))
        {
            verifyHomeDirectory();
        }
        else
        // Run Project
        if(getCommand().contains("python "))
        {
            Toast.makeText(activity, getProjectName() + " has finished.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPostExecuteImpl()
    {
        MainActivity activity = mainActivityWeakReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }

        // Login
        if(getCommand().equals("pwd"))
        {
            if (finalOutput.equals("/home/pi"))
            {
                Toast.makeText(activity, "Pi Login Successful!", Toast.LENGTH_SHORT).show();
                activity.setIsLoggedInToPi(true);
                getProjects();
            }
            else
            {
                Toast.makeText(activity, "A connection could not be made to the Pi.", Toast.LENGTH_SHORT).show();
            }
        }
        else
        // Delete Project Directory
        if(getCommand().contains("rm -r "))
        {
            Toast.makeText(activity, getProjectName() + " has been deleted.", Toast.LENGTH_SHORT).show();
            getProjects();
        }
        else
        // Save Python file
        if(getCommand().contains("echo "))
        {
            Toast.makeText(activity, getProjectName() + " has been saved to the Pi.", Toast.LENGTH_SHORT).show();
        }
        else
        // Run Project
        if(getCommand().contains("python "))
        {
            Toast.makeText(activity, getProjectName() + " has finished.", Toast.LENGTH_SHORT).show();
        }

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

    private void verifyHomeDirectory()
    {
        try
        {
            output.append(channelSftp.pwd());
        }
        catch(SftpException ex)
        {
            ex.printStackTrace();
        }
    }

}
