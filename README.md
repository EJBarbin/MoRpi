# MoRPi

This is the path to the directory which contains only the application source code:
https://github.com/EJBarbin/MoRPi/tree/master/app/src/main/java/com/csc400/eric/morpi

Folder and File Organization:

Folders are organized into distinct general parts of the system, such as wiring and hardware.
Files are organized by the part of the system that they belong to.

The main application folder morpi holds the three most critical classes in the system:
MainActivity, PiProjectCodeBuilder, ProjectConfig

The BreadBoard folder holds the BreadBoardUtil class which is used for computing new coordinates on the interface.

The DialogsMain folder holds all of the dialogs which are launched from the MainActivity such as login, save, and editor dialogs.

The Hardware folder holds all classes related to the HardwareConfiguration, including:
the parent HardwareComponent class, the Resistor and SingleColorLED child classes, and the HardwareComponentPropertiesClass.
The Dialogs folder inside of the Hardware folder holds all dialog classes used for component modification.

The Pins folder holds the PinFragment Class, the only class related to the GPIO pin configuration.

The SSHTasks folder holds all classes related to SSH tasks such as:
the SSHTask parent class, and 4 child classes which all represent different types of SSHTasks.

The Wiring folder holds all classes related to the wiring configuration such as:
WireEndCOordinates, WiringCOnfig, and WireConfigurationDisplay.
The Dialogs folder inside of the wiring folder contains dialog classes used in the modification of the wiring configuration.
