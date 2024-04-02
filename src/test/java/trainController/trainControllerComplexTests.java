//package trainController;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.stage.Stage;
//import org.junit.jupiter.api.Test;
//import org.testfx.framework.junit5.ApplicationTest;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.testfx.api.FxAssert.verifyThat;
//
//public class trainControllerComplexTests extends ApplicationTest{
//    TrainControllerImpl controller;
//    TrainControllerSubject currentSubject;
//
//
//    @Override
//    public void start(Stage stage) throws Exception{
//    // Assuming trainControllerSubjectMap is a singleton class managing subjects
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Framework/GUI/FXML/trainController.fxml"));
//        Scene scene = new Scene(loader.load());
//        stage.setScene(scene);
//        stage.show();
//        stage.toFront();
//
//        currentSubject = TrainControllerSubjectMap.getInstance().getSubject(1);
//        controller = TrainControllerSubjectMap.getInstance().getSubject(1).getController();
//    }
//
//    // Train's inTunnel Status is on:
//    // Int/Ext Lights should turn on
//    // OPTIONAL: Thoughts on the train slowing down
////    @Test public void testInsideTunnel(){
////        // Get the inTunnelStatus indicator
////        Circle inTunnelStatus = lookup("#inTunnelStatus").queryAs(Circle.class);
////
////
////        // Get the initial state of the inTunnelStatus, intLights, and extLights
////        Color initialTunnelStatusColor = (Color) inTunnelStatus.getFill();
////        boolean initialIntLightsState = controller.getIntLights();
////        boolean initialExtLightsState = controller.getExtLights();
////
////        // Verify that train controller in tunnel status is gray outside of the tunnel
////        verifyThat(inTunnelStatus, (Circle circle) -> circle.getFill().equals(initialTunnelStatusColor));
////        System.out.println("Int Light: " + controller.getIntLights());
////        // Verify that the intLights and extLights are off
////        //assertFalse(controller.getIntLights());
////        //assertFalse(controller.getExtLights());
////
////        // Simulate the train entering the tunnel
////        controller.setInTunnel(true);
////        System.out.println("Int Light: " + controller.getIntLights());
////        // Verify that the inTunnelStatus indicator turns yellow
////        verifyThat(inTunnelStatus, (Circle circle) -> circle.getFill().equals(Color.YELLOW));
////
////        // Verify that the intLights and extLights turn on
////        assertTrue(controller.getIntLights());
////        assertTrue(controller.getExtLights());
////
////        // OPTIONAL: Verify that the train slows down (if applicable)
////        // You can check the train's speed or any related properties here
////
////        // Simulate the train exiting the tunnel
////        controller.setInTunnel(false);
////
////        // Verify that the inTunnelStatus indicator turns back to the initial color
////        verifyThat(inTunnelStatus, (Circle circle) -> circle.getFill().equals(initialTunnelStatusColor));
////
////        // Verify that the intLights and extLights return to their initial states
////        assertEquals(initialIntLightsState, controller.getIntLights());
////        assertEquals(initialExtLightsState, controller.getExtLights());
////    }
//
//    // Set the train's position to a block or two before
//    // See if inTunnel status turns on
//    // Check the other tunnel info
//    @Test public void testEnteringATunnel(){
//
//    }
//}
