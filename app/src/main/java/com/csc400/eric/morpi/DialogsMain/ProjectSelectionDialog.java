package com.csc400.eric.morpi.DialogsMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


public class ProjectSelectionDialog extends DialogFragment
{
    private String selection;
    private String dialogTitle;

    private String[] projectsToDisplay;

    ProjectSelectionListener selectionListener;

    public interface ProjectSelectionListener
    {
        void projectSelectionDialog(String color);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String positiveButtonMessage = "OK";
        final String negativeButtonMessage = "Cancel";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);
        builder.setSingleChoiceItems(projectsToDisplay, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selection = projectsToDisplay[which];
            }
        });

        builder.setPositiveButton(positiveButtonMessage, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selectionListener.projectSelectionDialog(selection);
            }
        });

        builder.setNegativeButton(negativeButtonMessage, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        final String errorMessage = "Must implement userProjectSelectionListener interface methods";

        Activity activity = (Activity) context;

        try
        {
            selectionListener = (ProjectSelectionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + errorMessage);
        }

    }

    public void setTitle(String title)
    {
        dialogTitle = title;
    }

    public void setProjectsToDisplay(String[] toDisplay)
    {
        projectsToDisplay = toDisplay;
    }

}
