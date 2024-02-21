package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

public class WaysideControllerHWBridge extends WaysideControllerImpl {

    private final SerialPort serialPort;

    public WaysideControllerHWBridge(int id, int trackLine, String COMPort) {
        super(id, trackLine);
        SerialPortBuilder builder = SerialPortBuilder.newBuilder(COMPort);
        builder.setBaudRate(19200);
        builder.setParity(Parity.EVEN);

        try {
            serialPort = builder.build();
        } catch (IOException e) {
            System.out.println("Failed to build serial port at: " + COMPort);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadPLC(File PLC) {
        super.loadPLC(PLC);
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        super.setMaintenanceMode(maintenanceMode);
        try {
            System.out.println("Send: maintenanceMode="+maintenanceMode);
            serialPort.getOutputStream().write(("maintenanceMode="+maintenanceMode).getBytes());
        } catch (IOException e) {
            System.out.println("Failed to write maintenanceMode");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addBlock(WaysideBlockInfo block) {
        super.addBlock(block);
    }

    private void parseCOMMessage(String message) {
        String[] values = message.split("=", 2);

        switch(values[0]) {
            case "maintenanceMode":
                super.setMaintenanceMode(Boolean.parseBoolean(values[1]));
        }
    }

    @Override
    protected void finalize() {
        try {
            serialPort.close();
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
    }
}
