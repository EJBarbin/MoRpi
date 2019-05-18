package com.csc400.eric.morpi;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.csc400.eric.morpi.Breadboard.BreadboardUtil;

import com.csc400.eric.morpi.DialogsMain.EditProjectSourceCodeDialog;
import com.csc400.eric.morpi.DialogsMain.RaspberryPiLoginDialog;
import com.csc400.eric.morpi.DialogsMain.ProjectSelectionDialog;
import com.csc400.eric.morpi.DialogsMain.SaveAsDialog;
import com.csc400.eric.morpi.Hardware.Dialogs.ComponentConfigDialog;
import com.csc400.eric.morpi.Hardware.Dialogs.ComponentFunctionalityModifierDialog;
import com.csc400.eric.morpi.Hardware.HardwareComponent;
import com.csc400.eric.morpi.Hardware.HardwareComponentProperties;
import com.csc400.eric.morpi.Hardware.Resistor;
import com.csc400.eric.morpi.Hardware.SingleColorLED;
import com.csc400.eric.morpi.SSHTasks.SSHTaskExecShellCmd;
import com.csc400.eric.morpi.SSHTasks.SSHTaskProjectConfigFileRetrieval;
import com.csc400.eric.morpi.SSHTasks.SSHTaskProjectConfigSave;
import com.csc400.eric.morpi.Wiring.Dialogs.WireColorSelectionDialog;
import com.csc400.eric.morpi.Wiring.Dialogs.WireConfigurationOptionsDialog;
import com.csc400.eric.morpi.Wiring.Dialogs.WireEndConfigurationOptionsDialog;
import com.csc400.eric.morpi.Wiring.WiringConfig;
import com.csc400.eric.morpi.Wiring.WiringConfigurationDisplay;

import java.util.List;


