package org.firstinspires.ftc.teamcode.teleop;

import android.content.Context;

import androidx.annotation.Nullable;

import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.WebHandlerManager;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

@TeleOp(name = "BPathRecorder")
public class BPathRecorder extends AMainTeleOp {
    static final List<RecordNode> nodes = new ArrayList<>();

    SampleMecanumDrive driver;

    boolean last1X = false;
    boolean last2A = false;
    boolean last2B = false;
    boolean last2X = false;
    boolean last2Y = false;

    @Override
    public void init() {
        super.init();
        this.driver = new SampleMecanumDrive(hardwareMap);
        nodes.clear();
        nodes.add(new PoseNode(this.driver.getLocalizer().getPoseEstimate()));
    }

    @Override
    public void loop() {
        com.acmerobotics.roadrunner.localization.Localizer localizer = this.driver.getLocalizer();
        localizer.update();
        Pose2d pos = localizer.getPoseEstimate();

        this.telemetry.addData("Nodes", nodes.size());
        this.telemetry.addLine();
        this.telemetry.addData("Pos", "%+03.02f, %+03.02f", pos.getX(), pos.getY());
        this.telemetry.addData("Heading", Math.toDegrees(pos.getHeading()));

        super.loop();

        boolean updatedPose = !this.last1X && this.gamepad1.x;
        if (updatedPose) {
            nodes.add(new PoseNode(pos));
        }
        this.last1X = this.gamepad1.x;

        if (!updatedPose) {
            PoseNode lastNode = getLastPoseNode();
            Pose2d last2Pos = getSecondLastPose();
            if (lastNode == null) {
                if (Math.abs(last2Pos.getX() - pos.getX()) + Math.abs(last2Pos.getY() - pos.getY()) > 1 || Math.abs(last2Pos.getHeading() - pos.getHeading() + Math.PI * 2) % (Math.PI * 2) > 0.01) {
                    nodes.add(new PoseNode(pos));
                }
            } else {
                lastNode.pos = pos;
            }
        }
        if (!this.last2A && this.gamepad2.a) {
            nodes.add(new ActionNode("A"));
        }
        if (!this.last2B && this.gamepad2.b) {
            nodes.add(new ActionNode("B"));
        }
        if (!this.last2X && this.gamepad2.x) {
            nodes.add(new ActionNode("X"));
        }
        if (!this.last2Y && this.gamepad2.y) {
            nodes.add(new ActionNode("Y"));
        }
        this.last2A = this.gamepad2.a;
        this.last2B = this.gamepad2.b;
        this.last2X = this.gamepad2.x;
        this.last2Y = this.gamepad2.y;
    }

    @Nullable
    static PoseNode getLastPoseNode() {
        RecordNode n = nodes.get(nodes.size() - 1);
        return n instanceof PoseNode ? (PoseNode) n : null;
    }

    static Pose2d getSecondLastPose() {
        for (int i = nodes.size() - 2; i >= 0; i--) {
            RecordNode n = nodes.get(i);
            if (n instanceof PoseNode) {
                return ((PoseNode) n).pos;
            }
        }
        throw new IllegalStateException("No pose node found in the list!");
    }

    @WebHandlerRegistrar
    public static void registerWebHandler(Context context, WebHandlerManager manager) {
        manager.register("/recorded_paths", session -> {
            StringBuilder builder = new StringBuilder();
            Formatter fmt = new Formatter(builder);
            builder.append("# Length: ").append(nodes.size());
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
    int pos;
    RotorNode(String rotor, int pos) { this.rotor = rotor; this.pos = pos; }
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
