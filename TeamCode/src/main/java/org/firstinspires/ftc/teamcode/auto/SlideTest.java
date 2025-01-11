package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Configurations;
import org.firstinspires.ftc.teamcode.GlobalStorage;
import org.firstinspires.ftc.teamcode.drive.ClawSlide;

@Autonomous(name = "SlideTest")
public class SlideTest extends OpMode {
    ClawSlide clawSlide;
    ElapsedTime timer = new ElapsedTime();
    int maxDiff = 0;

    @Override
    public void init() {
        GlobalStorage.onInit(this);
        this.clawSlide = new ClawSlide(
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

    @Override
    public void start() {
        this.timer.reset();
        this.clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
    }

    @Override
    public void loop() {
        if (this.timer.seconds() > 3) {
            this.timer.reset();
            this.clawSlide.slideLift.setPosition(this.clawSlide.slideLift.getTargetPosition() == 0 ? ClawSlide.LIFT_MAX_POSITION : 0);
        }
        this.clawSlide.update();

        final int left = this.clawSlide.slideLift.getLeftPosition();
        final int right = this.clawSlide.slideLift.getRightPosition();
        final int diff = Math.abs(right - left);
        if (diff > maxDiff) {
            maxDiff = diff;
        }
        this.telemetry.addData("LeftSlide", left);
        this.telemetry.addData("RightSlide", right);
        this.telemetry.addData("Diff", diff);
        this.telemetry.addData("Max Diff", maxDiff);
    }
}
