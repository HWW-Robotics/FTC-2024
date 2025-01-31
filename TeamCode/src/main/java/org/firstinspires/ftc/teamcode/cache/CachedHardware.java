package org.firstinspires.ftc.teamcode.cache;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

public interface CachedHardware {
    void updateInfos();

    static Servo wrapAndAdd(Servo servo, List<? super CachedServo> list) {
        return CachedServo.wrapAndAdd(servo, list);
    }

    static DcMotor wrapAndAdd(DcMotor motor, List<? super CachedMotor> list) {
        return CachedMotor.wrapAndAdd(motor, list);
    }

    static DcMotorEx wrapAndAddEx(DcMotorEx motor, List<? super CachedMotor.CachedMotorEx> list) {
        return CachedMotor.wrapAndAddEx(motor, list);
    }
}
