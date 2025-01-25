package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.drive.MotorPair;

@Disabled
@TeleOp(name = "SlideControl")
public class SlideControl extends OpMode {
    MotorPair slideRotate, slideLift;

    @Override
    public void init() {
        this.slideRotate = new MotorPair(
        0,
            1000,
            0.5f,
            hardwareMap.get(DcMotor.class, "leftRotation"),
            hardwareMap.get(DcMotor.class, "rightRotation"),
            DcMotor.Direction.REVERSE);
        this.slideLift = new MotorPair(
        0,
            2750,
            1.0f,
            hardwareMap.get(DcMotor.class, "leftSlide"),
            hardwareMap.get(DcMotor.class, "rightSlide"));
        this.slideRotate.resetPosition();
        this.slideLift.resetPosition();
    }

    @Override
    public void loop() {
        if (this.gamepad2.guide) {
            if (this.gamepad2.dpad_right) {
                this.slideRotate.resetPosition();
            }
            if (this.gamepad2.b) {
                this.slideLift.resetPosition();
            }
            if (this.gamepad2.dpad_up) {
                this.slideRotate.left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.slideRotate.right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.slideRotate.left.setPower(-0.6);
                this.slideRotate.right.setPower(-0.6);
            } else if (this.gamepad2.dpad_down) {
                this.slideRotate.left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.slideRotate.right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.slideRotate.left.setPower(0.4);
                this.slideRotate.right.setPower(0.4);
            } else {
                final double power = this.gamepad2.left_stick_x * -0.8;
                this.slideRotate.left.setPower(power);
                this.slideRotate.right.setPower(power);
            }
            if (this.gamepad2.left_stick_y != 0) {
                double power = -this.gamepad2.left_stick_y * 0.2;
                this.slideLift.left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.slideLift.right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.slideLift.left.setPower(power);
                this.slideLift.right.setPower(power);
            }
        } else {
            if (this.slideLift.getLeftPosition() < 250) {
                if (this.gamepad2.dpad_up) {
                    this.slideRotate.move(-15);
                } else if (this.gamepad2.dpad_down) {
                    this.slideRotate.move(15);
                } else if (this.gamepad2.dpad_left) {
                    this.slideRotate.setPosition(0);
                } else {
                    this.slideRotate.move((int) (this.gamepad2.left_stick_x * 20));
                }
            }
            this.slideLift.move((int) (-this.gamepad2.left_stick_y * 50));
            this.slideRotate.update();
            this.slideLift.update();
        }

        this.telemetry.addData("Rotation Power", "%.03f", this.slideRotate.getPower());
        this.telemetry.addData("LeftRotation", this.slideRotate.getLeftPosition());
        this.telemetry.addData("RightRotation", this.slideRotate.getRightPosition());
        this.telemetry.addData("LeftPosition", this.slideLift.getLeftPosition());
        this.telemetry.addData("RightPosition", this.slideLift.getRightPosition());
        this.telemetry.update();
    }
}
