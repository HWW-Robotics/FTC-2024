package org.firstinspires.ftc.teamcode.cache;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

public interface CachedHardware {
    void updateInfos();

    static Servo wrapAndAdd(Servo servo, List<CachedHardware> list) {
        return CachedServo.wrapAndAdd(servo, list);
    }

    static <T extends DcMotor> T wrapAndAdd(T motor, List<CachedHardware> list) {
        return CachedMotor.<T>wrapAndAdd(motor, list);
    }
}
