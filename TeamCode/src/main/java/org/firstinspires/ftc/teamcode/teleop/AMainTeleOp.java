package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@TeleOp(name = "AMainTeleOp")
public class AMainTeleOp extends OpMode {
    public static AMainTeleOp INSTANCE = null;
    static final double MAX_DRIVE_POWER = 0.5;
    MecanumDrive drive;
    ClawSlide clawSlide;

    @Override
    public void init() {
        INSTANCE = this;
        this.drive = new MecanumDrive(
            MAX_DRIVE_POWER,
            hardwareMap.get(DcMotor.class, "rightFront"),
            hardwareMap.get(DcMotor.class,"rightRear"),
            hardwareMap.get(DcMotor.class,"leftFront"),
            hardwareMap.get(DcMotor.class,"leftRear")
        );
        this.clawSlide = new ClawSlide(
            hardwareMap.get(DcMotor.class, "leftRotation"),
            hardwareMap.get(DcMotor.class, "rightRotation"),
            hardwareMap.get(DcMotor.class, "leftSlide"),
            hardwareMap.get(DcMotor.class, "rightSlide"),
            hardwareMap.get(Servo.class, "ClawRotLeft"),
            hardwareMap.get(Servo.class, "ClawRotRight"),
            hardwareMap.get(Servo.class, "ClawArmLeft"),
            hardwareMap.get(Servo.class, "ClawArmRight")
        );
        this.clawSlide.claw.closeAll();
    }

    @Override
    public void loop() {
        final long startTime = System.nanoTime();

        boolean clawActioned = false;

        if (this.gamepad1.x) {
            this.clawSlide.releaseRestrictions();
        } else if (this.gamepad1.y) {
            this.clawSlide.setRestrictions();
        }

        /// Slides
        if (this.gamepad2.guide) {
            clawActioned = true;
            if (this.gamepad2.dpad_right) {
                this.clawSlide.slideRotate.resetPosition();
            }
            if (this.gamepad2.b) {
                this.clawSlide.slideLift.resetPosition();
            }
            if (this.gamepad2.dpad_up) {
                this.clawSlide.slideRotate.left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.clawSlide.slideRotate.right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.clawSlide.slideRotate.left.setPower(-0.6);
                this.clawSlide.slideRotate.right.setPower(-0.6);
            } else if (this.gamepad2.dpad_down) {
                this.clawSlide.slideRotate.left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.clawSlide.slideRotate.right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.clawSlide.slideRotate.left.setPower(0.4);
                this.clawSlide.slideRotate.right.setPower(0.4);
            } else {
                final double power = this.gamepad2.left_stick_x * -0.8;
                this.clawSlide.slideRotate.left.setPower(power);
                this.clawSlide.slideRotate.right.setPower(power);
            }
            if (this.gamepad2.left_stick_y != 0) {
                double power = -this.gamepad2.left_stick_y * 0.8;
                this.clawSlide.slideLift.left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.clawSlide.slideLift.right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.clawSlide.slideLift.left.setPower(power);
                this.clawSlide.slideLift.right.setPower(power);
            }
        } else {
            if (this.gamepad2.dpad_up) {
                this.clawSlide.slideRotate.move(-15);
                clawActioned = true;
            } else if (this.gamepad2.dpad_down) {
                this.clawSlide.slideRotate.move(15);
                clawActioned = true;
            } else if (this.gamepad2.dpad_left) {
                this.clawSlide.slideRotate.setPosition(0);
                clawActioned = true;
            }
            if (this.gamepad2.left_stick_y != 0) {
                this.clawSlide.slideLift.move((int) (-this.gamepad2.left_stick_y * 100));
                clawActioned = true;
            }
        }

        /// Claws
        if (this.gamepad2.left_bumper) {
            this.clawSlide.claw.closeLeft();
        } else {
            this.clawSlide.claw.openLeft();
        }
        if (this.gamepad2.right_bumper) {
            this.clawSlide.claw.closeRight();
        } else {
            this.clawSlide.claw.openRight();
        }
        if (!this.gamepad2.guide) {
            if (this.gamepad2.b) {
                this.clawSlide.claw.setRotate(118);
                clawActioned = true;
            } else if (this.gamepad2.y) {
                this.clawSlide.retractAndPullUp();
            } else if (this.gamepad2.x) {
                this.clawSlide.putDown();
            } else if (this.gamepad2.a) {
                this.clawSlide.putDownAndExtend();
            } else if (this.gamepad2.right_stick_y != 0) {
                this.clawSlide.claw.rotate(-this.gamepad2.right_stick_y * 5);
                clawActioned = true;
            }
        }
        if (clawActioned) {
            this.clawSlide.cancelAction();
        }
        if (!this.gamepad2.guide) {
            this.clawSlide.update();
        }

        /// Drives
        this.drive.shift(this.gamepad1.left_stick_x, this.gamepad1.left_stick_y);
        this.drive.rotate(this.gamepad1.right_stick_x);
        this.drive.updatePowers();

        this.telemetry.addData("SlideRot", this.clawSlide.slideRotate.getLeftPosition());
        this.telemetry.addData("SlideMaxPos", this.clawSlide.slideLift.getMaxPosition());
        this.telemetry.addData("SlideLift", this.clawSlide.slideLift.getLeftPosition());
        this.telemetry.addData("ClawRot", this.clawSlide.claw.getLeftRotAngle());

        final long endTime = System.nanoTime();
        this.telemetry.addData("LOOP DURATION", (double)(endTime - startTime) / 1000);
    }

    public static void addLog(String caption, Object data) {
        if (INSTANCE == null) {
            return;
        }
        INSTANCE.telemetry.addData(caption, data);
    }
}
