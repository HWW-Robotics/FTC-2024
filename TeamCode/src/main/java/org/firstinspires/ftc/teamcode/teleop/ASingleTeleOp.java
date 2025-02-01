package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.drive.Claw;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@TeleOp(name = "ASingleTeleOp")
public class ASingleTeleOp extends AbstractTeleOp {
    static final Pose2d BASKET_READY_POSE = new Pose2d(5.5, 23.25, Math.toRadians(135));

    private static final Gamepad.LedEffect RELEASED_WARN_LED = new Gamepad.LedEffect.Builder()
        .addStep(1.0, 1.0, 0.0, 650)
        .addStep(0.0, 0.0, 0.0, 350)
        .setRepeating(true)
        .build();

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
        this.gamepad1.runLedEffect(RELEASED_WARN_LED);
        this.gamepad2.runLedEffect(RELEASED_WARN_LED);
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
        if (this.gamepad1.b && !this.gamepad1.start) {
            if (this.clawSlide.slideRotate.getTargetPosition() < 600) {
                this.clawSlide.claw.setRotate(110);
            }
            updated = true;
        } else if (!this.prevGamepad1.y && this.gamepad1.y) {
            this.clawSlide.retractAndPullUp();
        } else if (!this.prevGamepad1.x && this.gamepad1.x) {
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
        }
        return updated;
    }

    protected boolean stage2ClawSlideUpdate(float dt) {
        boolean updated = false;
        if (!this.prevGamepad2.y && this.gamepad2.y) {
            this.clawSlide.putDownForHang();
        } else if (!this.prevGamepad2.x && this.gamepad2.x) {
            // this.clawSlide.putDown();
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
