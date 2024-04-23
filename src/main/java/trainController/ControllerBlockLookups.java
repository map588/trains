package trainController;

import Utilities.HelperObjects.BasicTrackMap;
import Utilities.Enums.Lines;
import Utilities.BasicBlockParser;
import Utilities.Records.BasicBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.ControllerBlocks.ControllerBlock;

import java.util.concurrent.ConcurrentHashMap;

final class ControllerBlockLookups {
    private static final Logger logger = LoggerFactory.getLogger(ControllerBlockLookups.class);

    private static ConcurrentHashMap<Lines, ConcurrentHashMap<Integer, ControllerBlock>> lineLookups = new ConcurrentHashMap<>();


    private ControllerBlockLookups() {
        BasicTrackMap lineMaps = BasicBlockParser.getInstance().getAllBasicLines();
        for(Lines line : lineMaps.keySet()){
            ConcurrentHashMap<Integer, ControllerBlock> lookup = new ConcurrentHashMap<>();
            for(Integer blockID : lineMaps.get(line).keySet()){
                BasicBlock basicBlock = lineMaps.get(line).get(blockID);
                ControllerBlock controllerBlock = new ControllerBlock(basicBlock);
                lookup.put(blockID, controllerBlock);
            }
            lineLookups.put(line, lookup);
            logger.info("ControllerLookup: " + line + " lookup created");
        }
    }

    public static ControllerBlockLookups INSTANCE = new ControllerBlockLookups();


    public static ConcurrentHashMap<Integer, ControllerBlock> getLookup(Lines line) {
        return lineLookups.get(line);
    }
}