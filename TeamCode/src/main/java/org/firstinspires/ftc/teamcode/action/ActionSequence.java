package org.firstinspires.ftc.teamcode.action;

public class ActionSequence implements ActionStage {
    private final ActionStage[] stages;
    private int stage = -1;

    public ActionSequence(ActionStage... stages) {
        this.stages = stages;
    }

    public int getStageIndex() {
        return this.stage;
    }

    public boolean isRunning() {
        return this.stage >= 0 && this.stage < this.stages.length;
    }

    @Override
    public boolean isDone() {
        return this.stage == -2 || this.stage >= this.stages.length;
    }

    @Override
    public void begin() {
        if (this.isRunning()) {
            this.stages[this.stage].after();
        }
        this.stage = -1;
    }

    @Override
    public void update() {
        if (this.isDone()) {
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
            if (this.isDone()) {
                return;
            }
            stage = this.stages[this.stage];
            stage.begin();
        }
        stage.update();
    }

    @Override
    public void after() {
        this.stage = -2;
    }
}
