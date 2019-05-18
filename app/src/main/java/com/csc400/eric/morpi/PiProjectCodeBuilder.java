package com.csc400.eric.morpi;

import java.util.ArrayList;
import java.util.List;

public class PiProjectCodeBuilder
{
    public static List<List<String>> projectSourceCode = new ArrayList<>();

    private static int numOfLinesInSourceCode;


    private static List<String> imports = new ArrayList<>();
    private static List<String> globalPinAssignment = new ArrayList<>();
    private static List<String> pinSetup = new ArrayList<>();
    private static List<String> businessLogic = new ArrayList<>();
    private static List<String> cleanupAndDestroy = new ArrayList<>();
    private static List<String> main = new ArrayList<>();

    static void initializeCodeLists()
    {
        // 0
        PiProjectCodeBuilder.imports.add("import RPi.GPIO as GPIO");

        // 1 - no starter code for pin assignment

        // 2
        PiProjectCodeBuilder.pinSetup.add("def setup():");
        PiProjectCodeBuilder.pinSetup.add("    GPIO.setmode(GPIO.BCM)");

        // 3
        PiProjectCodeBuilder.businessLogic.add("def loop():");

        // 4
        PiProjectCodeBuilder.cleanupAndDestroy.add("def destroy():");

        // 5
        PiProjectCodeBuilder.main.add("if __name__ == '__main__':");
        PiProjectCodeBuilder.main.add("    setup()");
        PiProjectCodeBuilder.main.add("    loop()");
        PiProjectCodeBuilder.main.add("    destroy()");

        projectSourceCode.add(imports);
        projectSourceCode.add(globalPinAssignment);
        projectSourceCode.add(pinSetup);
        projectSourceCode.add(businessLogic);
        projectSourceCode.add(cleanupAndDestroy);
        projectSourceCode.add(main);
    }

    static String getProjectSourceCode()
    {
        int numOfLines = 0;

        StringBuilder code = new StringBuilder();

        for(int i =0; i < projectSourceCode.size(); i ++)
        {
            for(int j = 0; j < projectSourceCode.get(i).size(); j++)
            {
                code.append(projectSourceCode.get(i).get(j));
                code.append(System.getProperty("line.separator"));
                numOfLines += 1;
            }
            code.append(System.getProperty("line.separator"));
        }

        setNumOfLinesInSourceCode(numOfLines);

        return code.toString();

    }

    static int getNumOfLinesInSourceCode()
    {
        return numOfLinesInSourceCode;
    }

    static void setNumOfLinesInSourceCode(int numOfLines)
    {
        numOfLinesInSourceCode = numOfLines;
    }

}
