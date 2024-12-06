package org.firstinspires.ftc.teamcode.action;

import org.firstinspires.ftc.teamcode.drive.MotorPair;

public class MotorPairAction implements ActionStage {
    private final MotorPair motors;
    private final int targetPos;
    private final int maxDiff = 20;

    public MotorPairAction(MotorPair motors, int targetPos) {
        this.motors = motors;
        this.targetPos = targetPos;
    }

    @Override
    public boolean isDone() {
        return Math.abs(this.motors.getLeftPosition() - this.targetPos) < this.maxDiff;
    }

    @Override
    public void begin() {}

    @Override
    public void update() {
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
