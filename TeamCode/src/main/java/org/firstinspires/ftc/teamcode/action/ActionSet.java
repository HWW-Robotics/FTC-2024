package org.firstinspires.ftc.teamcode.action;

public final class ActionSet implements ActionStage {
    private final ActionStage[] actions;

    public ActionSet(ActionStage... actions) {
        this.actions = actions;
    }

    public ActionStage[] getActions() {
        return this.actions;
    }

    @Override
    public boolean isDone() {
        for (ActionStage action : this.actions) {
            if (!action.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void begin() {
        for (ActionStage action : this.actions) {
            action.begin();
        }
    }

    @Override
    public void after() {
        for (ActionStage action : this.actions) {
            action.after();
        }
    }

    @Override
    public void update() {
        for (ActionStage action : this.actions) {
            action.update();
        }
    }
}