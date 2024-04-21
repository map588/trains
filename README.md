# J.A.M.E.S
TODO: Description of system

## Instalation

## Use Guides

### Table of Contents
1. [Simulation Setup](#simulation-setup)
1. [CTC](#ctc)
2. [Software Wayside Controller](#software-wayside-controller)
3. [Track Model](#track-model)
4. [Train Model](#train-model)
5. [Software Train Controller](#software-train-controller)
6. [Hardware Train Controller](#hardware-train-controller)

### Simulation Setup

To run the simulation, a csv file needs to be uploaded to represent all of the properties of the track.  The csv should be formatted as follows:

*Format*

The simulation is set to run at 1 second intervals.  The slide bar at the top of the menu allows the simulation to run up to 10x speed.  This can be seen in the time display also in the main menu.

### CTC

### Wayside Controller
To access the Wayside Controller GUI, first run the main program. Then, left-click the "waysideController" button. The Wayside Controller GUI will open. The Wayside Controller GUI displays the current state of the track, including the current state of the switches, lights, crossings, and boolean authority for all the track blocks that the currently selected wayside controller controls. To view the state of a different wayside controller, click the dropdown menu and select the desired wayside controller. The Wayside Controller GUI will automatically update to display the values of the selected wayside controller. The Wayside Controller GUI will display the values of the selected wayside controller until a new wayside controller is selected.

Each of the Wayside Controllers is governed by a PLC program. The PLC program is responsible for controlling the state of the track blocks. The PLC program for the wayside is enabled by default, but to swap out the PLC Program, first switch to the PLC tab. From this window, you may select the "Browse Folder" button to open a file explorer window. From here, you may select the folder containing the PLC program you wish to use. Once you have selected the folder, click the PLC file you wish to upload, and click the "Load PLC" button to load the PLC program. The Wayside Controller GUI will automatically update to display the new PLC program. Once the PLC Program is changed, the Wayside Controller will make decisions based on the new PLC program.

While the status of the track blocks is displayed in the Wayside Controller GUI, the Wayside Controller GUI does not allow the user to change the state of the track blocks in its default state. To begin changing track states, such as light states, switch states, crossing states, or boolean authority, check the "Maintenance Mode" checkbox to enable Maintenance Mode. Once Maintenance Mode is enabled, the PLC program will be disabled and the user will be able to change the state of the track blocks. To change the state of a track block, click the corresponding button to change the state of the track block (e.g. click the checkbox for Authority to toggle boolean authority for that block, or change the light switch to toggle the light state for that block). The Wayside Controller GUI will automatically update to display the new state of the track block. The Wayside Controller GUI will automatically update to display the new state of the selected track block. As long as Maintenance Mode is enabled, the user will be able to change the state of the track blocks. To disable Maintenance Mode, uncheck the "Maintenance Mode" checkbox. Once Maintenance Mode is disabled, the PLC program will be enabled and the user will not be able to change the state of the track blocks.

#### <i>Hardware Wayside Controller</i>

### Track Model
To access the Track Model GUI, first run the main program. Then, left-click the "trackModel" button. The Track Model GUI will open. The Track Model GUI displays the current state of the track, including the current state of the switches, lights, crossings, failures, track circuits, track heaters, and beacon information. All of the details about the track are displayed in the table for every block in a line.  To view other lines use the combo box located above the line table.

The number of lines will be determined by the uploaded csv file explained early in these instructions. When a train is dispatched onto the track, the block occupancies of that train will be displayed in the occupied column.  To view properties of a block, select the block in the table and the information on the left hand side will update based on the type of block.  If it is a station, passenger information will be updated accordingly.  If it is a switch, the switch states will be displayed as well as the block numbers of the switches.  Similarly, signal states are displayed as well as crossing states.  The beacon information will appear when a beacon block is selected in the table.  Finally, the track heaters automatically turn on based on the internally set environmental temperature.

To set a failure use the Murphy tab on the left hand side.  Here you can set different types of track failures to occur on the selected block.  The three types of failures are broken rail, track circuit failure, and power failure.  Pressing enter with the selected error will set the failure on that block in the table.  To undo a failure, there is an option to fix track failure in the combo box in the Murphy tab.  This will remove any failures on the selected block. 

### Train Model
To access the Train Model GUI, first run the main program. Then, left-click the "trainModel" button. The Train Model GUI will open. The Train Model GUI displays the current state of whichever train is selected, including the current speed, authority, and power. The Train Model GUI also displays the current state of the doors, lights, and brakes. If no train has been dispatched, the GUI will be filled with empty values until a train is dispatched.  Upon initial dispatch, the GUI will automatically update to display the values of the dispatched train.

To switch between different train models on a track, click the "Train No." dropdown menu and select the desired train. The Train Model GUI will automatically update to display the values of the selected train. The Train Model GUI will display the values of the selected train until a new train is selected.

To enable a Brake Failure, Power Failure, or Signal Pickup Failure, click the corresponding button. To disable a failure state, click the corresponding button again. The failure states are disabled by default. The failure states can be enabled and disabled at any time, even while the simulation is running. The failure states can be enabled and disabled in any combination and order.

To enable the Emergency Brake, click the Passenger Emergency Brake Button. The Emergency Brake cannot be disabled from the Train Model's GUI. The Emergency Brake can be enabled at any time, even while the simulation is running. The Emergency Brake can be enabled even in the case of brake failure.  The Emergency Brake cuts power to the engine and provides a negative acceleration to the train.

### Software Train Controller
To access the Train Controller GUI, first run the main program. Then, left-click the "trainController" button. The Train Controller GUI will open. The Train Controller GUI displays the current state of whatever train is selected, showcasing the current speed, authority, and power. The Train Controller GUI also displays the current states of the brakes, temperature, the name of the next station, whether the train is inside a tunnel, and the indicator lights for failure states. If no train is selected, the GUI will be un-interactable and will display empty values until a train is dispatched. Upon the initial dispatch of a train (the first train dispatched), the GUI will automatically update to reflect the values of the dispatched train.

To switch between train controllers for different trains, click the "Train No." dropdown menu and select the desired train. The GUI will automatically reflect the new values, and will subsequently change if a user selects a new train.

By default, the train controller is in Auto Mode, meaning the train will automatically adjust its power based on the commanded speed. Users can disable Auto Mode by unchecking the "Auto Mode" checkbox. When unchecked, the user can set a new setpoint speed for the train by either typing a double into a text field or by moving a slider to their desired speed. The power of the system would adjust accordingly.

By default, the Left and Right Doors, the Interior and Exterior lights, and the Service Brake are unchecked, users can check their respective checkboxes to enable the features. To disable the features, just uncheck the checkbox. In Auto Mode, these features would be automatically enabled and disabled depending on the scenario. Users can freely manipulate these values regardless of Auto Mode or not.

An "Announce" button is available to announce the arrival at a station. This will be automatically triggered in Auto Mode but can be manually triggered as well. 

The Ki, Kp, and SetTemperature text fields are set to their default values. The users can freely input a new double-value to change the current temperature or power output. Users can change these values during the simulation.

An indicator will light up showing whether the Emergency Brake is enabled or not. To enable the Emergency Brake, click on the Emergency Brake Button, the indicator light will light up.  To disable the Emergency Brake, simply click on the button again, and the indicator light will turn off. The Emergency Brake can be enabled at any time, even in the simulation. The Emergency Brake can be enabled in the case of a brake failure. The Emergency Brake cuts power and provides a negative acceleration to the train.

### Hardware Train Controller
