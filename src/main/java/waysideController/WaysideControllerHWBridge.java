package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WaysideControllerHWBridge extends WaysideControllerImpl {

    private final SerialPort serialPort;

    public WaysideControllerHWBridge(int id, String COMPort) {
        super(id);
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
    public void setManualMode(boolean manualMode) {
        super.setManualMode(manualMode);
        try {
            serialPort.getOutputStream().write(("manualMode="+manualMode).getBytes());
        } catch (IOException e) {
            System.out.println("Failed to write manualMode");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addBlock(BlockInfo block) {
        super.addBlock(block);
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
