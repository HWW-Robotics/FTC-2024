package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous(name = "BasketSide")
public class BasketSide extends OpMode {

    SampleMecanumDrive drive;
    ClawSlide clawSlide;
    TrajectorySequence sequence;

    @Override
    public void init() {
        this.drive = new SampleMecanumDrive(hardwareMap);
        this.clawSlide = new ClawSlide(
            hardwareMap.get(DcMotor.class, "leftRotation"),
            hardwareMap.get(DcMotor.class, "rightRotation"),
            hardwareMap.get(DcMotor.class, "leftSlide"),
            hardwareMap.get(DcMotor.class, "rightSlide"),
            hardwareMap.get(Servo.class, "ClawRotLeft"),
            hardwareMap.get(Servo.class, "ClawRotRight"),
            hardwareMap.get(Servo.class, "ClawArmLeft"),
            hardwareMap.get(Servo.class, "ClawArmRight")
        );

        this.telemetry.addLine("Building Sequence");
        this.telemetry.update();
        this.sequence = drive.trajectorySequenceBuilder(new Pose2d(0, 0, 0))
            .addTemporalMarker(clawSlide.claw::openAll)
            .lineTo(new Vector2d(14.24,-16.79))
            // begin pick
            .addTemporalMarker(clawSlide::putDown)
            .waitSeconds(4)
            .addTemporalMarker(clawSlide.claw::closeAll)
            .waitSeconds(2)
            .addTemporalMarker(clawSlide::retractAndPullUp)
            .waitSeconds(4)
            // after pick
            .lineTo(new Vector2d(14.0, -16.79))
            .turn(Math.toRadians(138.3))
            .addTemporalMarker(() -> {
                clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
            })
            .waitSeconds(5)
            .strafeTo(new Vector2d(1.9, -26.62))
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
            //
            // .lineToLinearHeading(new Pose2d(-1.4, -26.62, Math.toRadians(2.32)))
            // .splineTo(new Vector2d(-12.94, -26.1), 0)
            // // begin pick
            // .addTemporalMarker(clawSlide::putDown)
            // .waitSeconds(5)
            // .addTemporalMarker(clawSlide.claw::closeAll)
            // .waitSeconds(3)
            // .addTemporalMarker(clawSlide::retractAndPullUp)
            // .waitSeconds(5)
            // // after pick
            // .lineToLinearHeading(new Pose2d(-14.24, -16.79, Math.toRadians(138.3)))
            // .addTemporalMarker(() -> {
            //     clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
            // })
            // .waitSeconds(5)
            // .strafeTo(new Vector2d(-1.9, -26.62))
            // .addTemporalMarker(() -> {
            //     clawSlide.claw.setRotate(195);
            // })
            // .waitSeconds(3)
            // .addTemporalMarker(clawSlide.claw::openAll)
            // .waitSeconds(2)
            // .addTemporalMarker(() -> {
            //     clawSlide.claw.setRotate(0);
            // })
            // .waitSeconds(2)
            // .addTemporalMarker(() -> {
            //     clawSlide.slideLift.setPosition(0);
            // })
            // .waitSeconds(3)
            //
            // .lineToLinearHeading(new Pose2d(-3.57, -23.08, Math.toRadians(22.7)))
            // .lineToLinearHeading(new Pose2d(-17.45, -27.07, Math.toRadians(23.5)))
            // // begin pick
            // .addTemporalMarker(clawSlide::putDown)
            // .waitSeconds(5)
            // .addTemporalMarker(clawSlide.claw::closeAll)
            // .waitSeconds(3)
            // .addTemporalMarker(clawSlide::retractAndPullUp)
            // .waitSeconds(5)
            // // after pick
            // .lineToLinearHeading(new Pose2d(-14.24, -16.79, Math.toRadians(138.3)))
            // .addTemporalMarker(() -> {
            //     clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
            // })
            // .waitSeconds(5)
            // .strafeTo(new Vector2d(-1.9, -26.62))
            // .addTemporalMarker(() -> {
            //     clawSlide.claw.setRotate(195);
            // })
            // .waitSeconds(3)
            // .addTemporalMarker(clawSlide.claw::openAll)
            // .waitSeconds(2)
            // .addTemporalMarker(() -> {
            //     clawSlide.claw.setRotate(0);
            // })
            // .waitSeconds(2)
            // .addTemporalMarker(() -> {
            //     clawSlide.slideLift.setPosition(0);
            // })
            // .waitSeconds(3)
            .build();

        this.clawSlide.claw.closeAll();
        this.telemetry.addLine("Inited");
        this.telemetry.update();
    }

    @Override
    public void start() {
        this.drive.followTrajectorySequenceAsync(this.sequence);
        this.telemetry.addLine("Started");
        this.telemetry.update();
    }

    @Override
    public void loop() {
        this.drive.update();
        this.clawSlide.update();
        if (!this.drive.isBusy()) {
            //
        }

        Pose2d pos = this.drive.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));
        this.telemetry.update();
    }
}
