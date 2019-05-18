package com.csc400.eric.morpi.SSHTasks;

import com.csc400.eric.morpi.ProjectConfig;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.util.Vector;

public class SSHTaskProjectNameRetrieval extends SSHTask
{

    @Override
    public void doInBackgroundImpl()
    {
        // Get all Project Template Directories
        getProjectDirectoryNames(ProjectConfig.pathToProjectTemplates);

        output.append("_");

        // Get all Project Template Directories
        getProjectDirectoryNames(ProjectConfig.pathToUserProjects);
    }

    @Override
    public void onPostExecuteImpl()
    {
        String[] types = finalOutput.split("_");

        // Get all Project Template Directories
        ProjectConfig.projectTemplates = types[0].split("!");

        // Get all User Project Directories

        if(types.length == 2)
        {
            ProjectConfig.userProjects = types[1].split("!");
        }
    }

    private void getProjectDirectoryNames(String path)
    {
        try
        {
            Vector fileList = channelSftp.ls(path);

            for(int i=0; i<fileList.size();i++)
            {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) fileList.get(i);

                if (!entry.getFilename().equals(".") && !entry.getFilename().equals(".."))
                {
                    output.append(entry.getFilename());

                    if (i < fileList.size()-1)
                    {
                        output.append("!");
                    }
                }
            }
        }
        catch(SftpException ex)
        {
            ex.printStackTrace();
        }

    }

}
