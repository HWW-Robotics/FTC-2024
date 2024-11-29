package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.drive.ClawSlide;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.junit.Test;

public class PathBuildTest {
    @Test
    public void testBasketSidePath() {
        final ClawSlide clawSlide = null;

        TrajectorySequence sequence = new TrajectorySequenceBuilder(
                new Pose2d(),
                SampleMecanumDrive.VEL_CONSTRAINT, SampleMecanumDrive.ACCEL_CONSTRAINT,
                DriveConstants.MAX_ANG_VEL, DriveConstants.MAX_ANG_ACCEL
        )
                .addTemporalMarker(() -> clawSlide.claw.openAll())
                .strafeTo(new Vector2d(-10.35, -16.79))
                .strafeTo(new Vector2d(-14.24, -16.79))
                // begin pick
                .addTemporalMarker(() -> clawSlide.putDown())
                .waitSeconds(5)
                .addTemporalMarker(() -> clawSlide.claw.closeAll())
                .waitSeconds(3)
                .addTemporalMarker(() -> clawSlide.retractAndPullUp())
                .waitSeconds(5)
                // after pick
                .lineToLinearHeading(new Pose2d(-14.0, -16.79, Math.toRadians(138.3)))
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
                })
                .waitSeconds(5)
                .strafeTo(new Vector2d(-1.9, -26.62))
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(195);
                })
                .waitSeconds(3)
                .addTemporalMarker(() -> clawSlide.claw.openAll())
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(0);
                })
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(0);
                })
                .waitSeconds(3)

                .splineToSplineHeading(new Pose2d(-1.4, -26.62, Math.toRadians(2.32)), 0)
                .splineTo(new Vector2d(-12.94, -26.1), 0)
                // begin pick
                .addTemporalMarker(() -> clawSlide.putDown())
                .waitSeconds(5)
                .addTemporalMarker(() -> clawSlide.claw.closeAll())
                .waitSeconds(3)
                .addTemporalMarker(() -> clawSlide.retractAndPullUp())
                .waitSeconds(5)
                // after pick
                .lineToLinearHeading(new Pose2d(-14.24, -16.79, Math.toRadians(138.3)))
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(ClawSlide.LIFT_MAX_POSITION);
                })
                .waitSeconds(5)
                .strafeTo(new Vector2d(-1.9, -26.62))
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(195);
                })
                .waitSeconds(3)
                .addTemporalMarker(() -> clawSlide.claw.openAll())
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.claw.setRotate(0);
                })
                .waitSeconds(2)
                .addTemporalMarker(() -> {
                    clawSlide.slideLift.setPosition(0);
                })
                .waitSeconds(3)
                .build();

    }
}