public class MainActivity extends AppCompatActivity implements WireColorSelectionDialog.WireColorSelectionListener,
        WireConfigurationOptionsDialog.WireConfigOptionsDialogListener, WireEndConfigurationOptionsDialog.WireEndConfigOptionsDialogListener,
        RaspberryPiLoginDialog.RaspberryPiLoginDialogListener, ProjectSelectionDialog.ProjectSelectionListener, SaveAsDialog.saveAsDialogListener,
        ComponentConfigDialog.ComponentConfigListener, ComponentFunctionalityModifierDialog.ModifyComponentFunctionalityDialogListener,
        EditProjectSourceCodeDialog.modifyProjectSourceCodeListener
{
    /** Used to display the wiring configuration */
    public WiringConfigurationDisplay mWiringConfigurationDisplay;

    /** Handles all wiring
     * configuration options */
    WiringConfig wiringConfig;

    /** Layout holding all hardware components */
    ConstraintLayout parentHardwareLayout;

    /** Indicates whether a selected
     * project should be deleted */
    private boolean isToBeDeleted;
    /** Indicates whether the user
     * is logged in to the Pi */
    private boolean isLoggedInToPi;

    /** Pi Login credentials */
    private String username;
    private String password;
    private String ipAddress;
    /** Current project name and
     * paths for writing and running */
    private String currentProjectName;
    private String currentProjectPath;
    private String pathToProjectPythonFile;
    /** Pi project source code to be
     * written to the Pi */
    private String piProjectSourceCode;
    /** Full source code or code snippet
     * to be edited. */
    private String sourceCodeToEdit;

    /** Last hardware component clicked by the user */
    private HardwareComponent clickedHardwareComponent;

    /** Dialog which allows the user to modify component functionality */
    private ComponentFunctionalityModifierDialog componentFunctionalityModifierDialog;

    private final String TAG = "Main Activity";

    // Needed for Wiring Config


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHorizontalScrollCenterInPortrait();
        parentHardwareLayout = findViewById(R.id.constraintLayoutHardware);
        BreadboardUtil.setDisplayMetrics(getResources().getDisplayMetrics());
        BreadboardUtil.initPinholes();
        BreadboardUtil.convertAllDpToSp();
        initBreadboardPinholeOnTouchListeners();

        ProjectConfig.init();

        PiProjectCodeBuilder.initializeCodeLists();

        // Wiring
        wiringConfig = new WiringConfig();
        mWiringConfigurationDisplay = findViewById(R.id.wiringConfigDisplay);
        mWiringConfigurationDisplay.initializeWireColors();

        Log.i(TAG, "onCreate Finished!");
    }

    /**
     * Centers the display on startup since the breadboard
     * interface is larger than the screen.
     */
    private void setHorizontalScrollCenterInPortrait()
    {
        final int xCoordinateStart = 152;
        final int yCoordinateStart = 0;
        final int delay = 100;

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            final HorizontalScrollView hsv = findViewById(R.id.horizontalScrollView);
            hsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hsv.smoothScrollBy(xCoordinateStart, yCoordinateStart);
                }
            }, delay);
        }
    }

    /**
     * Initializes the listeners of the six layouts containing
     * pinholes so that user touch coordinates can be read in
     * when a pinhole is selected.
     */
    public void initBreadboardPinholeOnTouchListeners()
    {
        setBreadboardPinholeOnTouchListener(R.id.leftPosNegPinholes, BreadboardUtil.posNegPinholeIndices);
        setBreadboardPinholeOnTouchListener(R.id.leftPinholesOneToEighty, BreadboardUtil.pinholeIndicesOneToEighty);
        setBreadboardPinholeOnTouchListener(R.id.leftPinholesEightyOneToOneForty, BreadboardUtil.pinholeIndicesEightyOneToOneForty);
        setBreadboardPinholeOnTouchListener(R.id.rightPosNegPinholes, BreadboardUtil.posNegPinholeIndices);
        setBreadboardPinholeOnTouchListener(R.id.rightPinholesOneToEighty, BreadboardUtil.pinholeIndicesOneToEighty);
        setBreadboardPinholeOnTouchListener(R.id.rightPinholesEightyOneToOneForty, BreadboardUtil.pinholeIndicesEightyOneToOneForty);
    }

    /**
     * Sets the on touch listener of a layout.
     *
     * @param constraintLayoutId the layout to set the listener of
     * @param layoutIndices int 2D array containing the index of each pinhole in its layout
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setBreadboardPinholeOnTouchListener(final int constraintLayoutId, final int[][] layoutIndices)
    {
        final ConstraintLayout constraintLayout = findViewById(constraintLayoutId);
        final LayerDrawable layerDrawable = (LayerDrawable) constraintLayout.getBackground();

        constraintLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    respondToPinholeTouchEvents(constraintLayout, layerDrawable, layoutIndices, constraintLayoutId,
                            (int)event.getX(), (int)event.getY(),   (int)v.getTranslationX(), (int)v.getTranslationY());
                }
                return true;
            }
        });
    }

// MAIN OPTIONS

    /**
     * Inflate main options menu.
     *
     * @param menu menu to be inflated.
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Performs an action based on
     * the selected menu option.
     *
     * @param item the selected menu item
     * @return true
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.login_pi:
                showRaspberryPiLoginDialog();
                return true;

            case R.id.login_github:
                //showGitHubLoginDialog();
                System.out.println("**************Resistors**************");
                List<HardwareComponentProperties> res220 = ProjectConfig.componentDescriptionToPropertiesMap.get("Resistor220");
                for (HardwareComponentProperties prop : res220)
                {
                    System.out.println("Description: " + prop.getComponentDescription());
                    System.out.println("x: " + prop.getTopLeftX());
                    System.out.println("size: " + res220.size());
                }
                System.out.println("**************Red LEDs**************");
                List<HardwareComponentProperties> redLeds = ProjectConfig.componentDescriptionToPropertiesMap.get("RedLED");
                for (HardwareComponentProperties prop : redLeds)
                {
                    System.out.println("Description: " + prop.getComponentDescription());
                    System.out.println("x: " + prop.getTopLeftX());
                    System.out.println("size: " + redLeds.size());
                }
                return true;

            case R.id.open_project_templates_pi:
                currentProjectPath = ProjectConfig.pathToProjectTemplates;
                validateLoginAndShowProjectSelectionDialog(false, "Select Project Template", ProjectConfig.projectTemplates);
                return true;

            case R.id.open_user_projects_pi:
                currentProjectPath = ProjectConfig.pathToUserProjects;
                validateLoginAndShowProjectSelectionDialog(false, "Select User Project", ProjectConfig.userProjects);
                return true;

            case R.id.open_from_github:
                return true;

            case R.id.save_to_pi:
                saveCurrentProject();
                return true;

            case R.id.save_to_github:
                return true;

            case R.id.delete_project_template_pi:
                currentProjectPath = ProjectConfig.pathToProjectTemplates;
                validateLoginAndShowProjectSelectionDialog(true, "Delete Project Template", ProjectConfig.projectTemplates);
                return true;

            case R.id.delete_user_project_pi:
                currentProjectPath = ProjectConfig.pathToUserProjects;
                validateLoginAndShowProjectSelectionDialog(true, "Delete User Project", ProjectConfig.userProjects);
                return true;

            case R.id.delete_github:
                return true;

        // *** Wiring Config ***
            case R.id.wires_only:
                mWiringConfigurationDisplay.drawWiringConfigWithNums(false);
                return true;

            case R.id.wires_and_nums:
                mWiringConfigurationDisplay.drawWiringConfigWithNums(true);
                return true;

            case R.id.all_wires:
                mWiringConfigurationDisplay.drawWiresOfSelectedColor(0, 10);
                return true;

            case R.id.white_wires:
                mWiringConfigurationDisplay.drawWiresOfSelectedColor(0, 2);
                return true;

            case R.id.black_wires:
                mWiringConfigurationDisplay.drawWiresOfSelectedColor(2, 4);
                return true;

            case R.id.red_wires:
                mWiringConfigurationDisplay.drawWiresOfSelectedColor(4, 6);
                return true;

            case R.id.blue_wires:
                mWiringConfigurationDisplay.drawWiresOfSelectedColor(6, 8);
                return true;

            case R.id.yellow_wires:
                mWiringConfigurationDisplay.drawWiresOfSelectedColor(8, 10);
                return true;
        // *** End Wiring Config ***

        // *** HardwareComponents components ***
            case R.id.resistor_220:
                Resistor resistor = new Resistor(this, "220 Ohm", ProjectConfig.componentDescriptionResistor220);
                resistor.setCustomBackground(getDrawable(R.drawable.resistor_background));
                resistor.create(this);
                return true;

            case R.id.red_led:
                SingleColorLED redLed = new SingleColorLED(this, ProjectConfig.componentDescriptionRedLED);
                redLed.setCustomBackground(getDrawable(R.drawable.red_led_background));
                redLed.addComponentCodeToProjectSourceCode();
                redLed.create(this);
                return true;
        // *** End HardwareComponents ***

            case R.id.clear_configuration:
                ProjectConfig.clearProjectConfiguration(this);
                mWiringConfigurationDisplay.draw();
                return true;

            case R.id.open_source_code:
                // Show Full Source Code
                String code = PiProjectCodeBuilder.getProjectSourceCode();
                setSourceCodeToEdit(code);
                showProjectSourceCodeEditor(true, PiProjectCodeBuilder.getNumOfLinesInSourceCode());

                return true;

            case R.id.run_project:
                runCurrentProject();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

// EDIT SOURCE CODE

    /**
     * Shows the dialog which allows the user to edit source code.
     *
     * @param showFullCode bool indicating whether the full code is shown
     * @param minLines int number of lines to show in the EditText
     */
    public void showProjectSourceCodeEditor(boolean showFullCode, int minLines)
    {
        EditProjectSourceCodeDialog editProjectSourceCodeDialog = new EditProjectSourceCodeDialog();
        editProjectSourceCodeDialog.setProjectSourceCodeToEdit(getSourceCodeToEdit());
        editProjectSourceCodeDialog.setShowFullCode(showFullCode);
        editProjectSourceCodeDialog.setMinLines(minLines);
        editProjectSourceCodeDialog.show(getSupportFragmentManager(), "Edit Project Source Code");
    }

    /**
     * Accepts the edited Pi project source code.
     *
     * @param projectSourceCode edited Pi project source code
     * @param showFullCode
     */
    @Override
    public void modifyProjectSourceCode(String projectSourceCode, boolean showFullCode)
    {
        if(showFullCode)
        {
            piProjectSourceCode = projectSourceCode;
        }
        else
        {
            getClickedHardwareComponent().modifySourceCodeSnippet(projectSourceCode);
            componentFunctionalityModifierDialog.dismiss();

            piProjectSourceCode = PiProjectCodeBuilder.getProjectSourceCode();
        }
    }

    /**
     * onClick listener for the edit source code button shown
     * on the ComponentFunctionalityModifier dialog.
     *
     * @param view the view implementing this listener
     */
    public void onClickShowSourceCode(View view)
    {
        int oneLine = 1;
        showProjectSourceCodeEditor(false, oneLine);
    }

    /**
     * Returns the source code to be edited.
     *
     * @return source code to be edited
     */
    private String getSourceCodeToEdit()
    {
        return sourceCodeToEdit;
    }

    /**
     * Sets the source code to be edited.
     *
     * @param code source code to be edited.
     */
    public void setSourceCodeToEdit(String code)
    {
        sourceCodeToEdit = code;
    }

