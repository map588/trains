package trackModel;

import static Utilities.Enums.Direction.NORTH;
import static Utilities.Enums.Direction.SOUTH;

public class TrackBlogLog {

    public TrackBlogLog(TrackBlock block) {
        System.out.println("Block: " + block.getBlockID() + " Line: " + block.getLine());
        System.out.println("Type: " + block.getBlockType() + " Length: " + block.getLength());
        System.out.println("Next Block North: " + block.getNextBlock(NORTH));
        System.out.println("Next Block South: " + block.getNextBlock(SOUTH));
    }
}
