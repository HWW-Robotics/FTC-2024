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

@Autonomous(name = "ObserveSide")
public class ObserveSide extends OpMode {

    SampleMecanumDrive driver;
    ClawSlide clawSlide;
    TrajectorySequence sequence;

    @Override
    public void init() {
        GlobalStorage.onInit(this);
        this.driver = GlobalStorage.getOrCreateDriver(hardwareMap);
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

        TrajectorySequenceBuilder builder = driver.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(90)));
        builder
            .strafeRight(3)
            .back(35);
        this.sequence = builder.build();

        this.clawSlide.claw.closeAll();
        this.telemetry.addLine("Initialized");
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
        this.telemetry.update();
    }
}
