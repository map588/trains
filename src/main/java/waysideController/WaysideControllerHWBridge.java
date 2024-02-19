package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;

import java.io.File;
import java.util.List;

public class WaysideControllerHWBridge extends WaysideControllerImpl {

    private final Object serialPort;

    public WaysideControllerHWBridge(int id, String COMPort) {
        super(id);
        this.serialPort = COMPort;
    }

    @Override
    public void loadPLC(File PLC) {
        super.loadPLC(PLC);
    }

    @Override
    public void setManualMode(boolean manualMode) {
        super.setManualMode(manualMode);
    }

    @Override
    public void addBlock(BlockInfo block) {
        super.addBlock(block);
    }
}
