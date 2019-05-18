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

public class RaspberryPiLoginDialog extends AppCompatDialogFragment
{
    private EditText piUsernameEditText;
    private EditText piPasswordEditText;
    private EditText piIpAddressEditText;

    final String title = "Raspberry Pi Login";
    final String cancel = "Cancel";
    final String ok = "Ok";

    private RaspberryPiLoginDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.raspberry_pi_login_dialog, null);

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
                        String username = piUsernameEditText.getText().toString();
                        String password = piPasswordEditText.getText().toString();
                        String ipAddress = piIpAddressEditText.getText().toString();

                        listener.loginToRaspberryPi(username, password, ipAddress);
                    }
                });

        piUsernameEditText = view.findViewById(R.id.raspberryPiUsernameEditText);
        piPasswordEditText = view.findViewById(R.id.raspberryPiPasswordEditText);
        piIpAddressEditText = view.findViewById(R.id.raspberryPiIpAddressEditText);

        return builder.create();
    }


    public interface RaspberryPiLoginDialogListener
    {
        void loginToRaspberryPi(String username, String password, String ipAddress);
    }

    @Override
    public void onAttach(Context context)
    {
        final String exceptionMessage = "must implement RaspberryPiDialogListener";

        super.onAttach(context);

        try {
            listener = (RaspberryPiLoginDialogListener) context;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() + exceptionMessage);
        }
    }
}
