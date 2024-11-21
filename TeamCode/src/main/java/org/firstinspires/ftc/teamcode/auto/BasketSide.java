package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
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

        TrajectorySequence sequence = drive.trajectorySequenceBuilder(new Pose2d())
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(90);
                })
                .forward(3)
                .strafeLeft(10)
                .turn(Math.toRadians(100))
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(1500);
                })
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(180);
                })
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    clawSlide.claw.openAll();
                })
                .build();

        this.waitForStart();

        if (this.isStopRequested()) {
            return;
        }
        drive.followTrajectorySequence(sequence);
    }
}
