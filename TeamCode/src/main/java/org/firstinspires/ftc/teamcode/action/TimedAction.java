package org.firstinspires.ftc.teamcode.action;

import com.qualcomm.robotcore.util.ElapsedTime;

public class TimedAction implements ActionStage {
    private final ElapsedTime timer = new ElapsedTime();
    private final double time;
    public TimedAction(double time) {
        this.time = time;
    }

    @Override
    public boolean isDone() {
        return this.timer.seconds() >= time;
    }

    @Override
    public void begin() {
        this.timer.reset();
    }
}
