package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous(name = "BasketSide")
public class BasketSide extends LinearOpMode {

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
        clawSlide.claw.closeAll();

        // -3.57, -23.08, 22.7;
        // ; pick;

        TrajectorySequence sequence = drive.trajectorySequenceBuilder(new Pose2d())
                .addTemporalMarker(clawSlide.claw::openAll)
                .strafeTo(new Vector2d(-10.35, -16.79))
                .strafeTo(new Vector2d(-14.24, -16.79))
                // begin pick
                .addTemporalMarker(clawSlide::putDown)
                .waitSeconds(5)
                .addTemporalMarker(clawSlide.claw::closeAll)
                .waitSeconds(3)
                .addTemporalMarker(clawSlide::retractAndPullUp)
                .waitSeconds(5)
                // after pick
                .lineToLinearHeading(new Pose2d(-14.0, -16.79, Math.toRadians(138.3)))
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
                })
                .waitSeconds(5)
                .strafeTo(new Vector2d(-1.9, -26.62))
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(195);
                })
                .waitSeconds(3)
                .addTemporalMarker(clawSlide.claw::openAll)
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(0);
                })
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(0);
                })
                .waitSeconds(3)

                .splineToSplineHeading(new Pose2d(-1.4, -26.62, Math.toRadians(2.32)), 0)
                .splineTo(new Vector2d(-12.94, -26.1), 0)
                // begin pick
                .addTemporalMarker(clawSlide::putDown)
                .waitSeconds(5)
                .addTemporalMarker(clawSlide.claw::closeAll)
                .waitSeconds(3)
                .addTemporalMarker(clawSlide::retractAndPullUp)
                .waitSeconds(5)
                // after pick
                .lineToLinearHeading(new Pose2d(-14.24, -16.79, Math.toRadians(138.3)))
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
                })
                .waitSeconds(5)
                .strafeTo(new Vector2d(-1.9, -26.62))
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(195);
                })
                .waitSeconds(3)
                .addTemporalMarker(clawSlide.claw::openAll)
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(0);
                })
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(0);
                })
                .waitSeconds(3)

                .splineToSplineHeading(new Pose2d(-3.57, -23.08, Math.toRadians(22.7)), 0)
                .splineToSplineHeading(new Pose2d(-17.45, -27.07, Math.toRadians(23.5)), 0)
                // begin pick
                .addTemporalMarker(clawSlide::putDown)
                .waitSeconds(5)
                .addTemporalMarker(clawSlide.claw::closeAll)
                .waitSeconds(3)
                .addTemporalMarker(clawSlide::retractAndPullUp)
                .waitSeconds(5)
                // after pick
                .lineToLinearHeading(new Pose2d(-14.24, -16.79, Math.toRadians(138.3)))
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
                })
                .waitSeconds(5)
                .strafeTo(new Vector2d(-1.9, -26.62))
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(195);
                })
                .waitSeconds(3)
                .addTemporalMarker(clawSlide.claw::openAll)
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(0);
                })
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(0);
                })
                .waitSeconds(3)
                .build();

        this.waitForStart();

        if (this.isStopRequested()) {
            return;
        }
        drive.followTrajectorySequence(sequence);
    }
}
