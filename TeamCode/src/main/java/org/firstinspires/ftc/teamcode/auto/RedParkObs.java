package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous(name = "RedParkObs")
public class RedParkObs extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        final SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        final ClawSlide clawSlide = new ClawSlide(
            hardwareMap.get(DcMotor.class, "leftRotation"),
            hardwareMap.get(DcMotor.class, "rightRotation"),
            hardwareMap.get(DcMotor.class, "leftSlide"),
            hardwareMap.get(DcMotor.class, "rightSlide"),
            hardwareMap.get(Servo.class, "ClawRotLeft"),
            hardwareMap.get(Servo.class, "ClawRotRight"),
            hardwareMap.get(Servo.class, "ClawArmLeft"),
            hardwareMap.get(Servo.class, "ClawArmRight")
        );
        TrajectorySequence sequence = drive.trajectorySequenceBuilder(new Pose2d(0, 0))
                .addTemporalMarker(() -> {
                    clawSlide.claw.closeAll();
                    clawSlide.claw.setRotate(90);
                })
//                .lineToSplineHeading(new Pose2d(26,22, Math.toRadians(180)))
//                .addTemporalMarker(() -> {
//                    subsystem.clawRotatePos(subsystem.clawRotateDownPos);
//                })
//                .waitSeconds(0.5)
//                .addTemporalMarker(() -> {
//                    subsystem.armPos(subsystem.armDownPos);
//                })
//                .waitSeconds(0.5)
//                .addTemporalMarker(() -> {
//                    subsystem.leftClawOpen();
//                })
//                .waitSeconds(1)
//                .addTemporalMarker(() -> { //following purple pixel placement
//                    subsystem.armPos(subsystem.armUpPos);
//                    subsystem.clawRotatePos(subsystem.clawRotateUpPos);
//                    subsystem.leftClawClosed();
//                })
//                .waitSeconds(1)
//                .lineToSplineHeading(new Pose2d(48,35, Math.toRadians(180)))
//                .addTemporalMarker(() -> { //setting up arm to score
//                    subsystem.armPos(subsystem.armPlacePos);
//                    subsystem.clawRotatePos(subsystem.clawRotatePlacePos);
//                })
//                .waitSeconds(0.5)
//                .addTemporalMarker(() -> { //running slides up
//                    subsystem.slidePos(subsystem.slideMax/2);
//                })
//                .waitSeconds(3)
//                .addTemporalMarker(() -> {
//                    subsystem.rightClawOpen(); //placing yellow pixel
//                })
//                .waitSeconds(0.5)
//                .addTemporalMarker(() -> {
//                    subsystem.slidePos(0); //bringing slides down
//                })
//                .lineToSplineHeading(new Pose2d(48,58, Math.toRadians(180)))
                .build();

        this.waitForStart();

        if (!this.isStopRequested()) {
            drive.followTrajectorySequence(sequence);
        }
    }
}
