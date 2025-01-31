package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@TeleOp(name = "ASingleTeleOp")
public class ASingleTeleOp extends AbstractTeleOp {
    static final Pose2d BASKET_READY_POSE = new Pose2d(5.5, 23.25, Math.toRadians(135));

    @Override
    protected boolean shouldReleaseRestrictions() {
        return this.gamepad1.right_bumper;
    }

    @Override
    protected boolean shouldApplyRestrictions() {
        return false;
    }

    @Override
    protected float getXPower() {
        return this.gamepad1.left_stick_x;
    }

    @Override
    protected float getYPower() {
        return this.gamepad1.left_stick_y;
    }

    @Override
    protected float getRotatePower() {
        return this.gamepad1.right_stick_x;
    }

    @Override
    protected int getSlideRotateTargetSpeed() {
        if (this.gamepad1.dpad_up) {
            return -250;
        } else if (this.gamepad1.dpad_down) {
            return 250;
        }
        return 0;
    }

    @Override
    protected boolean shouldSlideRotateReset() {
        return false;
    }

    @Override
    protected int getSlideLiftTargetSpeed() {
        if (this.gamepad1.left_trigger >= 0.5) {
            return -2000;
        } else if (this.gamepad1.right_trigger >= 0.5) {
            return 2000;
        }
        return 0;
    }

    @Override
    protected boolean shouldOperateLeftClaw() {
        return !this.prevGamepad1.left_bumper && this.gamepad1.left_bumper;
    }

    @Override
    protected boolean shouldOpenLeftClaw() {
        return this.clawSlide.claw.isLeftClosed();
    }

    @Override
    protected boolean shouldOperateRightClaw() {
        return !this.prevGamepad1.right_bumper && this.gamepad1.right_bumper;
    }

    @Override
    protected boolean shouldOpenRightClaw() {
        return this.clawSlide.claw.isRightClosed();
    }

    @Override
    protected boolean beforeClawSlideUpdate(float dt) {
        if (this.inSlideAdjustMode()) {
            return false;
        }
        boolean updated = false;
        if (this.gamepad1.b) {
            this.clawSlide.claw.setRotate(110);
            updated = true;
        } else if (!this.prevGamepad1.y && this.gamepad1.y) {
            this.clawSlide.retractAndPullUp();
        } else if (!this.prevGamepad1.x && this.gamepad1.x) {
            this.clawSlide.putDown();
        } else if (!this.prevGamepad1.a && this.gamepad1.a) {
            this.clawSlide.putDownAndExtendNoPick();
        }
        if (this.gamepad1.dpad_left) {
            this.clawSlide.claw.rotate(-50 * dt);
            updated = true;
        } else if (this.gamepad1.dpad_right) {
            this.clawSlide.claw.rotate(50 * dt);
            updated = true;
        }
        return updated;
    }

    @Override
    protected void beforeDriveUpdate() {
        if (!this.prevGamepad1.left_stick_button && this.gamepad1.left_stick_button) {
            // Go in front of the basket and turn to 135°
            Pose2d pose = this.driver.getPoseEstimate();
            this.driver.followTrajectorySequenceAsync(this.driver.trajectorySequenceBuilder(pose)
                .lineToLinearHeading(BASKET_READY_POSE)
                .build());
            this.clawSlide.claw.setRotate(8);
        } else if (!this.prevGamepad1.right_stick_button && this.gamepad1.right_stick_button) {
            // Turn to 270°
            Pose2d pose = this.driver.getPoseEstimate();
            double angle = Math.toRadians(270) - pose.getHeading();
            double angle2 = angle - 2 * Math.PI;
            if (Math.abs(angle2) < Math.abs(angle)) {
                angle = angle2;
            }
            this.driver.followTrajectorySequenceAsync(this.driver.trajectorySequenceBuilder(pose).turn(angle).build());
        }
    }
}
