package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    private static final double ROT_RATIO = 250.0;
    public static final double MIN_ROT = 2;
    public static final double MAX_ROT = 195;
    public static final double MIN_CLAW = 8;
    public static final double MAX_CLAW = 46;
    private double maxRot = MAX_ROT;
    private final Servo leftRot, rightRot;
    private final Servo leftClaw, rightClaw;

    public Claw(Servo leftRot, Servo rightRot, Servo leftClaw, Servo rightClaw) {
        this.leftRot = leftRot;
        this.rightRot = rightRot;
        this.leftClaw = leftClaw;
        this.rightClaw = rightClaw;
        this.leftRot.setDirection(Servo.Direction.FORWARD);
        this.rightRot.setDirection(Servo.Direction.REVERSE);
        this.leftClaw.setDirection(Servo.Direction.FORWARD);
        this.rightClaw.setDirection(Servo.Direction.REVERSE);
        this.leftRot.scaleRange(0, 1);
        this.rightRot.scaleRange(0, 1);
        this.leftClaw.scaleRange(0, 1);
        this.rightClaw.scaleRange(0, 1);
    }

    public double getMaxRot() {
        return this.maxRot;
    }

    public void setMaxRot(double rot) {
        this.maxRot = Math.min(Math.max(rot, MIN_ROT), MAX_ROT);
    }

    public double getLeftRotAngle() {
        return this.leftRot.getPosition() * ROT_RATIO;
    }

    protected void setLeftRotAngle(double angle) {
        this.leftRot.setPosition(angle / ROT_RATIO);
    }

    public double getRightRotAngle() {
        return this.rightRot.getPosition() * ROT_RATIO;
    }

    protected void setRightRotAngle(double angle) {
        this.rightRot.setPosition(angle / ROT_RATIO);
    }

    public double getLeftClawAngle() {
        return this.leftClaw.getPosition() * ROT_RATIO;
    }

    public void setLeftClawAngle(double angle) {
        this.leftClaw.setPosition(angle / ROT_RATIO);
    }

    public double getRightClawAngle() {
        return this.rightClaw.getPosition() * ROT_RATIO;
    }

    public void setRightClawAngle(double angle) {
        this.rightClaw.setPosition(angle / ROT_RATIO);
    }

    public void closeAll() {
        this.closeLeft();
        this.closeRight();
    }

    public void closeLeft() {
        this.setLeftClawAngle(MIN_CLAW);;
    }

    public void closeRight() {
        this.setRightClawAngle(MIN_CLAW);
    }

    public void openAll() {
        this.openLeft();
        this.openRight();
    }

    public void openLeft() {
        this.setLeftClawAngle(MAX_CLAW);;
    }

    public void openRight() {
        this.setRightClawAngle(MAX_CLAW);;
    }

    public void setRotate(double angle) {
        angle = Math.min(Math.max(angle, MIN_ROT), this.maxRot);
        this.setLeftRotAngle(angle);
        this.setRightRotAngle(angle);
    }

    public void rotate(double angle) {
        this.setRotate(this.getLeftRotAngle() + angle);
    }
}
