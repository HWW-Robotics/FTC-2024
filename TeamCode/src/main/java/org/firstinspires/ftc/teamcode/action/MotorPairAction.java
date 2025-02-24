package org.firstinspires.ftc.teamcode.action;

import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.drive.MotorPair;

public class MotorPairAction implements ActionStage {
    private final MotorPair motors;
    private final int targetPos;
    private final int maxDiff;

    public MotorPairAction(MotorPair motors, int targetPos, int maxDiff) {
        this.motors = motors;
        this.targetPos = targetPos;
        this.maxDiff = maxDiff;
    }

    public MotorPairAction(MotorPair motors, int targetPos) {
        this(motors, targetPos, 20);
    }

    @Override
    public boolean isDone() {
        return Math.abs(this.motors.getLeftPosition() - this.targetPos) < this.maxDiff;
    }

    @Override
    public void begin() {}

    @Override
    public void update() {
        GlobalStorage.addData("motor updating", this.targetPos);
        this.motors.setPosition(this.targetPos);
    }

    @Override
    public void after() {
        int pos = this.motors.getLeftPosition();
        if (Math.abs(pos - this.targetPos) >= this.maxDiff) {
            this.motors.setPosition(pos);
        }
    }
}
