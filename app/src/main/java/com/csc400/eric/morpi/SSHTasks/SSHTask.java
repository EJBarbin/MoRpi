package com.csc400.eric.morpi.SSHTasks;

import android.os.AsyncTask;

import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Objects;
import java.util.Properties;

public abstract class SSHTask extends AsyncTask<Void, Void, String>
{

    private boolean isSftpChannel;

    private String command;
    private String piUsername;
    private String piPassword;
    private String piIpAddress;
    private String projectName;
    private String projectPath;


    private String TAG = "sshTask";

    private JSch jsch = new JSch();

    private Session session = null;

    private ChannelExec channelExec = null;
    protected ChannelSftp channelSftp = null;

    private Properties properties = new Properties();

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    protected StringBuilder output = new StringBuilder();

    protected String finalOutput;

    public abstract void doInBackgroundImpl();
    public abstract void onPostExecuteImpl();

    public SSHTask()
    {

    }

    @Override
    protected String doInBackground(Void... strings)
    {
        createSession();
        setSessionConfigProperties();
        createSessionConnection();

        if (!getIsSftp())
        {
            openSessionExecChannel();
            setExecChannelOutputStreamAndCommand();
            createChannelExecConnection();
            readInConsoleOutputExecChannel();
            disconnectExecChannel();
        }
        else
        {
            openSessionSftpChannel();

            doInBackgroundImpl();

            disconnectSftpChannel();
        }

        return output.toString();

    }

    @Override
    protected void onPostExecute(String consoleOutput)
    {
        super.onPostExecute(consoleOutput);

        finalOutput = consoleOutput.trim();

        onPostExecuteImpl();
    }










    /**
     * Sets the user's piUsername.
     *
     * @param username user's piUsername
     */
    public void setPiUsername(String username)
    {
        piUsername = username;
    }

    /**
     * Sets the user's piPassword.
     *
     * @param password user's piPassword
     */
    public void setPiPassword(String password)
    {
        piPassword = password;
    }

    /**
     * Gets the Ip address of the
     * Raspberry Pi to connect to.
     *
     * @param ipAddress Pi Ip Address
     */
    public void setPiIpAddress(String ipAddress)
    {
        piIpAddress = ipAddress;
    }

    /**
     * Gets the command to execute.
     *
     * @param cmd command to execute
     */
    public void setCommand(String cmd)
    {
        command = cmd;
    }

    public void setIsSftp(boolean isSftp)
    {
        isSftpChannel = isSftp;
    }

    public void setProjectName(String projName)
    {
        projectName = projName;
    }

    public void setProjectPath(String projPath)
    {
        projectPath = projPath;
    }

    /**
     * Gets the user's piUsername.
     *
     * @return users' piUsername
     */
    String getPiUsername()
    {
        return piUsername;
    }

    /**
     * Gets the user's piPassword.
     *
     * @return users' piPassword
     */
    String getPiPassword()
    {
        return piPassword;
    }

    /**
     * Gets the Ip address of the
     * Raspberry Pi to connect to.
     *
     * @return Pi Ip address
     */
    String getPiIpAddress()
    {
        return piIpAddress;
    }

    /**
     * Gets the command to execute.
     *
     * @return command to execute
     */
    String getCommand()
    {
        return command;
    }

    boolean getIsSftp()
    {
        return isSftpChannel;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getProjectPath()
    {
        return projectPath;
    }

    /**
     * Utility function for doInBackground().
     *
     * Creates a Session with the
     * user login credentials.
     */
    private void createSession()
    {
        int sshPort = 22;

        String logMessageCreateSessionFailed = "The jsch session could not be created.";

        try
        {
            session = jsch.getSession(getPiUsername(), getPiIpAddress(), sshPort);
            session.setPassword(getPiPassword());
        }
        catch (JSchException | NullPointerException e)
        {
            Log.i(TAG, logMessageCreateSessionFailed);
        }
    }

    /**
     * Utility function for doInBackground().
     *
     * Sets the session Properties.
     */
    private void setSessionConfigProperties()
    {
        String propertiesStrict = "StrictHostKeyChecking";
        String propertiesNo = "no";

        // Avoid asking for key confirmation
        properties.put(propertiesStrict, propertiesNo);

        if (Objects.nonNull(session))
        {
            session.setConfig(properties);
        }
    }

    /**
     * Utility function for doInBackground().
     *
     * Creates the session connection.
     */
    private void createSessionConnection()
    {
        String logMessageCreateSessionConnectionFailed = "The jsch session connection " +
                "could not be created.";

        try
        {
            if (Objects.nonNull(session))
            {
                session.connect();
            }
        }
        catch (JSchException | NullPointerException e)
        {
            Log.i(TAG, logMessageCreateSessionConnectionFailed);
        }
    }

    /**
     * Utility function for doInBackground().
     *
     * Opens the session channel for command execution.
     */
    private void openSessionExecChannel()
    {
        String channelType = "exec";
        String logMessageOpenChannelFailed = "The jsch session channel has not been opened.";

        try
        {
            if (Objects.nonNull(session))
            {
                channelExec = (ChannelExec) session.openChannel(channelType);
            }
        }
        catch (JSchException | NullPointerException e)
        {
            Log.i(TAG, logMessageOpenChannelFailed);
        }
    }

    /**
     * Utility function for doInBackground().
     *
     * Opens the session channel for command execution.
     */
    private void setExecChannelOutputStreamAndCommand()
    {
        if (Objects.nonNull(channelExec))
        {
            channelExec.setOutputStream(baos);
            channelExec.setCommand(getCommand());
        }
    }

    /**
     * Utility function for doInBackground().
     *
     * Creates the channel connection.
     */
    private void createChannelExecConnection()
    {
        String logMessageCreateChannelConnectionFailed = "The jsch session channel " +
                "has not been opened.";
        try
        {
            if (Objects.nonNull(channelExec))
            {
                channelExec.connect();
            }
        }
        catch (JSchException | NullPointerException e)
        {
            Log.i(TAG, logMessageCreateChannelConnectionFailed, e);
        }
    }

    private void readInConsoleOutputExecChannel()
    {
        int readByte;

        InputStream commandOutput;

        String logMessageReadInConsoleOutputFailed = "The console output has not been read.";

        try
        {
            commandOutput = channelExec.getInputStream();
            readByte = commandOutput.read();

            while (readByte != 0xffffffff)
            {
                output.append((char) readByte);
                readByte = commandOutput.read();
            }
        }
        catch (IOException | NullPointerException e)
        {
            Log.i(TAG, logMessageReadInConsoleOutputFailed, e);
        }
    }

    private void disconnectExecChannel()
    {
        if(Objects.nonNull(session))
        {
            session.disconnect();
        }

        if (Objects.nonNull(channelExec))
        {
            channelExec.disconnect();
        }
    }

    /**
     * Utility function for doInBackground().
     *
     * Opens the session channel for command execution.
     */
    private void openSessionSftpChannel()
    {
        String channelType = "sftp";
        String logMessageOpenChannelFailed = "The jsch session channel has not been opened.";

        try
        {
            channelSftp = (ChannelSftp)session.openChannel(channelType);
            channelSftp.connect();

        }
        catch(JSchException | NullPointerException e)
        {
            Log.i(TAG, logMessageOpenChannelFailed);
        }
    }

    private void disconnectSftpChannel()
    {
        if(Objects.nonNull(session))
        {
            session.disconnect();
        }

        if(Objects.nonNull(channelSftp))
        {
            channelSftp.disconnect();
        }
    }

}
