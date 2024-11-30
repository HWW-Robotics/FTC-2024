package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.action.Action;
import org.firstinspires.ftc.teamcode.action.MotorPairAction;
import org.firstinspires.ftc.teamcode.action.TimedAction;
import org.firstinspires.ftc.teamcode.teleop.AMainTeleOp;

public class ClawSlide {
    private static final double ROTATE_POWER = 0.3;
    private static final double LIFT_POWER = 1.0;

    public static final int ROTATE_MAX_POSITION = 1090;
    private static final double ROTATE_ANGLE_RATIO = 90.0 / 980;
    public static final int LIFT_MAX_POSITION = 2900;
    private static final int LIFT_MAX_POSITION_HORIZON = 2150;

    private static final double ROTATE_JOINT_HEIGHT = 9.7;
    private static final double LIFT_MIN_POSITION_LENGTH = 30.6;
    private static final double LIFT_MAX_POSITION_LENGTH = 96.5;
    private static final double LIFT_MAX_HORIZON_LENGTH = LIFT_MIN_POSITION_LENGTH + 48.8;
    private static final double LIFT_POSITION_LENGTH_RATIO = (LIFT_MAX_POSITION_LENGTH - LIFT_MIN_POSITION_LENGTH) / LIFT_MAX_POSITION;
    private static final double CLAW_ROT_OFFSET = 70;
    private static final double CLAW_ARM_LENGTH = 18.0;

    private final Action
        PUT_DOWN_ACTION,
        PUT_DOWN_AND_EXTEND_ACTION,
        RETRACT_AND_PULL_UP_ACTION;

    public final MotorPair slideRotate, slideLift;
    public final Claw claw;
    private Action action = null;

    public ClawSlide(
        DcMotor leftRotation, DcMotor rightRotation, DcMotor leftSlide, DcMotor rightSlide,
        Servo clawRotLeft, Servo clawRotRight, Servo clawArmLeft, Servo clawArmRight) {
        this.slideRotate = new MotorPair(
                ROTATE_MAX_POSITION,
                ROTATE_POWER,
                leftRotation,
                rightRotation,
                DcMotor.Direction.REVERSE);
        this.slideLift = new MotorPair(
                LIFT_MAX_POSITION,
                LIFT_POWER,
                leftSlide,
                rightSlide);
        this.claw = new Claw(
            clawRotLeft,
            clawRotRight,
            clawArmLeft,
            clawArmRight);

        this.PUT_DOWN_ACTION = new Action(
            new TimedAction(0.2, () -> this.claw.setRotate(20)),
            new MotorPairAction(this.slideLift, 0),
            new MotorPairAction(this.slideRotate, 940),
            new TimedAction(0.5, () -> this.claw.setRotate(105)));

        this.PUT_DOWN_AND_EXTEND_ACTION = new Action(
            new TimedAction(0.2, () -> this.claw.setRotate(20)),
            new MotorPairAction(this.slideLift, 0),
            new MotorPairAction(this.slideRotate, 945),
            new MotorPairAction(this.slideLift, 1125),
            new TimedAction(0.5, () -> this.claw.setRotate(102)));

        this.RETRACT_AND_PULL_UP_ACTION = new Action(
            new TimedAction(0.5, () -> this.claw.setRotate(20)),
            new MotorPairAction(this.slideLift, 0),
            new MotorPairAction(this.slideRotate, 0),
            new TimedAction(0.5, () -> this.claw.setRotate(111)));

        this.slideRotate.resetPosition();
        this.slideLift.resetPosition();
    }

    public boolean inAction() {
        return this.action != null;
    }

    public void cancelAction() {
        if (this.action == null) {
            return;
        }
        this.action.reset();
        this.action = null;
    }

    private void setAction(Action action) {
        if (this.inAction()) {
            this.cancelAction();
        }
        this.action = action;
        this.action.reset();
    }

    public void putDown() {
        this.setAction(this.PUT_DOWN_ACTION);
    }

    public void putDownAndExtend() {
        this.setAction(this.PUT_DOWN_AND_EXTEND_ACTION);
    }

    public void retractAndPullUp() {
        this.setAction(this.RETRACT_AND_PULL_UP_ACTION);
    }

