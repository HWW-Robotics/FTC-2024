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
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;

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

        TrajectorySequenceBuilder builder = drive.trajectorySequenceBuilder(new Pose2d(0, 0, 0));
        this.addPutSequence(builder);
        builder
            .lineToLinearHeading(new Pose2d(4.24,16.79, Math.toRadians(0)))
            .lineToLinearHeading(new Pose2d(14.24,16.79, Math.toRadians(0)));
        this.addPickAndPutSequence(builder);

        builder
            .lineToLinearHeading(new Pose2d(1.4, 26.62, Math.toRadians(2.3)))
            .lineToLinearHeading(new Pose2d(12.94, 26.1, Math.toRadians(2.32)));
        this.addPickAndPutSequence(builder);

        // builder
        //     .lineToLinearHeading(new Pose2d(3.57, 23.08, Math.toRadians(22.7)))
        //     .lineToLinearHeading(new Pose2d(17.45, 27.07, Math.toRadians(23.5)));
        // this.addPickAndPutSequence(builder);

        builder.lineToLinearHeading(new Pose2d(3.0, 23.0, Math.toRadians(0)));

        this.sequence = builder.build();

        this.clawSlide.claw.closeAll();
        this.telemetry.addLine("Initialized");
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

        this.telemetry.addData("isBusy", this.drive.isBusy());
        this.telemetry.addData("inAction", this.clawSlide.inAction());

        Pose2d pos = this.drive.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));
        this.telemetry.update();
    }

    private void addPickAndPutSequence(TrajectorySequenceBuilder builder) {
        // begin pick
        builder
            .addTemporalMarker(clawSlide::putDown)
            .waitSeconds(2.5)
            .addTemporalMarker(clawSlide.claw::closeAll)
            .waitSeconds(0.8)
            .addTemporalMarker(clawSlide::retractAndPullUp)
            .waitSeconds(2.5);
        this.addPutSequence(builder);
    }

    private void addPutSequence(TrajectorySequenceBuilder builder) {
        // after pick
        builder
            .lineToLinearHeading(new Pose2d(14.0, 16.79, Math.toRadians(138.3)))
            .addTemporalMarker(() -> this.clawSlide.claw.setRotate(105))
            .addTemporalMarker(() -> this.clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION))
            .waitSeconds(1.5)
            .lineTo(new Vector2d(1.9, 26.62))
            .addTemporalMarker(this.clawSlide.claw::openAll)
            .waitSeconds(0.3)
            .addTemporalMarker(() -> this.clawSlide.claw.setRotate(20))
            .waitSeconds(0.2)
            .addTemporalMarker(() -> this.clawSlide.slideLift.setPosition(0))
            .waitSeconds(1.0);
    }
}
