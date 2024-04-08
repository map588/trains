package Utilities;

import org.slf4j.Logger;
import trackModel.TrackLine;
import trainController.TrainControllerImpl;
import trainModel.TrainModelImpl;

public class KTuner {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(KTuner.class);
    private static final double TIME_STEP = (double)Constants.TIME_STEP_MS / 1000.0;

    private final TrainModelImpl train;
    private final TrainControllerImpl controller;
    private double Ki;
    private double Kp;
    private double setSpeed;

    public KTuner(double setSpeed, int iterations) {
        this.train = new TrainModelImpl(new TrackLine(), 0);
        this.controller = new TrainControllerImpl(train, train.getTrainNumber());
        this.Ki = 8;
        this.Kp = 12;
        this.setSpeed = setSpeed;
        tuneGains(iterations);
    }

    public void tuneGains(int iterations) {
        double bestKi = Ki;
        double bestKp = Kp;
        double bestScore = Double.MAX_VALUE;

        double prevScore = bestScore;

        for (int i = 0; i < iterations; i++) {
            double ki = bestKi;
            double kp = bestKp;

            double score = simulateSystem(ki, kp);

            double deltaPrevScore = prevScore - score;
            prevScore = score;

            if (score < bestScore) {
                bestKi = ki;
                bestKp = kp;
                bestScore = score;
            }

            // Update Ki and Kp based on the change in error
            double kiDelta = 20 * deltaPrevScore;
            double kpDelta = 30 * deltaPrevScore;

            ki += kiDelta;
            kp += kpDelta;
        }

        controller.setKi(bestKi);
        controller.setKp(bestKp);

        logger.info("Tuning completed.");
        logger.info("Best score: {}", String.format("%.12f", bestScore));
        logger.info("Best Ki: {}", String.format("%.6f", bestKi));
        logger.info("Best Kp: {}", String.format("%.6f", bestKp));
    }

    private double simulateSystem(double ki, double kp) {
        double prevKi = this.Ki;
        double prevKp = this.Kp;
        double currentSpeed = train.getSpeed();

        setKi(ki);
        setKp(kp);

        double duration = 10.0;
        double overshoot = 0.0;
        double settlingTime = 0.0;
        double time = 0.0;
        boolean settled = false;

        while (time < duration) {
            double power = controller.calculatePower(currentSpeed);
            train.setPower(power);
            train.trainModelPhysics();
            currentSpeed = train.getSpeed();

            double error = setSpeed - currentSpeed;

            if (!settled && Math.abs(error) <= 0.0001) {
                settlingTime = time;
                settled = true;
            }

            if (currentSpeed > setSpeed) {
                overshoot = Math.max(overshoot, currentSpeed - setSpeed);
            }

            time += TIME_STEP;
        }

        setKi(prevKi);
        setKp(prevKp);

        double score = overshoot + settlingTime;
        return score;
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