    public void update() {
        if (this.action != null) {
            this.action.update();
            if (this.action.isDone()) {
                this.action = null;
            }
        }
        double maxClawAngle = this.getMaxSafeClawAngle();
        double maxSlideAngle = this.getMaxSafeSlideAngle();
        double maxSlidePos = this.getMaxSafeSlidePos() - LIFT_MIN_POSITION_LENGTH;
        AMainTeleOp.addLog("Max Claw Rot:", maxClawAngle);
        AMainTeleOp.addLog("Max Slide Rot:", maxSlideAngle);
        AMainTeleOp.addLog("Max Slide Pos:", maxSlidePos);
        // this.claw.setMaxRot(maxClawAngle - CLAW_ROT_OFFSET);
        // this.slideRotate.setMaxPosition((int)(maxSlideAngle / ROTATE_ANGLE_RATIO));
        // this.slideLift.setMaxPosition((int)(maxSlidePos / LIFT_POSITION_LENGTH_RATIO));

        final double horizonRatio = Math.sin(this.slideRotate.getLeftPosition() * ROTATE_ANGLE_RATIO * Math.PI / 180);
        int maxPos = LIFT_MAX_POSITION;
        if (horizonRatio > 0) {
            maxPos = Math.min(maxPos, (int)(LIFT_MAX_POSITION_HORIZON / horizonRatio));
        }
        this.slideLift.setMaxPosition(maxPos);

        this.slideRotate.update();
        this.slideLift.update();
    }

    public double getClawAngle() {
        return CLAW_ROT_OFFSET + this.claw.getLeftClawAngle();
    }

    public double getSlideAngle() {
        return this.slideRotate.getLeftPosition();
    }

    public double getSlideLength() {
        return LIFT_MIN_POSITION_LENGTH + this.slideLift.getLeftPosition() * LIFT_POSITION_LENGTH_RATIO;
    }

    private double getMaxSafeSlidePos() {
        double slideAngle = Math.toRadians(this.getSlideAngle());
        double clawAngle = Math.toRadians(this.getClawAngle());
        double slideLength = this.getSlideLength(); // TODO: should not depends on old stat?
        double b = CLAW_ARM_LENGTH * Math.cos(clawAngle);
        double virtualArm = slideLength * slideLength + CLAW_ARM_LENGTH * CLAW_ARM_LENGTH - 2 * slideLength * b;
        double virtualAngle = -Math.asin(CLAW_ARM_LENGTH * Math.sin(clawAngle) / virtualArm);
        double c = ROTATE_JOINT_HEIGHT / -Math.cos(slideAngle + virtualAngle);
        double horizonRatio = Math.sin(slideAngle);
        double maxPos = horizonRatio > 0 ? LIFT_MAX_HORIZON_LENGTH / horizonRatio : LIFT_MAX_POSITION_LENGTH;
        double longMax = b + Math.sqrt(b * b - CLAW_ARM_LENGTH * CLAW_ARM_LENGTH + c * c);
        maxPos = Math.min(maxPos, longMax);
        if (virtualAngle < 0 && slideAngle > Math.PI / 2) {
            double shortMax = ROTATE_JOINT_HEIGHT / -Math.cos(slideAngle);
            if (shortMax < longMax) {
                maxPos = Math.min(maxPos, shortMax);
            }
        }
        return maxPos;
    }

    private double getMaxSafeSlideAngle() {
        double clawAngle = Math.toRadians(this.getClawAngle());
        double slideLength = this.getSlideLength();
        double virtualArm = slideLength * slideLength + CLAW_ARM_LENGTH * CLAW_ARM_LENGTH - 2 * slideLength * CLAW_ARM_LENGTH * Math.cos(clawAngle);
        double virtualAngle = -Math.asin(CLAW_ARM_LENGTH * Math.sin(clawAngle) / virtualArm);
        double longMax = Math.toDegrees(Math.acos(-ROTATE_JOINT_HEIGHT / virtualArm) - virtualAngle);
        double shortMax = Math.toDegrees(Math.acos(-ROTATE_JOINT_HEIGHT / slideLength));
        return Math.min(shortMax, longMax);
    }

    private double getMaxSafeClawAngle() {
        double slideAngle = this.getSlideAngle();
        double slideLength = this.getSlideLength();
        return 180 + Math.toDegrees(Math.acos((ROTATE_JOINT_HEIGHT + slideLength * Math.cos(slideAngle)) / CLAW_ARM_LENGTH)) - slideAngle;
    }
}
