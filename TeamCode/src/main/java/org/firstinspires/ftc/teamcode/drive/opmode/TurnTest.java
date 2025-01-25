package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

/*
 * This is a simple routine to test turning capabilities.
 */
@Config
@Autonomous(group = "drive")
public class TurnTest extends LinearOpMode {
    public static double RADIUS = 15; // in

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(0, -RADIUS, Math.toRadians(0));

        TrajectorySequence seq = drive.trajectorySequenceBuilder(startPose)
                .splineToSplineHeading(new Pose2d(RADIUS, 0, Math.toRadians(90)), Math.toRadians(90))
                .splineToSplineHeading(new Pose2d(0, RADIUS, Math.toRadians(180)), Math.toRadians(180))
                .splineToSplineHeading(new Pose2d(-RADIUS, 0, Math.toRadians(270)), Math.toRadians(270))
                .splineToSplineHeading(startPose, Math.toRadians(0))
                .build();

        drive.setPoseEstimate(startPose);
        this.telemetry.addData("pose:", drive.getPoseEstimate());

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive() && !isStopRequested()) {
            drive.followTrajectorySequence(seq);
        }
    }
}
