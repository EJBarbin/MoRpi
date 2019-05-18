package com.csc400.eric.morpi.Pins;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.csc400.eric.morpi.PiProjectCodeBuilder;
import com.csc400.eric.morpi.ProjectConfig;
import com.csc400.eric.morpi.R;

public class PinFragment extends Fragment implements View.OnClickListener
{
    public static String pinToSet;
    public static Drawable pin_default_pressed;

    private Drawable pin_default;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        pin_default_pressed = getActivity().getDrawable(R.drawable.pin_default_pressed);
        pin_default = getActivity().getDrawable(R.drawable.pin_default);

        View view = inflater.inflate(R.layout.pin_fragment, container, false);

        view.findViewById(R.id.pin_1).setOnClickListener(this);
        view.findViewById(R.id.pin_2).setOnClickListener(this);
        view.findViewById(R.id.pin_3).setOnClickListener(this);
        view.findViewById(R.id.pin_4).setOnClickListener(this);
        view.findViewById(R.id.pin_5).setOnClickListener(this);
        view.findViewById(R.id.pin_6).setOnClickListener(this);
        view.findViewById(R.id.pin_7).setOnClickListener(this);
        view.findViewById(R.id.pin_8).setOnClickListener(this);
        view.findViewById(R.id.pin_9).setOnClickListener(this);
        view.findViewById(R.id.pin_10).setOnClickListener(this);
        view.findViewById(R.id.pin_11).setOnClickListener(this);
        view.findViewById(R.id.pin_12).setOnClickListener(this);
        view.findViewById(R.id.pin_13).setOnClickListener(this);
        view.findViewById(R.id.pin_14).setOnClickListener(this);
        view.findViewById(R.id.pin_15).setOnClickListener(this);
        view.findViewById(R.id.pin_16).setOnClickListener(this);
        view.findViewById(R.id.pin_17).setOnClickListener(this);
        view.findViewById(R.id.pin_18).setOnClickListener(this);
        view.findViewById(R.id.pin_19).setOnClickListener(this);
        view.findViewById(R.id.pin_20).setOnClickListener(this);
        view.findViewById(R.id.pin_21).setOnClickListener(this);
        view.findViewById(R.id.pin_22).setOnClickListener(this);
        view.findViewById(R.id.pin_23).setOnClickListener(this);
        view.findViewById(R.id.pin_24).setOnClickListener(this);
        view.findViewById(R.id.pin_25).setOnClickListener(this);
        view.findViewById(R.id.pin_26).setOnClickListener(this);
        view.findViewById(R.id.pin_27).setOnClickListener(this);
        view.findViewById(R.id.pin_28).setOnClickListener(this);
        view.findViewById(R.id.pin_29).setOnClickListener(this);
        view.findViewById(R.id.pin_30).setOnClickListener(this);
        view.findViewById(R.id.pin_31).setOnClickListener(this);
        view.findViewById(R.id.pin_32).setOnClickListener(this);
        view.findViewById(R.id.pin_33).setOnClickListener(this);
        view.findViewById(R.id.pin_34).setOnClickListener(this);
        view.findViewById(R.id.pin_35).setOnClickListener(this);
        view.findViewById(R.id.pin_36).setOnClickListener(this);
        view.findViewById(R.id.pin_37).setOnClickListener(this);
        view.findViewById(R.id.pin_38).setOnClickListener(this);
        view.findViewById(R.id.pin_39).setOnClickListener(this);
        view.findViewById(R.id.pin_40).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        changePinState(v);
    }

    public void changePinState(View view)
    {
        int index;

        int textColorBlack = 0xFF000000;
        int textColorGray = 0xFF808080;

        String on = "on";
        String off = "off";
        String gpio = "GPIO";
        String whitespace = " ";



        Button pressedPin = getActivity().findViewById(view.getId());

        if (pressedPin.getContentDescription().equals(off))
        {
            pressedPin = setStateOfPin(pressedPin, pin_default_pressed, textColorBlack, on);

            ProjectConfig.pins.add(pressedPin.getId());

            // Generate Python code.
            if (pressedPin.getText().length() >= 4 && pressedPin.getText().subSequence(0, 4).toString().equals(gpio))
            {
                String[] gpioNum = pressedPin.getText().subSequence(4, pressedPin.getText().length()).toString().split(whitespace);
                String num = gpioNum[0];
                String setPinVarName = "LedPin";

                if (PiProjectCodeBuilder.projectSourceCode.get(1).isEmpty())
                {
                    PiProjectCodeBuilder.projectSourceCode.get(1).add(setPinVarName + " = " + num);
                }
                else
                {
                    PiProjectCodeBuilder.projectSourceCode.get(1).set(0, setPinVarName + " = " + num);
                }

                pinToSet = setPinVarName;
            }
            else
            {
                System.out.println("Pin: " + pressedPin.getText());
            }

            //for(String str : PiProjectCodeBuilder.projectSourceCode.get(1))
            //{
            //    System.out.println("Code : " + str);
            //}
        }
        else
        if (pressedPin.getContentDescription().equals(on))
        {
            pressedPin = setStateOfPin(pressedPin, pin_default, textColorGray, off);

            index = ProjectConfig.pins.indexOf(pressedPin.getId());
            ProjectConfig.pins.remove(index);

            // Generate Python code.
            if (pressedPin.getText().length() >= 4 && pressedPin.getText().subSequence(0, 4).toString().equals(gpio))
            {
                String[] gpioNum = pressedPin.getText().subSequence(4, pressedPin.getText().length()).toString().split(whitespace);
                String num = gpioNum[0];

                System.out.println("gpio number: " + num);

                PiProjectCodeBuilder.projectSourceCode.get(1).remove("LedPin" +  num + " = " + num);
            }
            else
            {
                System.out.println("Pin: " + pressedPin.getText());
            }
        }
    }

    public Button setStateOfPin(Button selectedPin, Drawable state, int color, String description)
    {
        selectedPin.setBackground(state);
        selectedPin.setTextColor(color);
        selectedPin.setContentDescription(description);

        return selectedPin;
    }

}
