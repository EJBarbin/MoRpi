package com.csc400.eric.morpi.Hardware.Dialogs;

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

public class ComponentFunctionalityModifierDialog extends AppCompatDialogFragment
{

    private static String title = "";

    private EditText functionalityModifierEditText;

    private ModifyComponentFunctionalityDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        final String cancel = "Cancel";
        final String ok = "Ok";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.project_code_editor_edit_text, null);

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
                        String projectName = functionalityModifierEditText.getText().toString();

                        listener.modifyComponentFunctionality(projectName);
                    }
                });

        functionalityModifierEditText = view.findViewById(R.id.inputEditText);

        return builder.create();
    }

    public interface ModifyComponentFunctionalityDialogListener
    {
        void modifyComponentFunctionality(String value);
    }

    @Override
    public void onAttach(Context context)
    {
        final String exceptionMessage = "must implement ModifyComponentFunctionalityDialogListener";

        super.onAttach(context);

        try {
            listener = (ModifyComponentFunctionalityDialogListener) context;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() + exceptionMessage);
        }
    }

    public void setTitle(String titleToSet)
    {
        title = titleToSet;
    }

}