// COMPONENT CONFIG

    /**
     * Displays the main component
     * configuration dialog.
     */
    public void showComponentConfigOptions()
    {
        ComponentConfigDialog componentConfigDialog = new ComponentConfigDialog();
        componentConfigDialog.setComponentConfigOptions(getClickedHardwareComponent().getConfigOptions());
        componentConfigDialog.show(getSupportFragmentManager(), "Component Config Dialog");
    }

    @Override
    public void componentConfigDialog(String configOption)
    {
        getClickedHardwareComponent().setMainActivityWeakRef(this);
        getClickedHardwareComponent().executeComponentConfigModification(configOption);
    }

    public void setComponentListener(HardwareComponent component)
    {
        component.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                setClickedHardwareComponent(v);

                showComponentConfigOptions();
            }
        });
    }

    public void showComponentFunctionalityModifierDialog(String title)
    {
        componentFunctionalityModifierDialog = new ComponentFunctionalityModifierDialog();
        componentFunctionalityModifierDialog.setTitle(title);
        componentFunctionalityModifierDialog.show(getSupportFragmentManager(), "Component Functionality Modifier Dialog");
    }

    @Override
    public void modifyComponentFunctionality(String value)
    {
        getClickedHardwareComponent().modifyComponentFunctionality(value);
    }

    private HardwareComponent getClickedHardwareComponent()
    {
        return clickedHardwareComponent;
    }

    private void setClickedHardwareComponent(View view)
    {
        clickedHardwareComponent = (HardwareComponent) view;
    }


