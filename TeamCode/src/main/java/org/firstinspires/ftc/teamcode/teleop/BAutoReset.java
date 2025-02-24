package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Configurations;
import org.firstinspires.ftc.teamcode.GlobalStorage;

@TeleOp(name = "BAutoReset")
public class BAutoReset extends OpMode {
    static final int ROT_ADJUST_SPEED = 20;
    static final double ROT_ADJUST_TICK_DUR = 10; // in ms
    static final int ROT_ELASTIC_TICK = 15;

    DcMotor leftRotation, rightRotation, leftSlide, rightSlide;
    boolean adjust = false;
    boolean adjustDone = false;
    ElapsedTime timer = new ElapsedTime();
    ElapsedTime tickTimer = new ElapsedTime();

    @Override
    public void init() {
        GlobalStorage.destroyAll();
        this.leftRotation = hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_ROT);
        this.rightRotation = hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_ROT);
        this.leftSlide = hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_LIFT);
        this.rightSlide = hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_LIFT);
        this.leftRotation.setDirection(DcMotor.Direction.REVERSE);
        this.rightRotation.setDirection(DcMotor.Direction.REVERSE);
        this.leftSlide.setDirection(DcMotor.Direction.FORWARD);
        this.rightSlide.setDirection(DcMotor.Direction.REVERSE);
        this.leftRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.leftRotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.rightRotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.leftSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.rightSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.leftRotation.setPower(0);
        this.rightRotation.setPower(0);
        this.leftSlide.setPower(0);
        this.rightSlide.setPower(0);
    }

    @Override
    public void loop() {
        boolean shouldAdjust = this.gamepad1.left_bumper && this.gamepad1.right_bumper;
        if (this.adjust != shouldAdjust) {
            if (shouldAdjust) {
                if (!this.adjustDone) {
                    this.adjust = true;
                    this.timer.reset();
                    this.leftRotation.setPower(-0.4);
                    this.rightRotation.setPower(-0.4);
                    this.leftSlide.setPower(-0.7);
                    this.rightSlide.setPower(-0.7);
                    this.leftRotation.setTargetPosition(this.leftRotation.getCurrentPosition());
                    this.rightRotation.setTargetPosition(this.rightRotation.getCurrentPosition());
                }
            } else {
                this.adjust = false;
                this.leftRotation.setPower(0);
                this.rightRotation.setPower(0);
                this.leftSlide.setPower(0);
                this.rightSlide.setPower(0);
            }
        }
        if (!this.adjust) {
            if (this.adjustDone) {
                this.telemetry.addLine("All resets are done!");
            } else {
                this.telemetry.addLine("Hold both bumpers on gamepad 1 to start adjust.");
            }
            return;
        }
        this.telemetry.addLine("Adjusting... Keep hold bumpers");
        this.telemetry.addData("Time", this.timer.seconds());

        if (this.tickTimer.milliseconds() > ROT_ADJUST_TICK_DUR) {
            if (!this.adjustDone) {
                boolean leftGood = Math.abs(this.leftRotation.getCurrentPosition() - this.leftRotation.getTargetPosition()) < 4;
                boolean rightGood = Math.abs(this.rightRotation.getCurrentPosition() - this.rightRotation.getTargetPosition()) < 4;
                if (leftGood && rightGood) {
                    this.leftRotation.setTargetPosition(this.leftRotation.getCurrentPosition() - ROT_ADJUST_SPEED);
                    this.leftRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    this.rightRotation.setTargetPosition(this.rightRotation.getCurrentPosition() - ROT_ADJUST_SPEED);
                    this.rightRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    this.tickTimer.reset();
                } else if (this.tickTimer.milliseconds() > 500) {
                    this.leftRotation.setTargetPosition(this.leftRotation.getCurrentPosition() + ROT_ELASTIC_TICK);
                    this.leftRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    this.rightRotation.setTargetPosition(this.rightRotation.getCurrentPosition() + ROT_ELASTIC_TICK);
                    this.rightRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    this.adjustDone = true;
                    this.tickTimer.reset();
                }
            } else if (this.tickTimer.milliseconds() > 1000) {
                this.adjust = false;
                this.leftRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                this.rightRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
        }
        this.telemetry.addData("LeftRot", this.leftRotation.getTargetPosition());
        this.telemetry.addData("RightRot", this.rightRotation.getTargetPosition());
    }
}
