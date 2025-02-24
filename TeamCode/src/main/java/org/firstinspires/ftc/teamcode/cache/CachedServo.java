package org.firstinspires.ftc.teamcode.cache;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import java.util.List;

public class CachedServo implements CachedHardware, Servo {
    private final Servo servo;
    private double position = 0;

    private CachedServo(Servo servo) {
        this.servo = servo;
    }

    @Override
    public void updateInfos() {
        //
    }

    @Override
    public ServoController getController() {
        return this.servo.getController();
    }

    @Override
    public int getPortNumber() {
        return this.servo.getPortNumber();
    }

    @Override
    public void setDirection(Direction direction) {
        this.servo.setDirection(direction);
    }

    @Override
    public Direction getDirection() {
        return this.servo.getDirection();
    }

    @Override
    public void setPosition(double position) {
        this.position = position;
        this.servo.setPosition(position);
    }

    @Override
    public double getPosition() {
        return this.position;
    }

    @Override
    public void scaleRange(double min, double max) {
        this.servo.scaleRange(min, max);
    }

    @Override
    public Manufacturer getManufacturer() {
        return this.servo.getManufacturer();
    }

    @Override
    public String getDeviceName() {
        return this.servo.getDeviceName();
    }

    @Override
    public String getConnectionInfo() {
        return this.servo.getConnectionInfo();
    }

    @Override
    public int getVersion() {
        return this.servo.getVersion();
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        this.servo.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void close() {
        this.servo.close();
    }

    public static CachedServo wrap(Servo servo) {
        return new CachedServo(servo);
    }

    public static Servo wrapAndAdd(Servo motor, List<? super CachedServo> list) {
        CachedServo wrapped = wrap(motor);
        list.add(wrapped);
        return wrapped;
    }
}
