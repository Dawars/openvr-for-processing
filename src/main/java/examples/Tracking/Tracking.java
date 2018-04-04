package examples.Tracking;

import me.dawars.openvr_for_processing.OpenVRLibrary;
import me.dawars.openvr_for_processing.utils.MathUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PVector;
import vr.VR;

import static vr.VR.ETrackedDeviceProperty.Prop_ModelNumber_String;
import static vr.VR.k_unMaxTrackedDeviceCount;
import static vr.VR.k_unTrackedDeviceIndex_Hmd;

public class Tracking extends PApplet {

    private float worldScale = 200;

    public static void main(String[] args) {
        PApplet.main(Tracking.class);
    }

    OpenVRLibrary openVR;

    @Override
    public void settings() {
        size(1280, 720, P3D);
    }

    @Override
    public void setup() {
        openVR = new OpenVRLibrary(this);

        frameRate(90);

        fill(255, 255, 255);
        color(255, 255, 255);
        strokeWeight(3);
        stroke(255f);
    }


    public void drawMatrix(PMatrix3D mat, float x, float y) {
        float[] m = new float[16];
        mat.get(m);

        for (int i = 0; i < m.length; i++) {
            m[i] = Math.round(m[i] * 100f) / 100f;
        }

        float w = textWidth("-2.001 ");

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                text(m[4 * j + i], x + i * w, y + j * 20);
            }
        }
    }

    @Override
    public void draw() {
        background(0);

        PVector playArea = openVR.getPlayArea();
        text("PlayArea: " + playArea.x + " m x " + playArea.y + " m", 10, 20);


        PVector[] playAreaRect = openVR.getPlayAreaRect();
        for (int i = 0; i <= playAreaRect.length; i++) {

            line(width / 2 + playAreaRect[i % 4].x * worldScale,
                    height / 2 + playAreaRect[i % 4].z * worldScale,
                    width / 2 + playAreaRect[(i + 1) % 4].x * worldScale,
                    height / 2 + playAreaRect[(i + 1) % 4].z * worldScale);
        }

        for (int deviceId = 0; deviceId < 6; deviceId++) {
            PMatrix3D pose = openVR.GetDeviceToAbsoluteTrackingPose(deviceId);
            if (pose == null)
                continue;

            text(openVR.getDeviceName(deviceId), 10, 40 + 120 * deviceId);
            drawMatrix(pose, 10, 60 + 120 * deviceId);

            PVector pos = MathUtils.GetPosition(pose);
            point(width / 2 + pos.x * worldScale, height / 2 + pos.z * worldScale); // hmd working

            float angle = MathUtils.GetRotationY(pose);

            PImage icon = openVR.getDeviceIcon(deviceId);
            if (icon == null) continue;


            pushMatrix();

            translate(width / 2 + pos.x * worldScale, height / 2 + pos.z * worldScale);

            rotateZ(-angle);

            imageMode(CENTER);
            image(icon, 0, 0);
            popMatrix();
        }
    }
}
