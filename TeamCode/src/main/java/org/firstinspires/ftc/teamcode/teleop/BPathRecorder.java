package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.drive.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "BPathRecorder")
public class BPathRecorder extends OpMode {
    static final double MAX_DRIVE_POWER = 0.5;

    MecanumDrive drive;
    GoBildaPinpointDriver odo;

    @Override
    public void init() {
        this.drive = new MecanumDrive(
                MAX_DRIVE_POWER,
                hardwareMap.get(DcMotor.class, "rightFront"),
                hardwareMap.get(DcMotor.class, "rightRear"),
                hardwareMap.get(DcMotor.class, "leftFront"),
                hardwareMap.get(DcMotor.class, "leftRear"));
        this.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        this.odo.setOffsets(255, -105);
        this.odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        this.odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        this.odo.resetPosAndIMU();
        this.telemetry.addData("Status", "Initialized");
        this.telemetry.addData("X offset", odo.getXOffset());
        this.telemetry.addData("Y offset", odo.getYOffset());
        this.telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        this.telemetry.addData("Device Scalar", odo.getYawScalar());
        this.telemetry.update();
    }

    @Override
    public void loop() {
        this.odo.update();
        if (gamepad1.a){
            odo.resetPosAndIMU(); //resets the position to 0 and recalibrates the IMU
        }
        if (gamepad1.b){
            odo.recalibrateIMU(); //recalibrates the IMU without resetting position
        }

        Pose2D pos = this.odo.getPosition();
        Pose2D vel = this.odo.getVelocity();
        this.drive.shift(this.gamepad1.left_stick_x, this.gamepad1.left_stick_y);
        this.drive.rotate(this.gamepad1.right_stick_x * 0.5);
        this.drive.updatePowers();

        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM));
        this.telemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
        this.telemetry.addData("Vel", "%+03.02f, %+03.02f", vel.getX(DistanceUnit.MM), vel.getY(DistanceUnit.MM));
        this.telemetry.addData("V-Heading", vel.getHeading(AngleUnit.DEGREES));
        this.telemetry.addData("Status", this.odo.getDeviceStatus());
        this.telemetry.update();
    }
}
