package org.firstinspires.ftc.teamcode.teleop;

import android.content.Context;

import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.WebHandlerManager;
import fi.iki.elonen.NanoHTTPD;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TeleOp(name = "BPathRecorder")
public class BPathRecorder extends AMainTeleOp {
    private static final String RECORDED_PATH_URL = "/recorded_path";

    static final List<RecordNode> nodes = new ArrayList<>();

    SampleMecanumDrive driver;

    @Override
    public void init() {
        super.init();
        this.driver = new SampleMecanumDrive(hardwareMap);

        nodes.clear();
        nodes.add(new PoseNode(this.driver.getPoseEstimate()));
        nodes.add(new PoseNode(this.driver.getPoseEstimate()));
        this.telemetry.addLine("Initialized");
        this.telemetry.update();
    }

    @Override
    public void loop() {
        this.driver.updatePoseEstimate();
        Pose2d pos = this.driver.getPoseEstimate();

        this.telemetry.addData("Nodes", nodes.size());
        this.telemetry.addLine();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));

        Gamepad prevGamepad1 = new Gamepad();
        Gamepad prevGamepad2 = new Gamepad();
        prevGamepad1.copy(this.prevGamepad1);
        prevGamepad2.copy(this.prevGamepad2);
        super.loop();

        recordRotorPos("slideRot", this.clawSlide.slideRotate.getTargetPosition());
        recordRotorPos("slideLift", this.clawSlide.slideLift.getTargetPosition());
        recordRotorPos("clawRot", this.clawSlide.claw.getLeftRotAngle());
        recordRotorPos("clawArmLeft", this.clawSlide.claw.getLeftClawAngle());
        recordRotorPos("clawArmRight", this.clawSlide.claw.getLeftClawAngle());

        RecordNode lastNode = nodes.get(nodes.size() - 1);
        if (!prevGamepad1.a && this.gamepad1.a) {
            nodes.add(new PoseNode(pos));
        } else {
            PoseNode lastPos = lastNode instanceof PoseNode ? (PoseNode) lastNode : null;
            Pose2d last2Pos = getSecondLastPose();
            if (lastPos == null) {
                if (last2Pos == null || isPoseMoved(pos, last2Pos) || isPoseTurned(pos, last2Pos)) {
                    nodes.add(new PoseNode(pos));
                }
            } else if ((isPoseMoved(lastPos.pos, last2Pos) && isPoseTurned(pos, lastPos.pos)) || (isPoseTurned(lastPos.pos, last2Pos) && isPoseMoved(pos, lastPos.pos))) {
                nodes.add(new PoseNode(pos));
            } else {
                lastPos.pos = pos;
            }
        }
        if (!prevGamepad2.a && this.gamepad2.a) {
            nodes.add(new ActionNode("A"));
        }
        if (!prevGamepad2.b && this.gamepad2.b) {
            nodes.add(new ActionNode("B"));
        }
        if (!prevGamepad2.x && this.gamepad2.x) {
            nodes.add(new ActionNode("X"));
        }
        if (!prevGamepad2.y && this.gamepad2.y) {
            nodes.add(new ActionNode("Y"));
        }
    }

    static boolean isPoseMoved(Pose2d a, Pose2d b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) >= 1;
    }

    static boolean isPoseTurned(Pose2d a, Pose2d b) {
        return Math.abs(Math.abs(a.getHeading() - b.getHeading() + Math.PI * 2) % (Math.PI * 2) - Math.PI * 2) > 0.1;
    }

    static void recordRotorPos(String name, double pos) {
        RecordNode lastNode = nodes.get(nodes.size() - 1);
        RotorNode n = lastNode instanceof RotorNode && ((RotorNode) lastNode).rotor.equals(name) ? (RotorNode) lastNode : null;
        double lastPos = getSecondLastRotorPos(name);
        if (Math.abs((n == null ? lastPos : n.pos) - pos) < 0.5) {
            return;
        }
        if (n == null) {
            nodes.add(new RotorNode(name, pos));
        } else {
            n.pos = pos;
        }
    }

    static double getSecondLastRotorPos(String name) {
        for (int i = nodes.size() - 2; i >= 0; i--) {
            RecordNode n = nodes.get(i);
            if (n instanceof RotorNode && ((RotorNode) n).rotor.equals(name)) {
                return ((RotorNode) n).pos;
            }
        }
        return 0;
    }

    static Pose2d getSecondLastPose() {
        for (int i = nodes.size() - 2; i >= 0; i--) {
            RecordNode n = nodes.get(i);
            if (n instanceof PoseNode) {
                return ((PoseNode) n).pos;
            }
        }
        throw new RuntimeException("No second last pose found");
    }

    @WebHandlerRegistrar
    public static void registerWebHandler(Context context, WebHandlerManager manager) {
        manager.register(RECORDED_PATH_URL, session -> {
            StringBuilder builder = new StringBuilder();
            Formatter fmt = new Formatter(builder);
            builder.append("# Length: ").append(nodes.size()).append("\n");
            Map<String, Object> data = new HashMap<>();
            for (int i = 0; i < nodes.size(); i++) {
                RecordNode n = nodes.get(i);
                data.clear();
                n.saveData(data);
                fmt.format("%03d", i + 1);
                builder
                    .append("; ")
                    .append(n.getType())
                    .append(" { ");
                boolean first = true;
                for (Map.Entry<String, Object> kv : data.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder
                        .append(kv.getKey())
                        .append(": ")
                        .append(kv.getValue());
                }
                builder.append(" }\n");
            }
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, builder.toString());
        });
    }
}

abstract class RecordNode {
    abstract String getType();
    abstract void saveData(Map<String, Object> data);
}

class LogNode extends RecordNode {
    final String title;
    final Object dat;
    LogNode(String title, Object dat) { this.title = title; this.dat = dat; }
    @Override
    String getType() { return "Log"; }
    @Override
    void saveData(Map<String, Object> data) {
        data.put("title", title);
        data.put("data", dat);
    }
}

class PoseNode extends RecordNode {
    Pose2d pos;
    PoseNode(Pose2d pos) { this.pos = pos; }
    @Override
    String getType() { return "Pose2d"; }
    @Override
    void saveData(Map<String, Object> data) {
        data.put("x", pos.getX());
        data.put("y", pos.getY());
        data.put("heading", Math.toDegrees(pos.getHeading()));
    }
}

class RotorNode extends RecordNode {
    final String rotor;
    double pos;
    RotorNode(String rotor, double pos) { this.rotor = rotor; this.pos = pos; }
    @Override
    String getType() { return "Rotor"; }
    @Override
    void saveData(Map<String, Object> data) {
        data.put("rotor", rotor);
        data.put("pos", pos);
    }
}

class ActionNode extends RecordNode {
    final String action;
    ActionNode(String action) { this.action = action; }
    @Override
    String getType() { return "Action"; }
    @Override
    void saveData(Map<String, Object> data) {
        data.put("act", action);
    }
}
