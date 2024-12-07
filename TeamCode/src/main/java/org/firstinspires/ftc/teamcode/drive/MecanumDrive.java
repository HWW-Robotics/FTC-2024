package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MecanumDrive {
    private static final double POWER_RATIO = DriveConstants.inchesToEncoderTicks(120);
    private final DcMotor rightFront, rightRear, leftFront, leftRear;
    private double maxPower;
    private double rightFrontPower, rightRearPower, leftFrontPower, leftRearPower, powerBase;

    public MecanumDrive(double maxPower, DcMotor rightFront, DcMotor rightRear, DcMotor leftFront, DcMotor leftRear) {
        this.maxPower = maxPower;
        this.rightFront = rightFront;
        this.rightRear = rightRear;
        this.leftFront = leftFront;
        this.leftRear = leftRear;
        this.rightFront.setDirection(DcMotor.Direction.FORWARD);
        this.rightRear.setDirection(DcMotor.Direction.FORWARD);
        this.leftFront.setDirection(DcMotor.Direction.REVERSE);
        this.leftRear.setDirection(DcMotor.Direction.REVERSE);
        this.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public double getMaxPower() {
        return this.maxPower;
    }

    public void setMaxPower(double power) {
        this.maxPower = power;
    }

    public void addPowers(double rightFront, double rightRear, double leftFront, double leftRear) {
        if (rightFront == 0 && rightRear == 0 && leftFront == 0 && leftRear == 0) {
            return;
        }
        this.rightFrontPower += rightFront;
        this.rightRearPower += rightRear;
        this.leftFrontPower += leftFront;
        this.leftRearPower += leftRear;
        this.powerBase += 1;
    }

    public void updatePowers() {
        if (this.powerBase <= 0) {
            this.rightFront.setPower(0);
            this.rightRear.setPower(0);
            this.leftFront.setPower(0);
            this.leftRear.setPower(0);
            return;
        }
        final double powerFactor = this.maxPower / this.powerBase;
        double
            rfPower = this.rightFrontPower * powerFactor,
            rrPower = this.rightRearPower * powerFactor,
            lfPower = this.leftFrontPower * powerFactor,
            lrPower = this.leftRearPower * powerFactor;
        if (this.rightFront instanceof DcMotorEx && this.rightRear instanceof DcMotorEx && this.leftFront instanceof DcMotorEx && this.leftRear instanceof DcMotorEx) {
            ((DcMotorEx) this.rightFront).setVelocity(rfPower * POWER_RATIO);
            ((DcMotorEx) this.rightRear).setVelocity(rrPower * POWER_RATIO);
            ((DcMotorEx) this.leftFront).setVelocity(lfPower * POWER_RATIO);
            ((DcMotorEx) this.leftRear).setVelocity(lrPower * POWER_RATIO);
        } else {
            this.rightFront.setPower(rfPower);
            this.rightRear.setPower(rrPower);
            this.leftFront.setPower(lfPower);
            this.leftRear.setPower(lrPower);
        }
        this.rightFrontPower = 0;
        this.rightRearPower = 0;
        this.leftFrontPower = 0;
        this.leftRearPower = 0;
        this.powerBase = 0;
    }

    /**
     * @param x left-right shift
     * @param y forward-back shift
     */
    public void shift(double x, double y) {
        double power = Math.sqrt(x * x + y * y);
        if (power == 0) {
            return;
        }
        double yaw = Math.atan2(-y, x) - Math.PI / 4;
        double xPower = Math.sin(yaw) * power;
        double yPower = Math.cos(yaw) * power;
        this.addPowers(yPower, xPower, xPower, yPower);
    }

    /**
     * @param speed the speed to rotate clockwise
     */
    public void rotate(double speed) {
        if (speed == 0) {
            return;
        }
        this.addPowers(-speed, -speed, speed, speed);
    }
}
