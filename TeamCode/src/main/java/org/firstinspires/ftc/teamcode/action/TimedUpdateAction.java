package org.firstinspires.ftc.teamcode.action;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.GlobalStorage;

import java.util.function.Consumer;

public class TimedUpdateAction implements ActionStage {
    protected final ElapsedTime timer = new ElapsedTime();
    private final double time;
    private final Consumer<ElapsedTime> action;

    public TimedUpdateAction(double time) {
        this(time, (Consumer<ElapsedTime>)(null));
    }

    public TimedUpdateAction(double time, Runnable action) {
        this(time, action == null ? null : (timer) -> action.run());
    }

    public TimedUpdateAction(double time, Consumer<ElapsedTime> action) {
        this.time = time;
        this.action = action;
    }

    @Override
    public boolean isDone() {
        GlobalStorage.addData("timer updating", time - this.timer.seconds());
        return this.timer.seconds() >= time;
    }

    @Override
    public void begin() {
        this.timer.reset();
    }

    @Override
    public void update() {
        if (this.action != null) {
            this.action.accept(this.timer);
        }
    }
}
