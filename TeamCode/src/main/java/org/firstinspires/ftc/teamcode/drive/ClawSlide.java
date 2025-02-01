package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.action.ActionSequence;
import org.firstinspires.ftc.teamcode.action.ActionSet;
import org.firstinspires.ftc.teamcode.action.ConditionedAction;
import org.firstinspires.ftc.teamcode.action.MotorPairAction;
import org.firstinspires.ftc.teamcode.action.TimedUpdateAction;

public class ClawSlide {
    public static final float ROTATE_POWER = 0.4f;
    public static final float LIFT_POWER1 = 0.8f;
    public static final float LIFT_POWER2 = 0.8f;

    public static final int ROTATE_MAX_POSITION = 1090;
    private static final double ROTATE_ANGLE_RATIO = 90.0 / 990;
    public static final int LIFT_MIN_POSITION = 40;
    public static final int LIFT_MAX_POSITION = 2930;
    private static final int LIFT_MAX_POSITION_HORIZON = 2150;

    private static final double ROTATE_JOINT_HEIGHT = 9.7;
    private static final double LIFT_MIN_POSITION_LENGTH = 30.6;
    private static final double LIFT_MAX_POSITION_LENGTH = 96.5;
    private static final double LIFT_MAX_HORIZON_LENGTH = LIFT_MIN_POSITION_LENGTH + 48.8;
    private static final double LIFT_POSITION_LENGTH_RATIO = (LIFT_MAX_POSITION_LENGTH - LIFT_MIN_POSITION_LENGTH) / LIFT_MAX_POSITION;
    private static final double CLAW_ROT_OFFSET = 70;
    private static final double CLAW_ARM_LENGTH = 18.0;

    private final ActionSequence
        PUT_DOWN_ACTION,
        PUT_DOWN_AND_EXTEND_ACTION,
        RETRACT_AND_PULL_UP_ACTION,
        PUT_DOWN_FOR_HANG_ACTION;

    public final MotorPair slideRotate, slideLift;
    public final Claw claw;
    private ActionSequence action = null;
    private boolean restricted = true;

    public ClawSlide(
        DcMotor leftRotation, DcMotor rightRotation, DcMotor leftSlide, DcMotor rightSlide,
        Servo clawRotLeft, Servo clawRotRight, Servo clawArmLeft, Servo clawArmRight) {
        this.slideRotate = new MotorPair(
        0,
            ROTATE_MAX_POSITION,
            ROTATE_POWER,
            leftRotation,
            rightRotation,
            DcMotor.Direction.REVERSE);
        this.slideLift = new MotorPair(
            LIFT_MIN_POSITION,
            LIFT_MAX_POSITION,
            LIFT_POWER1,
            leftSlide,
            rightSlide);
        this.claw = new Claw(
            clawRotLeft,
            clawRotRight,
            clawArmLeft,
            clawArmRight);

        this.PUT_DOWN_ACTION = new ActionSequence(
            new TimedUpdateAction(0.1, () -> this.claw.setRotate(20)),
            new MotorPairAction(this.slideLift, LIFT_MIN_POSITION),
            new MotorPairAction(this.slideRotate, 938),
            new TimedUpdateAction(0.3, () -> this.claw.setRotate(113)));

        this.PUT_DOWN_AND_EXTEND_ACTION = new ActionSequence(
            new TimedUpdateAction(0.1, () -> this.claw.setRotate(20)),
            new MotorPairAction(this.slideLift, LIFT_MIN_POSITION),
            new MotorPairAction(this.slideRotate, 945),
            new MotorPairAction(this.slideLift, 1125),
            new TimedUpdateAction(0.3, () -> this.claw.setRotate(102)));

        this.RETRACT_AND_PULL_UP_ACTION = new ActionSequence(
            new TimedUpdateAction(0.2, () -> this.claw.setRotate(105)),
            new MotorPairAction(this.slideLift, LIFT_MIN_POSITION),
            new ConditionedAction(
                () -> this.slideRotate.getLeftPosition() > 600,
                new ActionSequence(
                    new MotorPairAction(this.slideRotate, 600),
                    new TimedUpdateAction(0.08, () -> this.claw.setRotate(Claw.MAX_ROT))
                )
            ),
            new MotorPairAction(this.slideRotate, 0)
        );

        this.PUT_DOWN_FOR_HANG_ACTION = new ActionSequence(
            new TimedUpdateAction(0.1, () -> this.claw.setRotate(Claw.MAX_ROT)),
            new MotorPairAction(this.slideLift, LIFT_MIN_POSITION),
            new MotorPairAction(this.slideRotate, 458)
            // new MotorPairAction(this.slideLift, 1000)
        );

        this.slideRotate.resetPosition();
        this.slideLift.resetPosition();
        this.slideRotate.setGraduatedVelocity(60);
        this.slideLift.setGraduatedVelocity(200);
    }

