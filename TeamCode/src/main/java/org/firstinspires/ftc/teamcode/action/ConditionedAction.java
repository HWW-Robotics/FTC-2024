package org.firstinspires.ftc.teamcode.action;

import java.util.function.BooleanSupplier;

public class ConditionedAction implements ActionStage {
    private final BooleanSupplier condition;
    private final ActionStage action;
    private boolean started = false;

    public ConditionedAction(BooleanSupplier condition, ActionStage action) {
        this.condition = condition;
        this.action = action;
    }

    @Override
    public boolean isDone() {
        return !this.started || this.action.isDone();
    }

    @Override
    public void begin() {
        if (this.condition.getAsBoolean()) {
            this.started = true;
            this.action.begin();
        }
    }

    @Override
    public void update() {
        if (this.started) {
            this.action.update();
        }
    }

    @Override
    public void after() {
        if (this.started) {
            this.action.after();
            this.started = false;
        }
    }
}
