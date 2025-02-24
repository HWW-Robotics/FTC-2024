package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Configurations;
import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.cache.CachedHardware;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.MecanumDrive;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTeleOp extends OpMode {
    static final double MAX_DRIVE_POWER = 0.5;
    public Gamepad prevGamepad1 = new Gamepad();
    public Gamepad prevGamepad2 = new Gamepad();

    long lastLoopStart = 0;
    List<CachedHardware> cachedHardware = new ArrayList<>();
    SampleMecanumDrive driver;
    MecanumDrive drive;
    ClawSlide clawSlide;
    boolean wasAdjusting = false;

    @Override
    public void init() {
        GlobalStorage.onInit(this);
        this.driver = GlobalStorage.getOrCreateDriver(hardwareMap);
        this.driver.followTrajectorySequenceAsync(null);
        this.drive = new MecanumDrive(
            MAX_DRIVE_POWER,
            hardwareMap.get(DcMotorEx.class, Configurations.RIGHT_FRONT_WHEEL),
            hardwareMap.get(DcMotorEx.class, Configurations.RIGHT_REAR_WHEEL),
            hardwareMap.get(DcMotorEx.class, Configurations.LEFT_FRONT_WHEEL),
            hardwareMap.get(DcMotorEx.class, Configurations.LEFT_REAR_WHEEL)
        );
        this.clawSlide = GlobalStorage.getOrCreateClawSlide(hardwareMap);
    }

    @Override
    public void init_loop() {
        final long startTime = System.nanoTime();
        final float loopInterval = (float)(startTime - lastLoopStart) / 1e9f;

        for (CachedHardware c : cachedHardware) {
            c.updateInfos();
        }

        final long endTime = System.nanoTime();
        lastLoopStart = startTime;
        this.telemetry.addData("MSPT", "%.06f", (float)(endTime - startTime) / 1e6f);
        this.telemetry.addData("TPS", "%.01f", 1f / loopInterval);
    }

    @Override
    public void start() {
        this.clawSlide.claw.closeAll();
        this.clawSlide.claw.setRotate(30);
    }

    @Override
    public void loop() {
        final long startTime = System.nanoTime();
        final float loopInterval = (float)(startTime - lastLoopStart) / 1e9f;

        for (CachedHardware c : cachedHardware) {
            c.updateInfos();
        }

        this.preLoop();

        boolean clawActioned = false;

        if (this.shouldReleaseRestrictions()) {
            this.onReleaseRestriction();
        } else if (this.shouldApplyRestrictions()) {
            this.onApplyRestriction();
        }

        /// Slides
        boolean adjustingSlide = this.inSlideAdjustMode();
        if (adjustingSlide) {
            clawActioned = true;
            this.onAdjustSlide();
            this.wasAdjusting = true;
        } else {
            if (this.wasAdjusting) {
                this.onStopAdjustSlide();
            }
            int rotateSpeed = this.getSlideRotateTargetSpeed();
            if (rotateSpeed != 0) {
                this.clawSlide.slideRotate.move((int)(rotateSpeed * loopInterval));
                clawActioned = true;
            } else if (this.shouldSlideRotateReset()) {
                this.clawSlide.slideRotate.setPosition(0);
                clawActioned = true;
            }
            int liftSpeed = this.getSlideLiftTargetSpeed();
            if (liftSpeed != 0) {
                this.clawSlide.slideLift.move((int)(liftSpeed * loopInterval));
                clawActioned = true;
            }
        }

        /// Claws
        if (this.shouldOperateLeftClaw()) {
            if (this.shouldOpenLeftClaw()) {
                this.clawSlide.claw.openLeft();
            } else {
                this.clawSlide.claw.closeLeft();
            }
        }
        if (this.shouldOperateRightClaw()) {
            if (this.shouldOpenRightClaw()) {
                this.clawSlide.claw.openRight();
            } else {
                this.clawSlide.claw.closeRight();
            }
        }

        if (this.beforeClawSlideUpdate(loopInterval)) {
            clawActioned = true;
        }
        this.telemetry.addData("clawActioned", clawActioned);
        this.telemetry.addData("clawInAction", this.clawSlide.inAction());
        if (clawActioned) {
            this.clawSlide.cancelAction();
        }
        if (!adjustingSlide) {
            this.telemetry.addLine("updating clawSlide");
            this.clawSlide.update();
        }

        /// Drives
        this.beforeDriveUpdate();
        float xPower = this.getXPower(), yPower = this.getYPower(), rotatePower = this.getRotatePower();
        boolean moving = xPower != 0 || yPower != 0 || rotatePower != 0;
        if (moving) {
            this.drive.shift(xPower, yPower);
            this.drive.rotate(Math.signum(rotatePower) * rotatePower * rotatePower * this.getRotateScalePower());
        }
        if (this.driver.isBusy()) {
            if (moving) {
                this.drive.updatePowers();
                this.driver.followTrajectorySequenceAsync(null);
                this.driver.updatePoseEstimate();
            } else {
                this.driver.update();
            }
        } else {
            this.drive.updatePowers();
            this.driver.updatePoseEstimate();
        }

        Pose2d pos = this.driver.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));
        this.telemetry.addLine();
        this.telemetry.addData("SlideRot", this.clawSlide.slideRotate.getLeftPosition());
        this.telemetry.addData("SlideRotTarget", this.clawSlide.slideRotate.getTargetPosition());
        this.telemetry.addData("SlideLift", this.clawSlide.slideLift.getLeftPosition());
        this.telemetry.addData("SlideLift Diff", this.clawSlide.slideLift.getRightPosition() - this.clawSlide.slideLift.getLeftPosition());
        this.telemetry.addData("ClawRot", this.clawSlide.claw.getLeftRotAngle());

        this.postLoop();

        this.prevGamepad1.copy(this.gamepad1);
        this.prevGamepad2.copy(this.gamepad2);

        final long endTime = System.nanoTime();
        lastLoopStart = startTime;
        this.telemetry.addData("MSPT", "%.06f", (float)(endTime - startTime) / 1e6f);
        this.telemetry.addData("TPS", "%.01f", 1f / loopInterval);
    }

    protected void onAdjustSlide() {
        if (this.gamepad2.dpad_right) {
            this.clawSlide.slideRotate.resetPosition();
        }
        if (this.gamepad2.b) {
            this.clawSlide.slideLift.resetPosition();
        }
        if (this.gamepad2.dpad_up) {
            this.adjustSlideRotateUp();
        } else if (this.gamepad2.dpad_down) {
            this.adjustSlideRotateDown();
        } else {
            final float power = this.gamepad2.left_stick_x * 0.8f;
            this.telemetry.addData("adjusting slide", power);
            this.clawSlide.slideRotate.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            this.clawSlide.slideRotate.setPower(power);
        }
        if (this.gamepad2.left_stick_y != 0) {
            float power = -this.gamepad2.left_stick_y * 0.8f;
            this.clawSlide.slideLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            this.clawSlide.slideLift.setPower(power);
        }
    }

    protected void onStopAdjustSlide() {
        this.clawSlide.slideLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.clawSlide.slideRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.clawSlide.slideLift.setPower(ClawSlide.LIFT_POWER1);
        this.clawSlide.slideRotate.setPower(ClawSlide.ROTATE_POWER);
    }

    protected void adjustSlideRotateUp() {
        this.telemetry.addData("adjusting slide", "up");
        this.clawSlide.slideRotate.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.clawSlide.slideRotate.setPower(-0.6f);
    }

    protected void adjustSlideRotateDown() {
        this.telemetry.addData("adjusting slide", "down");
        this.clawSlide.slideRotate.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.clawSlide.slideRotate.setPower(0.4f);
    }

    protected void preLoop() {}
    protected void postLoop() {}

    protected boolean beforeClawSlideUpdate(float dt) {
        return false;
    }

    protected void beforeDriveUpdate() {}

    protected abstract boolean shouldReleaseRestrictions();
    protected abstract boolean shouldApplyRestrictions();

    protected void onReleaseRestriction() {
        this.clawSlide.releaseRestrictions();
    }

    protected void onApplyRestriction() {
        this.clawSlide.setRestrictions();
    }

    protected abstract float getXPower();
    protected abstract float getYPower();
    protected abstract float getRotatePower();
    protected float getRotateScalePower() {
        return 1.0f;
    }

    protected boolean inSlideAdjustMode() {
        return this.gamepad2.getGamepadId() != Gamepad.ID_UNASSOCIATED && this.gamepad2.guide;
    }
    protected abstract int getSlideRotateTargetSpeed();
    protected abstract boolean shouldSlideRotateReset();
    protected abstract int getSlideLiftTargetSpeed();

    protected abstract boolean shouldOperateLeftClaw();
    protected abstract boolean shouldOpenLeftClaw();
    protected abstract boolean shouldOperateRightClaw();
    protected abstract boolean shouldOpenRightClaw();
}
