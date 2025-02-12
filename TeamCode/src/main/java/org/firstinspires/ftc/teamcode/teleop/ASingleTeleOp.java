package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.auto.BasketSide;
import org.firstinspires.ftc.teamcode.drive.Claw;

@TeleOp(name = "ASingleTeleOp")
public class ASingleTeleOp extends AbstractTeleOp {
    static final Pose2d BASKET_READY_POSE = BasketSide.BASKET_READY_POSE;

    private boolean stage2 = false;

    protected boolean isGamepad2Active() {
        return this.gamepad2.getGamepadId() != Gamepad.ID_UNASSOCIATED;
    }

    @Override
    protected boolean shouldReleaseRestrictions() {
        return this.isGamepad2Active();
    }

    @Override
    protected boolean shouldApplyRestrictions() {
        return !this.isGamepad2Active();
    }

    @Override
    protected void onReleaseRestriction() {
        super.onReleaseRestriction();
        this.stage2 = true;
    }

    @Override
    protected void onApplyRestriction() {
        super.onApplyRestriction();
        this.stage2 = false;
    }

    @Override
    protected float getXPower() {
        return this.stage2 ? this.gamepad2.right_stick_x : this.gamepad1.left_stick_x;
    }

    @Override
    protected float getYPower() {
        return this.stage2 ? this.gamepad2.right_stick_y : this.gamepad1.left_stick_y;
    }

    @Override
    protected float getRotatePower() {
        return this.stage2 ? this.gamepad2.right_trigger - this.gamepad2.left_trigger : this.gamepad1.right_stick_x;
    }

    @Override
    protected float getRotateScalePower() {
        return this.stage2 ? 0.35f : 1.0f;
    }

    @Override
    protected int getSlideRotateTargetSpeed() {
        if (this.stage2) {
            if (this.gamepad2.dpad_up) {
                return -250;
            } else if (this.gamepad2.dpad_down) {
                return 250;
            }
            return 0;
        }
        if (this.gamepad1.dpad_up) {
            return -250;
        } else if (this.gamepad1.dpad_down) {
            return 250;
        }
        return 0;
    }

    @Override
    protected boolean shouldSlideRotateReset() {
        return this.stage2 && this.gamepad2.dpad_left;
    }

    @Override
    protected int getSlideLiftTargetSpeed() {
        if (this.stage2) {
            return (int)(this.gamepad2.left_stick_y * -2000);
        }
        return (int)((this.gamepad1.right_trigger - this.gamepad1.left_trigger) * 2000);
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
        if (this.stage2) {
            return this.stage2ClawSlideUpdate(dt);
        }
        boolean updated = false;
        if (!this.prevGamepad1.b && this.gamepad1.b && !this.gamepad1.start) {
            this.clawSlide.claw.openAll();
            this.clawSlide.putDownInBar();
        } else if (!this.prevGamepad1.y && this.gamepad1.y) {
            this.clawSlide.retractAndPullUp();
        } else if (!this.prevGamepad1.x && this.gamepad1.x) {
            this.clawSlide.claw.openAll();
            this.clawSlide.putDown();
        } else if (!this.prevGamepad1.a && this.gamepad1.a) {
            this.clawSlide.claw.setRotate(20);
            updated = true;
        }
        if (this.gamepad1.dpad_left) {
            this.clawSlide.claw.rotate(-50 * dt);
            updated = true;
        } else if (this.gamepad1.dpad_right) {
            this.clawSlide.claw.rotate(50 * dt);
            updated = true;
        } else if (!this.prevGamepad1.left_stick_button && this.gamepad1.left_stick_button) {
            // Go in front of the basket and turn to 135°
            Pose2d pose = this.driver.getPoseEstimate();
            this.clawSlide.setAction(BasketSide.buildPutSequence(this.driver, this.clawSlide, pose));
            this.clawSlide.claw.setRotate(8);
        }
        return updated;
    }

    protected boolean stage2ClawSlideUpdate(float dt) {
        boolean updated = false;
        if (!this.prevGamepad2.y && this.gamepad2.y) {
            this.clawSlide.putDownForHang();
        } else if (!this.prevGamepad2.x && this.gamepad2.x) {
            this.clawSlide.slideLift.setPosition(0);
        } else if (!this.prevGamepad2.a && this.gamepad2.a) {
            this.clawSlide.claw.setRotate(Claw.MAX_ROT);
            updated = true;
        }
        if (this.gamepad2.dpad_left) {
            this.clawSlide.claw.rotate(-50 * dt);
            updated = true;
        } else if (this.gamepad2.dpad_right) {
            this.clawSlide.claw.rotate(50 * dt);
            updated = true;
        }
        return updated;
    }

    @Override
    protected void beforeDriveUpdate() {
        if (this.stage2) {
            return;
        }
        if (!this.prevGamepad1.right_stick_button && this.gamepad1.right_stick_button) {
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
