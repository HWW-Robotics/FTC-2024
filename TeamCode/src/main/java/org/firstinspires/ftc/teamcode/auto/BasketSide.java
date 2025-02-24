package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.action.ActionSequence;
import org.firstinspires.ftc.teamcode.action.ActionStage;
import org.firstinspires.ftc.teamcode.action.RoadRunnerPath;
import org.firstinspires.ftc.teamcode.action.TimedAction;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;

import java.util.function.Consumer;

@Autonomous(name = "BasketSide", preselectTeleOp = "ASingleTeleOp")
public class BasketSide extends OpMode {

    long lastLoopStart = 0;
    SampleMecanumDrive driver;
    ClawSlide clawSlide;
    ActionSequence sequence;
    public static final Pose2d BASKET_READY_POSE = new Pose2d(3.60, 26.10, Math.toRadians(135));
    static final Pose2d AFTER_PUT_POSE = new Pose2d(6.68, 20.73, Math.toRadians(130));

    @Override
    public void init() {
        GlobalStorage.onInit(this);
        GlobalStorage.destroyAll();
        this.driver = GlobalStorage.getOrCreateDriver(hardwareMap);
        this.driver.setPoseEstimate(new Pose2d(0, 0, 0));
        this.clawSlide = GlobalStorage.getOrCreateClawSlide(hardwareMap);

        this.telemetry.addLine("Building Sequence");
        this.telemetry.update();

        this.sequence = new ActionSequence(
            this.buildPutSequence(new Pose2d(0, 0, 0)),
            this.buildPickAndPutSequence(BASKET_READY_POSE, new Pose2d(14,17.08, Math.toRadians(0))),
            this.buildPickAndPutSequence(BASKET_READY_POSE, new Pose2d(14, 26.66, Math.toRadians(0)), (builder) -> {
                builder.turn(Math.toRadians(-179));
                // builder.splineToSplineHeading(new Pose2d(14, 17, Math.toRadians(130)), Math.toRadians(-90));
                builder.lineToLinearHeading(BASKET_READY_POSE);
            }),
            new TimedAction(0.2, () -> {
                this.clawSlide.slideLift.setMinPosition(0);
                this.clawSlide.slideLift.setPosition(0);
                this.clawSlide.claw.closeAll();
            })
        );

        // builder
        //     .lineToLinearHeading(new Pose2d(3.57, 23.08, Math.toRadians(22.7)))
        //     .lineToLinearHeading(new Pose2d(17.45, 27.07, Math.toRadians(23.5)));
        // this.addPickAndPutSequence(builder);

        // builder.lineToLinearHeading(new Pose2d(3.0, 23.0, Math.toRadians(0)));

        // this.sequence = builder.build();

        this.clawSlide.claw.closeAll();
        this.telemetry.addLine("Initialized");
        this.telemetry.update();
    }

    @Override
    public void init_loop() {
        final long startTime = System.nanoTime();
        final float loopInterval = (float)(startTime - lastLoopStart) / 1e9f;

        this.driver.getLocalizer().update();
        this.telemetry.addLine("Initialized");
        Pose2d pos = this.driver.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));

        this.telemetry.addLine();
        final long endTime = System.nanoTime();
        lastLoopStart = startTime;
        this.telemetry.addData("MSPT", "%.06f", (float)(endTime - startTime) / 1e6f);
        this.telemetry.addData("TPS", "%.01f", 1f / loopInterval);
    }

    @Override
    public void start() {
        this.clawSlide.setAction(this.sequence);
        this.telemetry.addLine("Started");
        this.telemetry.update();

        this.clawSlide.claw.setRotate(30);
    }

    @Override
    public void loop() {
        final long startTime = System.nanoTime();
        final float loopInterval = (float)(startTime - lastLoopStart) / 1e9f;

        this.clawSlide.update();
        this.driver.update();

        this.telemetry.addData("isBusy", this.driver.isBusy());
        this.telemetry.addData("inAction", this.clawSlide.inAction());

        // Pose2d pos = this.driver.getPoseEstimate();
        // this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        // this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));

        double rightLiftPos = this.clawSlide.slideLift.right.getCurrentPosition();
        this.telemetry.addData("Lift Right", rightLiftPos);
        this.telemetry.addData("Lift Diff", this.clawSlide.slideLift.left.getCurrentPosition() - rightLiftPos);

        this.telemetry.addLine();
        final long endTime = System.nanoTime();
        lastLoopStart = startTime;
        this.telemetry.addData("MSPT", "%.06f", (float)(endTime - startTime) / 1e6f);
        this.telemetry.addData("TPS", "%.01f", 1f / loopInterval);
    }

    private ActionStage buildPickAndPutSequence(Pose2d lastPose, Pose2d target) {
        return this.buildPickAndPutSequence(lastPose, target, null);
    }

    private ActionStage buildPickAndPutSequence(Pose2d lastPose, Pose2d target, Consumer<TrajectorySequenceBuilder> beforePut) {
        return new ActionSequence(
            this.buildPickSequence(lastPose, target),
            this.buildPutSequence(target, beforePut)
        );
    }

    private ActionStage buildPickSequence(Pose2d lastPose, Pose2d target) {
        // begin pick
        return new ActionSequence(
            new RoadRunnerPath(this.driver, lastPose, target),
            new TimedAction(0, clawSlide.claw::openAll),
            clawSlide.PUT_DOWN_ACTION,
            new TimedAction(0.2, clawSlide.claw::closeAll),
            clawSlide.RETRACT_AND_PULL_UP_OPEN_ACTION
        );
    }

    private ActionStage buildPutSequence(Pose2d endPose) {
        return buildPutSequence(this.driver, this.clawSlide, endPose, null);
    }

    private ActionStage buildPutSequence(Pose2d endPose, Consumer<TrajectorySequenceBuilder> beforeMove) {
        return buildPutSequence(this.driver, this.clawSlide, endPose, beforeMove);
    }

    public static ActionStage buildPutSequence(SampleMecanumDrive driver, ClawSlide clawSlide, Pose2d endPose, Consumer<TrajectorySequenceBuilder> beforeMove) {
        TrajectorySequenceBuilder builder = driver.trajectorySequenceBuilder(endPose);
        builder.addTemporalMarker(() -> clawSlide.claw.setRotate(30));
        if (beforeMove != null) {
            beforeMove.accept(builder);
        } else {
            builder.lineToLinearHeading(BASKET_READY_POSE);
        }
        builder.addTemporalMarker(() -> clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION));
        TrajectorySequence beforePutPath = builder.build();
        return new ActionSequence(
            new RoadRunnerPath(driver, beforePutPath),
            new ActionStage() {
                @Override
                public boolean isDone() {
                    return Math.abs(clawSlide.slideLift.getLeftPosition() - ClawSlide.LIFT_MAX_POSITION) < 20;
                }

                @Override
                public void begin() {}
            },
            new TimedAction(0.33, () -> clawSlide.claw.setRotate(150)),
            new TimedAction(0.2, clawSlide.claw::openAll),
            new TimedAction(0.1, () -> clawSlide.claw.setRotate(30)),
            new TimedAction(1.0, () -> clawSlide.slideLift.setPosition(0))
            // new RoadRunnerPath(driver, beforePutPath.end(), AFTER_PUT_POSE)
        );
    }
}
