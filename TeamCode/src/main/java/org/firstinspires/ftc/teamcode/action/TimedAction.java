package org.firstinspires.ftc.teamcode.action;

import com.qualcomm.robotcore.util.ElapsedTime;

public class TimedAction implements ActionStage {
    protected final ElapsedTime timer = new ElapsedTime();
    private final double time;
    private final Runnable action;

    public TimedAction(double time) {
        this(time, null);
    }

    public TimedAction(double time, Runnable action) {
        this.time = time;
        this.action = action;
    }

    @Override
    public boolean isDone() {
        return this.timer.seconds() >= time;
    }

    @Override
    public void begin() {
        this.timer.reset();
        if (this.action != null) {
            this.action.run();
        }
    }
}
