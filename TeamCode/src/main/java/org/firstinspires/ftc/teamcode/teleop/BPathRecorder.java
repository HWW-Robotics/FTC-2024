package org.firstinspires.ftc.teamcode.teleop;

import android.content.Context;

import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.WebHandlerManager;
import fi.iki.elonen.NanoHTTPD;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TeleOp(name = "BPathRecorder")
public class BPathRecorder extends AMainTeleOp {
    private static final String RECORDED_PATH_URL = "/recorded_path";

    static final List<RecordNode> nodes = new ArrayList<>();

    @Override
    public void init() {
        super.init();
        this.driver.setPoseEstimate(new Pose2d(0, 0, 0));

        nodes.clear();
        nodes.add(new PoseNode(this.driver.getPoseEstimate()));
        nodes.add(new PoseNode(this.driver.getPoseEstimate()));
        this.telemetry.addLine("Initialized");
        this.telemetry.update();
    }

    @Override
    public void loop() {
        this.telemetry.addData("Nodes", nodes.size());
        this.telemetry.addLine();

        super.loop();

        Pose2d pos = this.driver.getPoseEstimate();

        Gamepad prevGamepad1 = new Gamepad();
        Gamepad prevGamepad2 = new Gamepad();
        prevGamepad1.copy(this.prevGamepad1);
        prevGamepad2.copy(this.prevGamepad2);

        recordRotorPos("slideRot", this.clawSlide.slideRotate.getTargetPosition());
        recordRotorPos("slideLift", this.clawSlide.slideLift.getTargetPosition());
        recordRotorPos("clawRot", this.clawSlide.claw.getLeftRotAngle());
        recordRotorPos("clawArmLeft", this.clawSlide.claw.getLeftClawAngle());
        recordRotorPos("clawArmRight", this.clawSlide.claw.getLeftClawAngle());

        RecordNode lastNode = nodes.get(nodes.size() - 1);
        if (!prevGamepad1.a && this.gamepad1.a) {
            nodes.add(new PoseNode(pos));
            nodes.add(new LogNode("ManualSave", true));
            nodes.add(new PoseNode(pos));
        } else {
            PoseNode lastPos = lastNode instanceof PoseNode ? (PoseNode) lastNode : null;
            Pose2d last2Pos = getSecondLastPose();
            if (lastPos == null) {
                if (last2Pos == null || isPoseMoved(pos, last2Pos) || isPoseTurned(pos, last2Pos)) {
                    nodes.add(new PoseNode(pos));
                }
            } else {
                boolean lastMoved = isPoseMoved(lastPos.pos, last2Pos);
                boolean lastTurned = isPoseTurned(lastPos.pos, last2Pos);
                boolean moved = isPoseMoved(lastPos.pos, pos);
                boolean turned = isPoseTurned(lastPos.pos, pos);
                if ((lastMoved && lastTurned) || (lastMoved && turned) || (lastTurned && moved)) {
                    if (lastMoved) {
                        lastPos.pos = new Pose2d(lastPos.pos.getX(), lastPos.pos.getY(), last2Pos.getHeading());
                    } else {
                        lastPos.pos = new Pose2d(last2Pos.getX(), last2Pos.getY(), lastPos.pos.getHeading());
                    }
                    nodes.add(new PoseNode(pos));
                } else {
                    lastPos.pos = pos;
                }
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
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        return Math.abs(x) + Math.abs(y) >= 1;
    }

    static boolean isPoseTurned(Pose2d a, Pose2d b) {
        final double MAX_DIFF = Math.toRadians(1.0);
        double diff = Math.abs(a.getHeading() - b.getHeading() + Math.PI * 2) % (Math.PI * 2);
        return MAX_DIFF < diff && diff < Math.PI * 2 - MAX_DIFF;
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
        data.put("x", String.format("%.3f", pos.getX()));
        data.put("y", String.format("%.3f", pos.getY()));
        data.put("heading", String.format("%.2f", Math.toDegrees(pos.getHeading())));
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
