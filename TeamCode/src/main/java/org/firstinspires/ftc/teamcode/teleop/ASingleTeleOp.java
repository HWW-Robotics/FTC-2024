package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Configurations;
import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.MecanumDrive;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp(name = "ASingleTeleOp")
public class ASingleTeleOp extends OpMode {
    static final double MAX_DRIVE_POWER = 0.5;
    public Gamepad prevGamepad1 = new Gamepad();
    public Gamepad prevGamepad2 = new Gamepad();
    long lastLoopStart = 0;
    SampleMecanumDrive driver;
    MecanumDrive drive;
    ClawSlide clawSlide;

    @Override
    public void init() {
        GlobalStorage.onInit(this);
        this.driver = GlobalStorage.getOrCreateDriver(hardwareMap);
        this.drive = new MecanumDrive(
            MAX_DRIVE_POWER,
            hardwareMap.get(DcMotorEx.class, Configurations.RIGHT_FRONT_WHEEL),
            hardwareMap.get(DcMotorEx.class, Configurations.RIGHT_REAR_WHEEL),
            hardwareMap.get(DcMotorEx.class, Configurations.LEFT_FRONT_WHEEL),
            hardwareMap.get(DcMotorEx.class, Configurations.LEFT_REAR_WHEEL)
        );
        this.clawSlide = new ClawSlide(
            hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_ROT),
            hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_ROT),
            hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_LIFT),
            hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_LIFT),
            hardwareMap.get(Servo.class, Configurations.LEFT_CLAW_ROT),
            hardwareMap.get(Servo.class, Configurations.RIGHT_CLAW_ROT),
            hardwareMap.get(Servo.class, Configurations.LEFT_CLAW_ARM),
            hardwareMap.get(Servo.class, Configurations.RIGHT_CLAW_ARM)
        );
        this.clawSlide.claw.closeAll();
    }

    @Override
    public void loop() {
        final long startTime = System.nanoTime();
        final double loopInterval = (double)(startTime - lastLoopStart) / 1e9;

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
            boolean slideActioned = false;
            if (this.gamepad2.dpad_up) {
                this.clawSlide.slideRotate.move(-15);
                slideActioned = true;
            } else if (this.gamepad2.dpad_down) {
                this.clawSlide.slideRotate.move(15);
                slideActioned = true;
            } else if (this.gamepad2.dpad_left) {
                this.clawSlide.slideRotate.setPosition(0);
                slideActioned = true;
            }
            if (this.gamepad2.left_stick_y != 0) {
                this.clawSlide.slideLift.move((int) (-this.gamepad2.left_stick_y * 120));
                slideActioned = true;
            }
            clawActioned = clawActioned || slideActioned;
            if (!slideActioned) {
                if (this.gamepad1.left_bumper) {
                    if (this.clawSlide.slideRotate.getTargetPosition() > 5) {
                        this.clawSlide.slideRotate.move(-10);
                    } else {
                        this.clawSlide.slideLift.move(120);
                    }
                } else if (this.gamepad1.left_trigger >= 0.5) {
                    if (this.clawSlide.slideLift.getTargetPosition() > 50) {
                        this.clawSlide.slideLift.move(-120);
                    } else {
                        this.clawSlide.slideRotate.setPosition(980);
                    }
                } else if (this.prevGamepad1.left_bumper || this.prevGamepad1.left_trigger < 0.5) {
                    this.clawSlide.slideLift.setPosition(this.clawSlide.slideLift.getLeftPosition());
                    this.clawSlide.slideRotate.setPosition(this.clawSlide.slideRotate.getLeftPosition());
                }
            }
        }

        /// Claws
        if (!this.prevGamepad2.left_bumper && this.gamepad2.left_bumper) {
            this.clawSlide.claw.closeLeft();
        } else if (this.prevGamepad2.left_bumper && !this.gamepad2.left_bumper) {
            this.clawSlide.claw.openLeft();
        }
        if (!this.prevGamepad2.right_bumper && this.gamepad2.right_bumper) {
            this.clawSlide.claw.closeRight();
        } else if (this.prevGamepad2.right_bumper && !this.gamepad2.right_bumper) {
            this.clawSlide.claw.openRight();
        }
        if (!this.gamepad2.guide) {
            if (this.gamepad2.b) {
                this.clawSlide.claw.setRotate(110);
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
        this.driver.updatePoseEstimate();
        this.drive.shift(this.gamepad1.left_stick_x, this.gamepad1.left_stick_y);
        this.drive.rotate(this.gamepad1.right_stick_x);
        this.drive.updatePowers();

        Pose2d pos = this.driver.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));
        this.telemetry.addLine();
        this.telemetry.addData("SlideRot", this.clawSlide.slideRotate.getLeftPosition());
        this.telemetry.addData("SlideMaxPos", this.clawSlide.slideLift.getMaxPosition());
        this.telemetry.addData("SlideLift", this.clawSlide.slideLift.getLeftPosition());
        this.telemetry.addData("ClawRot", this.clawSlide.claw.getLeftRotAngle());

        this.prevGamepad1.copy(this.gamepad1);
        this.prevGamepad2.copy(this.gamepad2);

        final long endTime = System.nanoTime();
        lastLoopStart = startTime;
        this.telemetry.addData("LOOP DURATION", "%.02fµs", (double)(endTime - startTime) / 1000);
        this.telemetry.addData("LOOP TPS", "%.01f", 1 / loopInterval);
    }
}