// WIRING

    public void respondToPinholeTouchEvents(ConstraintLayout parentConstraintLayout, LayerDrawable pinholeLayerDrawable, int[][] pinholeIndices, int parentId, int xTouch, int yTouch, int xTrans, int yTrans)
    {
        wiringConfig.respondToPinholeTouchEvents(parentConstraintLayout, pinholeLayerDrawable, pinholeIndices, parentId, xTouch, yTouch, xTrans, yTrans, this);
    }

    // Wire Color
    public void showWireColorSelectionDialog(View view)
    {
        WireColorSelectionDialog wireColorSelectionDialog = new WireColorSelectionDialog();
        wireColorSelectionDialog.show(getSupportFragmentManager(), "wire_color_selection_dialog");
    }

    @Override
    public void onWireColorSelector(String color)
    {
        wiringConfig.createNewWire(color);
        mWiringConfigurationDisplay.draw();
    }

    // Wire Config --- Full Wire
    public void showFullWireConfigOptionsDialog(View view)
    {
        WireConfigurationOptionsDialog wiringConfigurationOptionsDialog = new WireConfigurationOptionsDialog();
        wiringConfigurationOptionsDialog.show(getSupportFragmentManager(), "wire_config_options_dialog");
    }
    @Override
    public void onWireConfigOptionSelector(String wireConfigOption)
    {
        wiringConfig.configureFullWire(wireConfigOption, this);
        mWiringConfigurationDisplay.draw();
    }

    // Wire Config --- One End
    public void showWireEndConfigOptionsDialog(View view)
    {
        WireEndConfigurationOptionsDialog wireEndConfigurationOptionsDialog = new WireEndConfigurationOptionsDialog();
        wireEndConfigurationOptionsDialog.show(getSupportFragmentManager(), "wire_end_config_options_dialog");
    }
    @Override
    public void onWireEndConfigOptionSelector(String wireEndConfigOption)
    {
        wiringConfig.configureWireEnd(wireEndConfigOption, this);
        mWiringConfigurationDisplay.draw();

    }

