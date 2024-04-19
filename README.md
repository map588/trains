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

### Software Wayside Controller

### Hardware Wayside Controller

### Track Model

### Train Model
To access the Train Model GUI, first run the main program. Then, left-click the "trainModel" button. The Train Model GUI will open. The Train Model GUI displays the current state of whichever train is selected, including the current speed, authority, and power. The Train Model GUI also displays the current state of the doors, lights, and brakes. If no train has been dispatched, the GUI will be filled with empty values until a train is dispatched.  Upon initial dispatch, the GUI will automatically update to display the values of the dispatched train.

To switch between different train models on a track, click the "Train No." dropdown menu and select the desired train. The Train Model GUI will automatically update to display the values of the selected train. The Train Model GUI will display the values of the selected train until a new train is selected.

To enable a Brake Failure, Power Failure, or Signal Pickup Failure, click the corresponding button. To disable a failure state, click the corresponding button again. The failure states are disabled by default. The failure states can be enabled and disabled at any time, even while the simulation is running. The failure states can be enabled and disabled in any combination and order.

To enable the Emergency Brake, click the Passenger Emergency Brake Button. The Emergency Brake cannot be disabled from the Train Model's GUI. The Emergency Brake can be enabled at any time, even while the simulation is running. The Emergency Brake can be enabled even in the case of brake failue.  The Emergency Brake cuts power to the engine and provides a negative acceleration to the train.

### Software Train Controller

### Hardware Train Controller
