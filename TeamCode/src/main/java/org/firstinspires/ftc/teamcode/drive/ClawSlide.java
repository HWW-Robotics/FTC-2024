package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.action.Action;
import org.firstinspires.ftc.teamcode.action.MotorPairAction;
import org.firstinspires.ftc.teamcode.action.TimedAction;
import org.firstinspires.ftc.teamcode.teleop.AMainTeleOp;

public class ClawSlide {
    private static final double ROTATE_POWER = 0.5;
    private static final double LIFT_POWER = 1.0;
    private static final int ROTATE_MAX_POSITION = 1100;
    private static final double ROTATE_ANGLE_RATIO = 90.0 / 1000;
    private static final int LIFT_MAX_POSITION = 2900;
    private static final int LIFT_MAX_POSITION_HORIZON = 2150;

    private final Action PUT_DOWN_ACTION;
    private final Action RETRACT_AND_PULL_UP_ACTION;

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
            new TimedAction(1.0, () -> ClawSlide.this.claw.setRotate(90)),
            new MotorPairAction(this.slideLift, 0),
            new MotorPairAction(this.slideRotate, 1050));

        this.RETRACT_AND_PULL_UP_ACTION = new Action(
            new TimedAction(1.0, () -> ClawSlide.this.claw.setRotate(90)),
            new MotorPairAction(this.slideLift, 0),
            new MotorPairAction(this.slideRotate, 0));

        this.slideRotate.resetPosition();
        this.slideLift.resetPosition();
        this.claw.closeAll();
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
        final double horizonRatio = Math.sin(this.slideRotate.getLeftPosition() * ROTATE_ANGLE_RATIO * Math.PI / 180);
        int maxPos = LIFT_MAX_POSITION;
        if (horizonRatio > 0) {
            maxPos = Math.min(maxPos, (int)(LIFT_MAX_POSITION_HORIZON / horizonRatio));
        }
        this.slideLift.setMaxPosition(maxPos);
        this.slideRotate.update();
        this.slideLift.update();
    }
}
