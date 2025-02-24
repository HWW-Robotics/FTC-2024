package org.firstinspires.ftc.teamcode.action;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;


public class RoadRunnerPath implements ActionStage {
    private final SampleMecanumDrive driver;
    private final TrajectorySequence path;

    public RoadRunnerPath(SampleMecanumDrive driver, TrajectorySequence path) {
        this.driver = driver;
        this.path = path;
    }

    public RoadRunnerPath(SampleMecanumDrive driver, Pose2d start, Pose2d target) {
        this(driver, driver.trajectorySequenceBuilder(start).lineToLinearHeading(target).build());
    }

    public TrajectorySequence getPath() {
        return this.path;
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
