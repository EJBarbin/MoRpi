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

public class EditProjectSourceCodeDialog extends AppCompatDialogFragment
{
    private String codeToEdit = "";
    private boolean showFullCode;
    private int minLines = 1;

    private EditText modifyProjectSourceCodeEditText;

    private modifyProjectSourceCodeListener listener;

    public interface modifyProjectSourceCodeListener
    {
        void modifyProjectSourceCode(String projectSourceCode, boolean showFullCode);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String title = "Edit source code";
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

                    }
                })
                .setPositiveButton(ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String projectName = modifyProjectSourceCodeEditText.getText().toString();

                        listener.modifyProjectSourceCode(projectName, showFullCode);
                    }
                });

        modifyProjectSourceCodeEditText = view.findViewById(R.id.inputEditText);
        modifyProjectSourceCodeEditText.setText(codeToEdit);
        modifyProjectSourceCodeEditText.setMinLines(minLines);

        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        final String exceptionMessage = "must implement modifyProjectSourceCodeListener";

        super.onAttach(context);

        try {
            listener = (modifyProjectSourceCodeListener) context;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() + exceptionMessage);
        }
    }

    public void setProjectSourceCodeToEdit(String sourceCode)
    {
        codeToEdit = sourceCode;
    }

    public void setShowFullCode(boolean showFull)
    {
        showFullCode = showFull;
    }

    public void setMinLines(int lines)
    {
        minLines = lines;
    }

}
