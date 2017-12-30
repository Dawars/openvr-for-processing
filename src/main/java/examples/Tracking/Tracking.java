package examples.Tracking;

import me.dawars.openvr_for_processing.OpenVRLibrary;
import me.dawars.openvr_for_processing.utils.MathUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PVector;
import vr.VR;

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


    @Override
    public void draw() {
        background(0xffffff);

        background(0);
        text("FPS: " + frameRate, width - textWidth("FPS: 90 ") - 10, 20);

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
            PVector pos = MathUtils.GetPosition(pose);
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
