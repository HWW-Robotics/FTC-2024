package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

public final class GlobalStorage {
    private static SampleMecanumDrive driver = null;
    private static ClawSlide clawSlide = null;
    private static Telemetry telemetry = null;

    private GlobalStorage() {}

    public static void onInit(OpMode opMode) {
        telemetry = opMode.telemetry;
    }

    public static SampleMecanumDrive getOrCreateDriver(HardwareMap hardwareMap) {
        if (driver == null) {
            driver = new SampleMecanumDrive(hardwareMap);
        }
        return driver;
    }

    public static ClawSlide getOrCreateClawSlide(HardwareMap hardwareMap) {
        // if (clawSlide == null) {
            clawSlide = makeClawSlide(hardwareMap);
        // }
        return clawSlide;
    }

    public static void destroyAll() {
        driver = null;
    }

    private static ClawSlide makeClawSlide(HardwareMap hardwareMap) {
        return new ClawSlide(
            hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_ROT),
            hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_ROT),
            hardwareMap.get(DcMotor.class, Configurations.LEFT_SLIDE_LIFT),
            hardwareMap.get(DcMotor.class, Configurations.RIGHT_SLIDE_LIFT),
            hardwareMap.get(Servo.class, Configurations.LEFT_CLAW_ROT),
            hardwareMap.get(Servo.class, Configurations.RIGHT_CLAW_ROT),
            hardwareMap.get(Servo.class, Configurations.LEFT_CLAW_ARM),
            hardwareMap.get(Servo.class, Configurations.RIGHT_CLAW_ARM)
        );
    }

    public static void addData(String cap, Object data) {
        if (telemetry != null) {
            telemetry.addData(cap, data);
        }
    }

    public static void addLine(String line) {
        if (telemetry != null) {
            telemetry.addLine(line);
        }
    }
}
