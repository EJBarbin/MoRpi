package com.csc400.eric.morpi.Wiring.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.csc400.eric.morpi.R;

public class WireConfigurationOptionsDialog extends DialogFragment
{
    private String selection;

    public interface WireConfigOptionsDialogListener
    {
        void onWireConfigOptionSelector(String wireConfigOption);
    }

    WireConfigOptionsDialogListener selectionListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String dialogTitle = "Select Wire Config Option";
        final String positiveButtonMessage = "OK";
        final String negativeButtonMessage = "Cancel";

        final String[] wireConfigOptions = getActivity().getResources().getStringArray(R.array.wire_config_options);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);
        builder.setSingleChoiceItems(R.array.wire_config_options, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selection = wireConfigOptions[which];
            }
        });

        builder.setPositiveButton(positiveButtonMessage, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selectionListener.onWireConfigOptionSelector(selection);
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

        final String errorMessage = "Must implement WireConfigOptionsDialogListener interface methods";

        Activity activity = (Activity) context;

        try
        {
            selectionListener = (WireConfigOptionsDialogListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + errorMessage);
        }

    }
}
