package com.csc400.eric.morpi.Hardware.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ComponentConfigDialog extends DialogFragment
{
    private String selection;

    private String[] componentConfigOptions;

    public interface ComponentConfigListener
    {
        void componentConfigDialog(String configOption);
    }

    ComponentConfigListener selectionListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String dialogTitle = "Select Configuration Option";
        final String positiveButtonMessage = "OK";
        final String negativeButtonMessage = "Cancel";


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);
        builder.setSingleChoiceItems(getComponentConfigOptions(), 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selection = getComponentConfigOptions()[which];
            }
        });

        builder.setPositiveButton(positiveButtonMessage, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selectionListener.componentConfigDialog(selection);
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

        final String errorMessage = "Must implement resistorConfigListener interface methods";

        Activity activity = (Activity) context;

        try
        {
            selectionListener = (ComponentConfigListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + errorMessage);
        }

    }

    private String[] getComponentConfigOptions()
    {
        return componentConfigOptions;
    }

    public void setComponentConfigOptions(String[] configOptions)
    {
        componentConfigOptions = configOptions;
    }

}