// LOGINS

    public void showRaspberryPiLoginDialog()
    {
        RaspberryPiLoginDialog raspberryPiLoginDialog = new RaspberryPiLoginDialog();
        raspberryPiLoginDialog.show(getSupportFragmentManager(), "Pi Login Dialog");
    }

    @Override
    public void loginToRaspberryPi(String piUsername, String piPassword, String piIpAddress)
    {
        username = piUsername;
        password = piPassword;
        ipAddress = piIpAddress;

        SSHTaskExecShellCmd piLogin = new SSHTaskExecShellCmd(this);
        piLogin.setPiUsername(username);
        piLogin.setPiPassword(password);
        piLogin.setPiIpAddress(ipAddress);
        piLogin.setCommand("pwd");
        piLogin.execute();
    }

// PROJECT SELECTION

    public void showProjectTemplateSelectionDialog(String title, String[] projects)
    {
        ProjectSelectionDialog projectSelectionDialog = new ProjectSelectionDialog();
        projectSelectionDialog.setTitle(title);
        projectSelectionDialog.setProjectsToDisplay(projects);
        projectSelectionDialog.show(getSupportFragmentManager(), "Project Selection Dialog");
    }

    @Override
    public void projectSelectionDialog(String projectName)
    {
        // Delete Project Directory
        if(isToBeDeleted)
        {
            currentProjectPath = currentProjectPath + projectName;

            // Delete project template from Pi
            SSHTaskExecShellCmd deleteProject = new SSHTaskExecShellCmd(this);
            deleteProject.setPiUsername(username);
            deleteProject.setPiPassword(password);
            deleteProject.setPiIpAddress(ipAddress);
            deleteProject.setProjectName(projectName);
            deleteProject.setCommand("rm -r " + currentProjectPath);
            deleteProject.execute();
        }
        // Open Project
        else
        {
            ProjectConfig.clearProjectConfiguration(this);
            mWiringConfigurationDisplay.draw();

            currentProjectName = projectName;
            currentProjectPath = currentProjectPath + projectName;
            pathToProjectPythonFile = currentProjectPath + "/python/" +  projectName + ".py";

            // Open Project from Pi
            SSHTaskProjectConfigFileRetrieval fileRetrieval = new SSHTaskProjectConfigFileRetrieval(this);
            fileRetrieval.setPiUsername(username);
            fileRetrieval.setPiPassword(password);
            fileRetrieval.setPiIpAddress(ipAddress);
            fileRetrieval.setProjectPath(currentProjectPath);
            fileRetrieval.setIsSftp(true);
            fileRetrieval.execute();
        }
    }

