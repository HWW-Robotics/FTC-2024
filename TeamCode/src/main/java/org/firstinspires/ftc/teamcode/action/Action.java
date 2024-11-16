package org.firstinspires.ftc.teamcode.action;

public class Action {
    private final ActionStage[] stages;
    private int stage = -1;

    public Action(ActionStage... stages) {
        this.stages = stages;
    }

    public int getStageIndex() {
        return this.stage;
    }

    public boolean isRunning() {
        return this.stage >= 0 && this.stage < this.stages.length;
    }

    public boolean isDone() {
        return this.stage >= this.stages.length;
    }

    public void reset() {
        if (this.isRunning()) {
            this.stages[this.stage].after();
        }
        this.stage = -1;
    }

    public void update() {
        if (this.stage >= this.stages.length) {
            return;
        }
        if (this.stage == -1) {
            this.stage = 0;
            this.stages[this.stage].begin();
        }
        ActionStage stage = this.stages[this.stage];
        while (stage.isDone()) {
            stage.after();
            this.stage++;
            if (this.stage >= this.stages.length) {
                return;
            }
            stage = this.stages[this.stage];
            stage.begin();
        }
        stage.update();
    }
}
