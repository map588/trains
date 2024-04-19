# J.A.M.E.S
TODO: Description of system

## Instalation

## Use Guides

### Table of Contents
1. [CTC](#ctc)
2. [Software Wayside Controller](#software-wayside-controller)
3. [Track Model](#track-model)
4. [Train Model](#train-model)
5. [Software Train Controller](#software-train-controller)
6. [Hardware Train Controller](#hardware-train-controller)

### CTC

### Wayside Controller
To access the Wayside Controller GUI, first run the main program. Then, left-click the "waysideController" button. The Wayside Controller GUI will open. The Wayside Controller GUI displays the current state of the track, including the current state of the switches, lights, crossings, and boolean authority for all the track blocks that the currently selected wayside controller controls. To view the state of a different wayside controller, click the dropdown menu and select the desired wayside controller. The Wayside Controller GUI will automatically update to display the values of the selected wayside controller. The Wayside Controller GUI will display the values of the selected wayside controller until a new wayside controller is selected.

Each of the Wayside Controllers is governed by a PLC program. The PLC program is responsible for controlling the state of the track blocks. The PLC program for the wayside is enabled by default, but to swap out the PLC Program, first switch to the PLC tab. From this window, you may select the "Browse Folder" button to open a file explorer window. From here, you may select the folder containing the PLC program you wish to use. Once you have selected the folder, click the PLC file you wish to upload, and click the "Load PLC" button to load the PLC program. The Wayside Controller GUI will automatically update to display the new PLC program. Once the PLC Program is changed, the Wayside Controller will make decisions based on the new PLC program.

While the status of the track blocks is displayed in the Wayside Controller GUI, the Wayside Controller GUI does not allow the user to change the state of the track blocks in its default state. To begin changing track states, such as light states, switch states, crossing states, or boolean authority, check the "Maintenance Mode" checkbox to enable Maintenance Mode. Once Maintenance Mode is enabled, the PLC program will be disabled and the user will be able to change the state of the track blocks. To change the state of a track block, click the corresponding button to change the state of the track block. The Wayside Controller GUI will automatically update to display the new state of the track block. To change the state of a different track block, click the dropdown menu and select the desired track block (e.g. click the checkbox for Authority to toggle boolean authority for that block, or change the light switch to toggle the light state for that block). The Wayside Controller GUI will automatically update to display the new state of the selected track block. As long as Maintenance Mode is enabled, the user will be able to change the state of the track blocks. To disable Maintenance Mode, uncheck the "Maintenance Mode" checkbox. Once Maintenance Mode is disabled, the PLC program will be enabled and the user will not be able to change the state of the track blocks.

#### <i>Hardware Wayside Controller</i>

### Track Model

### Train Model
To access the Train Model GUI, first run the main program. Then, left-click the "trainModel" button. The Train Model GUI will open. The Train Model GUI displays the current state of whichever train is selected, including the current speed, authority, and power. The Train Model GUI also displays the current state of the doors, lights, and brakes. If no train has been dispatched, the GUI will be filled with empty values until a train is dispatched.  Upon initial dispatch, the GUI will automatically update to display the values of the dispatched train.

To switch between different train models on a track, click the "Train No." dropdown menu and select the desired train. The Train Model GUI will automatically update to display the values of the selected train. The Train Model GUI will display the values of the selected train until a new train is selected.

To enable a Brake Failure, Power Failure, or Signal Pickup Failure, click the corresponding button. To disable a failure state, click the corresponding button again. The failure states are disabled by default. The failure states can be enabled and disabled at any time, even while the simulation is running. The failure states can be enabled and disabled in any combination and order.

To enable the Emergency Brake, click the Passenger Emergency Brake Button. The Emergency Brake cannot be disabled from the Train Model's GUI. The Emergency Brake can be enabled at any time, even while the simulation is running. The Emergency Brake can be enabled even in the case of brake failue.  The Emergency Brake cuts power to the engine and provides a negative acceleration to the train.

### Software Train Controller

### Hardware Train Controller