// SAVE AS

    private void saveCurrentProject()
    {
        if(isLoggedInToPi)
        {
            showSaveProjectDialog();
        }
        else
        {
            showRaspberryPiLoginDialog();
        }
    }

    public void showSaveProjectDialog()
    {
        SaveAsDialog saveAsDialog = new SaveAsDialog();
        saveAsDialog.show(getSupportFragmentManager(), "Save as Dialog");
    }

    @Override
    public void saveToPiAs(String projectName)
    {
        currentProjectName = projectName;
        //DB
        //currentProjectPath = ProjectConfig.pathToProjectTemplates + projectName;
        currentProjectPath = ProjectConfig.pathToUserProjects + projectName;
        pathToProjectPythonFile = currentProjectPath + "/python/" +  projectName + ".py";

// Create new Project Directory
        SSHTaskExecShellCmd createUserProjectDirectory = new SSHTaskExecShellCmd(this);
        createUserProjectDirectory.setPiUsername(username);
        createUserProjectDirectory.setPiPassword(password);
        createUserProjectDirectory.setPiIpAddress(ipAddress);
        createUserProjectDirectory.setCommand("cp -r /home/pi/MoRPi/Config " + currentProjectPath);
        createUserProjectDirectory.execute();

// Save Project Configuration
        SSHTaskProjectConfigSave saveProjectConfig = new SSHTaskProjectConfigSave(this);
        saveProjectConfig.setPiUsername(username);
        saveProjectConfig.setPiPassword(password);
        saveProjectConfig.setPiIpAddress(ipAddress);
        saveProjectConfig.setProjectName(currentProjectName);
        saveProjectConfig.setProjectPath(currentProjectPath);
        saveProjectConfig.setIsSftp(true);
        saveProjectConfig.execute();

// Create project Python file

        piProjectSourceCode = PiProjectCodeBuilder.getProjectSourceCode();

        if(!PiProjectCodeBuilder.projectSourceCode.get(4).contains("    GPIO.cleanup()"))
        {
            PiProjectCodeBuilder.projectSourceCode.get(4).add("    GPIO.cleanup()");
        }


        SSHTaskExecShellCmd saveProjectSourceCode = new SSHTaskExecShellCmd(this);
        saveProjectSourceCode.setPiUsername(username);
        saveProjectSourceCode.setPiPassword(password);
        saveProjectSourceCode.setPiIpAddress(ipAddress);
        saveProjectSourceCode.setProjectName(currentProjectName);
        saveProjectSourceCode.setCommand("echo  \"" + piProjectSourceCode + "\" > " + pathToProjectPythonFile);
        saveProjectSourceCode.execute();
    }

// Run Project

    private void runCurrentProject()
    {
        if(isLoggedInToPi)
        {
            Toast.makeText(this, currentProjectName + " is running!", Toast.LENGTH_SHORT).show();

            SSHTaskExecShellCmd runProject = new SSHTaskExecShellCmd(this);
            runProject.setPiUsername(username);
            runProject.setPiPassword(password);
            runProject.setPiIpAddress(ipAddress);
            runProject.setProjectName(currentProjectName);
            runProject.setCommand("python " + pathToProjectPythonFile);
            runProject.execute();
        }
        else
        {
            showRaspberryPiLoginDialog();
        }
    }

    private void validateLoginAndShowProjectSelectionDialog(boolean isDelete, String projectTypeSelectionTitle, String[] projects)
    {
        if(isLoggedInToPi)
        {
            isToBeDeleted = isDelete;

            showProjectTemplateSelectionDialog(projectTypeSelectionTitle, projects);
        }
        else
        {
            showRaspberryPiLoginDialog();
        }
    }

    public ConstraintLayout getParentHardwareLayout()
    {
        return parentHardwareLayout;
    }

    public void setIsLoggedInToPi(boolean isLoggedIn)
    {
        isLoggedInToPi = isLoggedIn;
    }

}
