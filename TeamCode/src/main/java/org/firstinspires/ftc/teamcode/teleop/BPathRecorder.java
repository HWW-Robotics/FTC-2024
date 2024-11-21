package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.StandardTrackingWheelLocalizer;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "BPathRecorder")
public class BPathRecorder extends OpMode {
    StandardTrackingWheelLocalizer localizer;
    List<Integer> lastTrackingEncPositions = new ArrayList<>(3);
    List<Integer> lastTrackingEncVels = new ArrayList<>(3);

    @Override
    public void init() {
        this.localizer = new StandardTrackingWheelLocalizer(this.hardwareMap, this.lastTrackingEncPositions, this.lastTrackingEncVels);
        this.localizer.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(0)));
    }

    @Override
    public void loop() {
        this.localizer.update();
        Pose2d pose = this.localizer.getPoseEstimate();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pose.getX(), pose.getY());
        this.telemetry.addData("Heading", pose.getHeading());
    }
}
