package examples.ControllerEvents;

import me.dawars.openvr_for_processing.OpenVRLibrary;

import me.dawars.openvr_for_processing.utils.ControllerUtils;
import processing.core.PApplet;
import vr.VR;
import vr.VREvent_Controller_t;
import vr.VREvent_t;

import static vr.VR.EVRButtonId.*;

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
        background(0xffffff);

        stroke(0);

        int[] buttons = {
                k_EButton_Grip,
                k_EButton_A,
                k_EButton_ApplicationMenu,
                k_EButton_SteamVR_Trigger,
                k_EButton_Oculus_Joystick
        };

        for (int controller = 0; controller < ControllerUtils.Side.Max; controller++) {

            for (int i = 0; i < buttons.length; i++) {

                if (openVR.isButtonPressed(controller, buttons[i])) {
                    fill(0);
                } else if (openVR.isButtonTouched(controller, buttons[i])) {
                    fill(127f);
                } else {
                    fill(255f);
                }

                ellipse((float) (width / 2 + Math.pow(-1, controller) * width / 4), 100 + i * 100, 50, 50);
            }
        }

        fill(255);
    }

    public void buttonPressed(int deviceId, int buttonId) {
        System.out.println(deviceId + " : Pressed " + buttonId);
    }

    public void buttonReleased(int deviceId, int buttonId) {
        System.out.println(deviceId + " : Released " + buttonId);
    }

    public void buttonTouched(int deviceId, int buttonId) {
        System.out.println(deviceId + " : Touched " + buttonId);
    }

    public void buttonUntouched(int deviceId, int buttonId) {
        System.out.println(deviceId + " : Untouched " + buttonId);
    }

    public boolean vrEvent(VREvent_t event) {
//        System.out.println("Event " + event.eventType);

        return false;
    }
}
