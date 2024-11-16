package org.firstinspires.ftc.teamcode.action;

public interface ActionStage {
    boolean isDone();
    void begin();
    default void after() {}
    default void update() {}
}