    public boolean inAction() {
        return this.action != null;
    }

    public void cancelAction() {
        if (this.action == null) {
            return;
        }
        this.action.begin();
        this.action = null;
    }

    private void setAction(ActionSequence action) {
        if (this.inAction()) {
            this.cancelAction();
        }
        this.action = action;
        this.action.begin();
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

    public void putDownForHang() {
        this.setAction(this.PUT_DOWN_FOR_HANG_ACTION);
    }

    public void releaseRestrictions() {
        this.restricted = false;
        this.slideLift.setPower(LIFT_POWER1);
    }

    public void setRestrictions() {
        this.restricted = true;
        this.slideLift.setPower(LIFT_POWER2);
    }

    public boolean getRestricted() {
        return this.restricted;
    }

    public void update() {
        if (this.action != null) {
            this.action.update();
            if (this.action.isDone()) {
                this.action = null;
            }
        }
        GlobalStorage.addData("Position Restriction", this.restricted);
        if (this.restricted) {
            double maxClawAngle = this.getMaxSafeClawAngle();
            double maxSlideAngle = this.getMaxSafeSlideAngle();
            double maxSlidePos = this.getMaxSafeSlidePos() - LIFT_MIN_POSITION_LENGTH;
            // GlobalStorage.addData("D: Max Claw Rot:", maxClawAngle);
            // GlobalStorage.addData("D: Max Slide Rot:", maxSlideAngle);
            // GlobalStorage.addData("D: Max Slide Pos:", maxSlidePos);
            // this.claw.setMaxRot(maxClawAngle - CLAW_ROT_OFFSET);
            // this.slideRotate.setMaxPosition((int)(maxSlideAngle / ROTATE_ANGLE_RATIO));
            // this.slideLift.setMaxPosition((int)(maxSlidePos / LIFT_POSITION_LENGTH_RATIO));

            final double horizonRatio = Math.sin(Math.toRadians(this.slideRotate.getLeftPosition() * ROTATE_ANGLE_RATIO));
            int maxPos = LIFT_MAX_POSITION;
            if (horizonRatio > 0) {
                maxPos = Math.min(maxPos, (int) (LIFT_MAX_POSITION_HORIZON / horizonRatio));
            }
            this.slideLift.setMaxPosition(maxPos);
        } else {
            this.claw.setMaxRot(Claw.MAX_ROT);
            this.slideRotate.setMaxPosition(2000);
            final double horizonRatio = Math.sin(Math.toRadians(this.slideRotate.getLeftPosition() * ROTATE_ANGLE_RATIO));
            int maxPos = LIFT_MAX_POSITION;
            if (horizonRatio > 0) {
                maxPos = Math.min(maxPos, (int) (LIFT_MAX_POSITION_HORIZON / horizonRatio));
            }
            this.slideLift.setMaxPosition(maxPos);
        }

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
        maxPos = min(maxPos, longMax);
        if (virtualAngle < 0 && slideAngle > Math.PI / 2) {
            double shortMax = ROTATE_JOINT_HEIGHT / -Math.cos(slideAngle);
            maxPos = min(maxPos, shortMax);
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
        double maxSlideAngle = min(shortMax, longMax);
        return Double.isNaN(maxSlideAngle) ? ROTATE_MAX_POSITION * ROTATE_ANGLE_RATIO : maxSlideAngle;
    }

    private double getMaxSafeClawAngle() {
        double slideAngle = this.getSlideAngle();
        double slideLength = this.getSlideLength();
        double maxAngle = 180 + Math.toDegrees(Math.acos((ROTATE_JOINT_HEIGHT + slideLength * Math.cos(slideAngle)) / CLAW_ARM_LENGTH)) - slideAngle;
        return Double.isNaN(maxAngle) ? Claw.MAX_ROT + CLAW_ROT_OFFSET : maxAngle;
    }

    private static double min(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        return a > b ? b : a;
    }

    private static double max(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        return a < b ? b : a;
    }
}
