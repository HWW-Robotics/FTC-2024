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
            return -15;
        }
        if (this.gamepad2.dpad_down) {
            return 15;
        }
        return 0;
    }

    @Override
    protected boolean shouldSlideRotateReset() {
        return this.gamepad2.dpad_left;
    }

    @Override
    protected int getSlideLiftTargetSpeed() {
        return (int) (-this.gamepad2.left_stick_y * 120);
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
    protected boolean beforeClawSlideUpdate() {
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
        } else if (this.gamepad2.y) {
            this.clawSlide.retractAndPullUp();
        } else if (this.gamepad2.x) {
            this.clawSlide.putDown();
        } else if (this.gamepad2.a) {
            this.clawSlide.putDownAndExtend();
        } else if (this.gamepad2.right_stick_y != 0) {
            this.clawSlide.claw.rotate(-this.gamepad2.right_stick_y * 5);
            updated = true;
        }
        return updated;
    }
}
