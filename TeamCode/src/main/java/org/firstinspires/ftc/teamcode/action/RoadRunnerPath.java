package org.firstinspires.ftc.teamcode.action;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;


public class RoadRunnerPath implements ActionStage {
    private final SampleMecanumDrive driver;
    private final TrajectorySequence path;

    public RoadRunnerPath(SampleMecanumDrive driver, TrajectorySequence path) {
        this.driver = driver;
        this.path = path;
    }

    @Override
    public boolean isDone() {
        return !this.driver.isBusy();
    }

    @Override
    public void begin() {
        this.driver.followTrajectorySequenceAsync(path);
    }

    @Override
    public void after() {
        this.driver.followTrajectorySequenceAsync(null);
    }

    @Override
    public void update() {
        // this.driver.update();
    }
}
