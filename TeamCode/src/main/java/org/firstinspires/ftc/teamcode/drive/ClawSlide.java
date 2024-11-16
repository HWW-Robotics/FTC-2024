package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.action.Action;
import org.firstinspires.ftc.teamcode.action.MotorPairAction;
import org.firstinspires.ftc.teamcode.action.TimedAction;

public class ClawSlide {
    private static final int ROTATE_MAX_POSITION = 790;
    private static final int LIFT_MAX_POSITION = 2750;
    private static final double ROTATE_POWER = 0.5;
    private static final double LIFT_POWER = 1.0;

    public final MotorPair slideRotate, slideLift;
    public final Claw claw;
    private Action action = null;
    private final Action PUT_DOWN_AND_EXTEND_ACTION;

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

        this.PUT_DOWN_AND_EXTEND_ACTION = new Action(
            new TimedAction(1.0) {
                @Override
                public void begin() {
                    super.begin();
                    ClawSlide.this.claw.setRotate(0);
                }
            })
            new MotorPairAction(this.slideLift, 0),
            new MotorPairAction(this.slideRotate, 1000),
            new MotorPairAction(this.slideLift, LIFT_MAX_POSITION),
            new TimedAction(1.0) {
                @Override
                public void begin() {
                    super.begin();
                    ClawSlide.this.claw.setRotate(90);
                }
            });

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

    public void putDownAndExtend() {
        if (this.inAction()) {
            throw new IllegalStateException("Another action is already in progress");
        }
        this.action = this.PUT_DOWN_AND_EXTEND_ACTION;
        this.action.reset();
    }

    public void update() {
        if (this.action != null) {
            this.action.update();
            if (this.action.isDone()) {
                this.action = null;
            }
        }
        this.slideRotate.update();
        this.slideLift.update();
    }
}
