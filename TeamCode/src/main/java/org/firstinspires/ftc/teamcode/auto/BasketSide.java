package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Configurations;
import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;

@Autonomous(name = "BasketSide")
public class BasketSide extends OpMode {

    SampleMecanumDrive driver;
    ClawSlide clawSlide;
    TrajectorySequence sequence;

    @Override
    public void init() {
        GlobalStorage.onInit(this);
        this.driver = GlobalStorage.getOrCreateDriver(hardwareMap);
        this.driver.setPoseEstimate(new Pose2d(0, 0, 0));
        this.clawSlide = new ClawSlide(
            hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_ROT),
            hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_ROT),
            hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_LIFT),
            hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_LIFT),
            hardwareMap.get(Servo.class, Configurations.LEFT_CLAW_ROT),
            hardwareMap.get(Servo.class, Configurations.RIGHT_CLAW_ROT),
            hardwareMap.get(Servo.class, Configurations.LEFT_CLAW_ARM),
            hardwareMap.get(Servo.class, Configurations.RIGHT_CLAW_ARM)
        );

        this.telemetry.addLine("Building Sequence");
        this.telemetry.update();

        TrajectorySequenceBuilder builder = driver.trajectorySequenceBuilder(new Pose2d(0, 0, 0));
        builder
            .lineToLinearHeading(new Pose2d(6,13, Math.toRadians(0)));
        this.addPutSequence(builder);

        builder
            .lineToLinearHeading(new Pose2d(14,15.08, Math.toRadians(0)));
        this.addPickAndPutSequence(builder);

        builder
            .lineToLinearHeading(new Pose2d(14, 25.66, Math.toRadians(0)));
        this.addPickAndPutSequence(builder);

        // builder
        //     .lineToLinearHeading(new Pose2d(3.57, 23.08, Math.toRadians(22.7)))
        //     .lineToLinearHeading(new Pose2d(17.45, 27.07, Math.toRadians(23.5)));
        // this.addPickAndPutSequence(builder);

        // builder.lineToLinearHeading(new Pose2d(3.0, 23.0, Math.toRadians(0)));

        this.sequence = builder.build();

        this.clawSlide.claw.closeAll();
        this.telemetry.addLine("Initialized");
        this.telemetry.update();
    }

    @Override
    public void init_loop() {
        this.driver.getLocalizer().update();
        this.telemetry.addLine("Initialized");
        Pose2d pos = this.driver.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));
        this.telemetry.update();
    }

    @Override
    public void start() {
        this.driver.followTrajectorySequenceAsync(this.sequence);
        this.telemetry.addLine("Started");
        this.telemetry.update();
    }

    @Override
    public void loop() {
        this.driver.update();
        this.clawSlide.update();

        this.telemetry.addData("isBusy", this.driver.isBusy());
        this.telemetry.addData("inAction", this.clawSlide.inAction());

        Pose2d pos = this.driver.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));

        this.telemetry.addData("Lift Diff", this.clawSlide.slideLift.left.getCurrentPosition() - this.clawSlide.slideLift.right.getCurrentPosition());
        this.telemetry.update();
    }

    private void addPickAndPutSequence(TrajectorySequenceBuilder builder) {
        // begin pick
        builder
            .addTemporalMarker(clawSlide.claw::openAll)
            .addTemporalMarker(clawSlide::putDown)
            .waitSeconds(1.9)
            .addTemporalMarker(clawSlide.claw::closeAll)
            .waitSeconds(0.3)
            .addTemporalMarker(clawSlide::retractAndPullUp)
            .waitSeconds(1.8);
        this.addPutSequence(builder);
    }

    private void addPutSequence(TrajectorySequenceBuilder builder) {
        // after pick
        builder
            // .lineToLinearHeading(new Pose2d(5.83, 16.17, Math.toRadians(130)))
            .addTemporalMarker(() -> this.clawSlide.claw.setRotate(30))
            .lineToLinearHeading(new Pose2d(3.27, 24.9, Math.toRadians(130)))
            .addTemporalMarker(() -> this.clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION))
            .waitSeconds(1.4)
            .addTemporalMarker(() -> this.clawSlide.claw.setRotate(130))
            .waitSeconds(0.2)
            .addTemporalMarker(this.clawSlide.claw::openAll)
            .waitSeconds(0.2)
            .addTemporalMarker(() -> this.clawSlide.claw.setRotate(30))
            .waitSeconds(0.1)
            .addTemporalMarker(() -> this.clawSlide.slideLift.setPosition(0))
            .waitSeconds(1.0)
            .lineToLinearHeading(new Pose2d(6.68, 20.73, Math.toRadians(130)));
    }
}
