package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.MecanumDrive;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp(name = "BPathRecorder")
public class BPathRecorder extends OpMode {
    static final double MAX_DRIVE_POWER = 0.5;

    MecanumDrive drive;
    SampleMecanumDrive driver;
    ClawSlide clawSlide;

    @Override
    public void init() {
        this.drive = new MecanumDrive(
                MAX_DRIVE_POWER,
                hardwareMap.get(DcMotor.class, "rightFront"),
                hardwareMap.get(DcMotor.class, "rightRear"),
                hardwareMap.get(DcMotor.class, "leftFront"),
                hardwareMap.get(DcMotor.class, "leftRear"));
        this.driver = new SampleMecanumDrive(hardwareMap);
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
    }

    @Override
    public void loop() {
        com.acmerobotics.roadrunner.localization.Localizer localizer = this.driver.getLocalizer();
        localizer.update();

        Pose2d pos = localizer.getPoseEstimate();

        boolean clawActioned = false;
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
            this.clawSlide.slideLift.move((int) (-this.gamepad2.left_stick_y * 50));
            clawActioned = true;
        }
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
        if (this.gamepad2.b) {
            this.clawSlide.claw.setRotate(90);
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
        if (clawActioned) {
            this.clawSlide.cancelAction();
        }
        this.clawSlide.update();

        this.drive.shift(this.gamepad1.left_stick_x, this.gamepad1.left_stick_y);
        this.drive.rotate(this.gamepad1.right_stick_x * 0.5);
        this.drive.updatePowers();

        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));

        this.telemetry.addData("LeftSlideRot", this.clawSlide.slideRotate.getLeftPosition());
        this.telemetry.addData("RightSlideRot", this.clawSlide.slideRotate.getRightPosition());
        this.telemetry.addData("SlideMaxPos", this.clawSlide.slideLift.getMaxPosition());
        this.telemetry.addData("LeftPosition", this.clawSlide.slideLift.getLeftPosition());
        this.telemetry.addData("RightPosition", this.clawSlide.slideLift.getRightPosition());
        this.telemetry.addData("LeftClawRot", this.clawSlide.claw.getLeftRotAngle());
        this.telemetry.addData("RightClawRot", this.clawSlide.claw.getRightRotAngle());
        this.telemetry.update();
    }
}
