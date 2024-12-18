package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

public final class GlobalStorage {
    private static SampleMecanumDrive driver = null;
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

    public static void destroyDriver() {
        driver = null;
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
