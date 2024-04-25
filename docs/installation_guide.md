# Installation Guide
This installation guide is a document meant to help guide users through the installation of the J.A.M.E.S. system. This guide will cover both installation of software and optional hardware components.

## Table of Contents
1. [Installation of Main Executable](installation_guide.md#installation-of-main-executable)
2. [Hardware Wayside Controller](installation_guide.md#hardware-wayside-controller)
3. [Hardware Train Controller](installation_guide.md#hardware-train-controller)

## Installation of Main Executable

Download JRE v17 from Oracle: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

Run the Java JRE v17 installer and follow the instructions to install the JRE.

Download the Executable installer from the release tab from Github.

Launch the installer and follow the onscreen instructions 

## Hardware Wayside Controller
The Hardware Wayside Controller requires a Raspberry Pi 4B with a SparkFun FTDI 3.3V Serial USB Adapter. In addition, a 5V 3A USB power adapter and mini USB-B to USB-A cable are required to power the Raspberry Pi.

Start by cloning the repository onto the Raspberry Pi. Then ensure the PI has Java 17 installed, as well as Gradle version 8.4. Open a terminal in the repository location, cd into the "trains" folder, and run the following command:
```bash
gradle shadowJar -PmainClass=waysideController.WaysideControllerHW
```

This will create a jar file in the "build/libs" folder called "trains.jar". Copy this jar file to the trains directory, then run the program with the following command:
```bash
java -jar trains.jar
```

## Hardware Train Controller