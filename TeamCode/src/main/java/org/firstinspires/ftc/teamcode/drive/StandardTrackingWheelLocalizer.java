package org.firstinspires.ftc.teamcode.drive;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.util.Encoder;

import java.util.Arrays;
import java.util.List;

/*
 * tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    | ___          |
 *    | ---          |
 *    |              |
 *    |              |
 *    |              |
 *    |      ||      |
 *    \--------------/
 *
 */
@Config
public class StandardTrackingWheelLocalizer extends TwoTrackingWheelLocalizer {
    private IMU imu;
    private Encoder leftEncoder, frontEncoder;

    private List<Integer> lastEncPositions, lastEncVels;

    public StandardTrackingWheelLocalizer(HardwareMap hardwareMap, List<Integer> lastTrackingEncPositions, List<Integer> lastTrackingEncVels) {
        super(Arrays.asList(
                new Pose2d(2.2 /*5.6cm*/, -1.5 /*-3.8cm*/, Math.toRadians(90)), // left
                new Pose2d(9.1 /*23.1cm*/, 12.8 /*28.8cm + 3.7cm*/, 0) // front
        ));

        lastEncPositions = lastTrackingEncPositions;
        lastEncVels = lastTrackingEncVels;

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(DriveConstants.getIMUParameters());
        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "leftEncoder"));
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "frontEncoder"));

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        int leftPos = leftEncoder.getCurrentPosition();
        int frontPos = frontEncoder.getCurrentPosition();

        lastEncPositions.clear();
        lastEncPositions.add(leftPos);
        lastEncPositions.add(frontPos);

        return Arrays.asList(
                DriveConstants.encoderTicksToInches(leftPos),
                DriveConstants.encoderTicksToInches(frontPos)
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        int leftVel = (int) leftEncoder.getCorrectedVelocity();
        int frontVel = (int) frontEncoder.getCorrectedVelocity();

        lastEncVels.clear();
        lastEncVels.add(leftVel);
        lastEncVels.add(frontVel);

        return Arrays.asList(
                DriveConstants.encoderTicksToInches(leftVel),
                DriveConstants.encoderTicksToInches(frontVel)
        );
    }

    @Override
    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw();
    }
}
