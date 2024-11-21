package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.Claw;

@TeleOp(name = "ClawControl")
public class ClawControl extends OpMode {
    Claw claw;

    @Override
    public void init() {
        this.claw = new Claw(
            hardwareMap.get(Servo.class, "ClawRotLeft"),
            hardwareMap.get(Servo.class, "ClawRotRight"),
            hardwareMap.get(Servo.class, "ClawArmLeft"),
            hardwareMap.get(Servo.class, "ClawArmRight"));
        this.claw.closeAll();
    }

    @Override
    public void loop() {
        if (this.gamepad2.left_bumper) {
            this.claw.closeLeft();
        } else {
            this.claw.openLeft();
        }
        if (this.gamepad2.right_bumper) {
            this.claw.closeRight();
        } else {
            this.claw.openRight();
        }
        if (!this.gamepad2.guide) {
            if (this.gamepad2.y) {
                this.claw.setRotate(180);
            } else if (this.gamepad2.b) {
                this.claw.setRotate(90);
            } else if (this.gamepad2.x) {
                this.claw.setRotate(0);
            } else {
                this.claw.rotate(this.gamepad2.right_stick_y * 5);
            }
        }
        this.telemetry.addData("LeftRot", this.claw.getLeftRotAngle());
        this.telemetry.addData("RightRot", this.claw.getRightRotAngle());
        this.telemetry.addData("LeftClaw", this.claw.getLeftClawAngle());
        this.telemetry.addData("RightClaw", this.claw.getRightClawAngle());
        this.telemetry.update();
    }
}
