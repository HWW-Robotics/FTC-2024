package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "AMainTeleOp")
public class AMainTeleOp extends AbstractTeleOp {
    @Override
    protected boolean shouldReleaseRestrictions() {
        return this.gamepad1.x;
    }

    @Override
    protected boolean shouldApplyRestrictions() {
        return this.gamepad1.y;
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
        if (this.gamepad2.dpad_up) {
            return -250;
        }
        if (this.gamepad2.dpad_down) {
            return 250;
        }
        return 0;
    }

    @Override
    protected boolean shouldSlideRotateReset() {
        return this.gamepad2.dpad_left;
    }

    @Override
    protected int getSlideLiftTargetSpeed() {
        return (int) (-this.gamepad2.left_stick_y * 2000);
    }

    @Override
    protected boolean shouldOperateLeftClaw() {
        return this.prevGamepad2.left_bumper != this.gamepad2.left_bumper;
    }

    @Override
    protected boolean shouldOpenLeftClaw() {
        return !this.gamepad2.left_bumper;
    }

    @Override
    protected boolean shouldOperateRightClaw() {
        return this.prevGamepad2.right_bumper != this.gamepad2.right_bumper;
    }

    @Override
    protected boolean shouldOpenRightClaw() {
        return !this.gamepad2.right_bumper;
    }

    @Override
    protected boolean beforeClawSlideUpdate(float dt) {
        if (this.inSlideAdjustMode()) {
            return false;
        }
        boolean updated = false;
        if (this.gamepad2.left_trigger >= 0.7) {
            if (this.clawSlide.getRestricted()) {
                this.clawSlide.slideLift.setPosition(990);
                updated = true;
            }
        }
        if (this.gamepad2.b) {
            this.clawSlide.claw.setRotate(110);
            updated = true;
        } else if (!this.prevGamepad2.y && this.gamepad2.y) {
            this.clawSlide.retractAndPullUp();
        } else if (!this.prevGamepad2.x && this.gamepad2.x) {
            this.clawSlide.putDown();
        } else if (!this.prevGamepad2.a && this.gamepad2.a) {
            this.clawSlide.putDownAndExtend();
        } else if (this.gamepad2.right_stick_y != 0) {
            this.clawSlide.claw.rotate(-this.gamepad2.right_stick_y * 80 * dt);
            updated = true;
        }
        return updated;
    }
}
