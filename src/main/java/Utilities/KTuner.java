package Utilities;

import org.slf4j.Logger;
import trainController.TrainControllerImpl;
import trainModel.TrainModelImpl;

public class KTuner {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(KTuner.class);
    private static final double TIME_STEP = Constants.TIME_STEP_MS / 1000.0;

    private final TrainModelImpl train;
    private final TrainControllerImpl controller;
    private double Ki;
    private double Kp;
    private double setSpeed;


    public KTuner(double setSpeed, int iterations) {
        this.train = new TrainModelImpl();
        this.controller = new TrainControllerImpl(train,train.getTrainNumber());
        this.Ki = controller.getKi();
        this.Kp = controller.getKp();
        this.setSpeed = setSpeed;
        tuneGains(iterations);
        logger.info("Tuned K values after {} iterations: Ki = {}, Kp = {}", iterations, controller.getKi(), controller.getKp());
    }


    public void tuneGains(int iterations) {
        // Set the initial values for Ki and Kp
        double initialKi = 0.0;
        double initialKp = 0.0;

        double kiStep = 0.05;
        double kpStep = 0.1;

        // Set the initial scaling factors for Ki and Kp adjustments
        double kiScale = 0.1;
        double kpScale = 0.2;

        // Set the adjustment factors for scaling factors
        double kiScaleAdjustment = 0.01;
        double kpScaleAdjustment = 0.02;

        // Initialize the rolling error and current error
        double rollingError = 0.0;
        double currentError = 0.0;

        // Perform the tuning iterations
        for (int i = 0; i < iterations; i++) {
            // Simulate the system with the current gains
            double[] errors = simulateSystem(initialKi, initialKp);
            rollingError += errors[0];
            currentError = errors[1];

            // Adjust the scaling factors based on the errors
            if (Math.abs(rollingError) > 1.0) {
                kiScale += kiScaleAdjustment;
            } else {
                kiScale -= kiScaleAdjustment;
            }

            if (Math.abs(currentError) > 1.0) {
                kpScale += kpScaleAdjustment;
            } else {
                kpScale -= kpScaleAdjustment;
            }

            // Ensure the scaling factors remain within reasonable ranges
            kiScale = Math.max(0.01, Math.min(1.0, kiScale));
            kpScale = Math.max(0.01, Math.min(1.0, kpScale));

            // Adjust Ki based on the rolling error
            double kiAdjustment = kiScale * rollingError;
            initialKi += kiAdjustment * kiStep;

            // Adjust Kp based on the current error
            double kpAdjustment = kpScale * currentError;
            initialKp += kpAdjustment * kpStep;
        }

        // Set the optimized gains
        controller.setKi(initialKi);
        controller.setKp(initialKp);
    }

    private double[] simulateSystem(double ki, double kp) {
        // Save the current values of Ki and Kp
        double prevKi = this.Ki;
        double prevKp = this.Kp;
        double currentSpeed = train.getSpeed();

        // Set the temporary values of Ki and Kp for simulation
        setKi(ki);
        setKp(kp);

        // Run the simulation for a certain duration
        double duration = 10.0; // Adjust the duration as needed
        double rollingError = 0.0;
        double currentError = 0.0;
        double time = 0.0;
        while (time < duration) {
            // Calculate the power and update the train model
            double power = controller.calculatePower(currentSpeed);
            train.setPower(power);
            train.trainModelPhysics();

            // Calculate the current error
            currentError = setSpeed - train.getSpeed();

            // Accumulate the rolling error
            rollingError += currentError * TIME_STEP;

            time += TIME_STEP;
        }

        // Restore the previous values of Ki and Kp
        setKi(prevKi);
        setKp(prevKp);

        // Return the rolling error and current error
        return new double[]{rollingError, currentError};
    }

    private void setKi(double ki) {
        this.Ki = ki;
        controller.setKi(ki);
    }

    private void setKp(double kp) {
        this.Kp = kp;
        controller.setKp(kp);
    }
}
