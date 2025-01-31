package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MotorPair {
    public final DcMotor left, right;
    private int minPosition, maxPosition;
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_TO_POSITION;
    private float power;
    private int currentTargetPos;
    private int graduatedVelocity = 0;

    public MotorPair(int minPosition, int maxPosition, float power, DcMotor left, DcMotor right) {
        this(minPosition, maxPosition, power, left, right, DcMotor.Direction.FORWARD, DcMotor.Direction.REVERSE);
    }

    public MotorPair(int minPosition, int maxPosition, float power, DcMotor left, DcMotor right, DcMotor.Direction direction) {
        this(minPosition, maxPosition, power, left, right, direction, direction);
    }

    public MotorPair(int minPosition, int maxPosition, float power, DcMotor left, DcMotor right, DcMotor.Direction leftDirection, DcMotor.Direction rightDirection) {
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
        this.currentTargetPos = this.minPosition;
        this.power = power;
        this.left = left;
        this.right = right;
        this.left.setDirection(leftDirection);
        this.right.setDirection(rightDirection);
        this.left.setPower(0);
        this.right.setPower(0);
        this.left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.left.setTargetPosition(0);
        this.right.setTargetPosition(0);
        this.left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.left.setPower(this.power);
        this.right.setPower(this.power);
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(float power) {
        this.power = Math.min(Math.max(power, -1), 1);
        this.left.setPower(this.power);
        this.right.setPower(this.power);
    }

    public void setMode(DcMotor.RunMode mode) {
        this.mode = mode;
        if (mode != DcMotor.RunMode.RUN_TO_POSITION) {
            this.left.setMode(mode);
            this.right.setMode(mode);
        }
    }

    public void setVelocity(float speed) {
        ((DcMotorEx)(this.left)).setVelocity(speed);
        ((DcMotorEx)(this.right)).setVelocity(speed);
    }

    public void setVelocity(float speed, AngleUnit unit) {
        ((DcMotorEx)(this.left)).setVelocity(speed, unit);
        ((DcMotorEx)(this.right)).setVelocity(speed, unit);
    }

    public int getMinPosition() {
        return this.minPosition;
    }

    public int getMaxPosition() {
        return this.maxPosition;
    }

    public void setMaxPosition(int pos) {
        this.maxPosition = pos;
        if (this.maxPosition < this.currentTargetPos) {
            this.setPosition(this.currentTargetPos);
        }
    }

    public void setGraduatedVelocity(int vel) {
        this.graduatedVelocity = vel;
    }

    public void resetPosition() {
        this.left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.setPosition(this.minPosition);
    }

    public int getTargetPosition() {
        return this.currentTargetPos;
    }

    public void setPosition(int pos) {
        this.currentTargetPos = Math.min(Math.max(pos, this.minPosition), this.maxPosition);
    }

    public void move(int pos) {
        this.setPosition(this.currentTargetPos + pos);
    }

    public int getLeftPosition() {
        return this.left.getCurrentPosition();
    }

    public int getRightPosition() {
        return this.right.getCurrentPosition();
    }

    public void update() {
        if (this.mode != DcMotor.RunMode.RUN_TO_POSITION) {
            return;
        }
        this.left.setTargetPosition(this.currentTargetPos);
        this.right.setTargetPosition(this.currentTargetPos);
        this.left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
