package examples.ControllerEvents;

import me.dawars.openvr_for_processing.OpenVRLibrary;

import processing.core.PApplet;
import vr.VREvent_Controller_t;
import vr.VREvent_t;

public class ControllerEvents extends PApplet {

    public static void main(String[] args) {
        PApplet.main(ControllerEvents.class);
    }

    OpenVRLibrary openVR;

    @Override
    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
        openVR = new OpenVRLibrary(this);
    }

    @Override
    public void draw() {
        background(0xff00ff);

        fill(255);
    }

    public void buttonPressed(int deviceId, VREvent_Controller_t event) {
        System.out.println(deviceId + " : Pressed " + event.button);
    }

    public void buttonReleased(int deviceId, VREvent_Controller_t event) {
        System.out.println(deviceId + " : Released " + event.button);
    }

    public void buttonTouched(int deviceId, VREvent_Controller_t event) {
        System.out.println(deviceId + " : Touched " + event.button);
    }

    public void buttonUntouched(int deviceId, VREvent_Controller_t event) {
        System.out.println(deviceId + " : Untouched " + event.button);
    }

    public boolean vrEvent(VREvent_t event) {
        System.out.println("Event " + event.eventType);

        return false;
    }
}
