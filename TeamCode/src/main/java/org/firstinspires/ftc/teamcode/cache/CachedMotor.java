package org.firstinspires.ftc.teamcode.cache;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.List;

public class CachedMotor implements CachedHardware, DcMotor {
    private final DcMotor motor;
    private MotorConfigurationType motorType = null;
    private int targetPosition = 0;
    private int currentPosition = 0;
    private double power = 0;

    private CachedMotor(DcMotor motor) {
        this.motor = motor;
    }

    @Override
    public void updateInfos() {
        this.currentPosition = this.motor.getCurrentPosition();
        this.power = this.motor.getPower();
    }

    @Override
    public MotorConfigurationType getMotorType() {
        if (this.motorType == null) {
            this.motorType = this.motor.getMotorType();
        }
        return this.motorType;
    }

    @Override
    public void setMotorType(MotorConfigurationType motorType) {
        this.motorType = motorType;
        this.motor.setMotorType(motorType);
    }

    @Override
    public DcMotorController getController() {
        return this.motor.getController();
    }

    @Override
    public int getPortNumber() {
        return this.motor.getPortNumber();
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        this.motor.setZeroPowerBehavior(zeroPowerBehavior);
    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return this.motor.getZeroPowerBehavior();
    }

    @Override
    @SuppressWarnings("deprecated")
    public void setPowerFloat() {
        this.motor.setPowerFloat();
    }

    @Override
    public boolean getPowerFloat() {
        return this.motor.getPowerFloat();
    }

    @Override
    public void setTargetPosition(int position) {
        this.targetPosition = position;
        this.motor.setTargetPosition(position);
    }

    @Override
    public int getTargetPosition() {
        return this.targetPosition;
    }

    @Override
    public boolean isBusy() {
        return this.motor.isBusy();
    }

    @Override
    public int getCurrentPosition() {
        return this.currentPosition;
    }

    @Override
    public void setMode(RunMode mode) {
        this.motor.setMode(mode);
    }

    @Override
    public RunMode getMode() {
        return this.motor.getMode();
    }

    @Override
    public void setDirection(Direction direction) {
        this.motor.setDirection(direction);
    }

    @Override
    public Direction getDirection() {
        return this.motor.getDirection();
    }

    @Override
    public void setPower(double power) {
        this.power = power;
        this.motor.setPower(power);
    }

    @Override
    public double getPower() {
        return this.power;
    }

    @Override
    public Manufacturer getManufacturer() {
        return this.motor.getManufacturer();
    }

    @Override
    public String getDeviceName() {
        return this.motor.getDeviceName();
    }

    @Override
    public String getConnectionInfo() {
        return this.motor.getConnectionInfo();
    }

    @Override
    public int getVersion() {
        return this.motor.getVersion();
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        this.motor.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void close() {
        this.motor.close();
    }

    public static class CachedMotorEx extends CachedMotor implements DcMotorEx {
        private final DcMotorEx motor;
        private boolean enabled = true;
        private double velocity = 0;

        private CachedMotorEx(DcMotorEx motor) {
            super(motor);
            this.motor = motor;
        }

        @Override
        public void updateInfos() {
            // this.enabled = this.motor.isMotorEnabled();
            this.velocity = this.motor.getVelocity();
        }

        @Override
        public void setMotorEnable() {
            this.enabled = true;
            this.motor.setMotorEnable();
        }

        @Override
        public void setMotorDisable() {
            this.enabled = false;
            this.motor.setMotorDisable();
        }

        @Override
        public boolean isMotorEnabled() {
            return this.enabled;
        }

        @Override
        public void setVelocity(double angularRate) {
            this.motor.setVelocity(angularRate);
        }

        @Override
        public void setVelocity(double angularRate, AngleUnit unit) {
            this.motor.setVelocity(angularRate, unit);
        }

        @Override
        public double getVelocity() {
            return this.velocity;
        }

        @Override
        public double getVelocity(AngleUnit unit) {
            return this.motor.getVelocity(unit);
        }

        @Override
        public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) {
            this.motor.setPIDCoefficients(mode, pidCoefficients);
        }

        @Override
        public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) throws UnsupportedOperationException {
            this.motor.setPIDFCoefficients(mode, pidfCoefficients);
        }

        @Override
        public void setVelocityPIDFCoefficients(double p, double i, double d, double f) {
            this.motor.setVelocityPIDFCoefficients(p, i, d, f);
        }

        @Override
        public void setPositionPIDFCoefficients(double p) {
            this.motor.setPositionPIDFCoefficients(p);
        }

        @Override
        public PIDCoefficients getPIDCoefficients(RunMode mode) {
            return this.motor.getPIDCoefficients(mode);
        }

        @Override
        public PIDFCoefficients getPIDFCoefficients(RunMode mode) {
            return this.motor.getPIDFCoefficients(mode);
        }

        @Override
        public void setTargetPositionTolerance(int tolerance) {
            this.motor.setTargetPositionTolerance(tolerance);
        }

        @Override
        public int getTargetPositionTolerance() {
            return this.motor.getTargetPositionTolerance();
        }

        @Override
        public double getCurrent(CurrentUnit unit) {
            return this.motor.getCurrent(unit);
        }

        @Override
        public double getCurrentAlert(CurrentUnit unit) {
            return this.motor.getCurrentAlert(unit);
        }

        @Override
        public void setCurrentAlert(double current, CurrentUnit unit) {
            this.motor.setCurrentAlert(current, unit);
        }

        @Override
        public boolean isOverCurrent() {
            return this.motor.isOverCurrent();
        }
    }

    public static CachedMotor wrap(DcMotor motor) {
        if (motor instanceof DcMotorEx) {
            return new CachedMotorEx((DcMotorEx)(motor));
        }
        return new CachedMotor(motor);
    }

    public static CachedMotorEx wrap(DcMotorEx motor) {
        return new CachedMotorEx(motor);
    }

    public static DcMotor wrapAndAdd(DcMotor motor, List<? super CachedMotor> list) {
        CachedMotor wrapped = wrap(motor);
        list.add(wrapped);
        return wrapped;
    }

    public static DcMotorEx wrapAndAddEx(DcMotorEx motor, List<? super CachedMotorEx> list) {
        CachedMotorEx wrapped = wrap(motor);
        list.add(wrapped);
        return wrapped;
    }
}
