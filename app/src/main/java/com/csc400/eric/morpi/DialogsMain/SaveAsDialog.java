package com.csc400.eric.morpi.DialogsMain;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.csc400.eric.morpi.R;

public class SaveAsDialog extends AppCompatDialogFragment
{
    private EditText saveAsEditText;

    private saveAsDialogListener listener;

    public interface saveAsDialogListener
    {
        void saveToPiAs(String projectName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String title = "Save Project As..";
        final String cancel = "Cancel";
        final String ok = "Ok";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_edit_text_dialog, null);


        builder.setView(view)
                .setTitle(title)
                .setNegativeButton(cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String projectName = saveAsEditText.getText().toString();

                        listener.saveToPiAs(projectName);
                    }
                });

        saveAsEditText = view.findViewById(R.id.inputEditText);

        return builder.create();
    }



    @Override
    public void onAttach(Context context)
    {
        final String exceptionMessage = "must implement SaveAsDialogListener";

        super.onAttach(context);

        try {
            listener = (saveAsDialogListener) context;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() + exceptionMessage);
        }
    }
}

