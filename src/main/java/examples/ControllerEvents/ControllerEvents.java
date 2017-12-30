package examples.ControllerEvents;

import me.dawars.openvr_for_processing.OpenVRLibrary;

import me.dawars.openvr_for_processing.utils.ControllerUtils;
import processing.core.PApplet;
import vr.VREvent_t;

import static vr.VR.EVRButtonId.*;

public class ControllerEvents extends PApplet {

    public static void main(String[] args) {
        PApplet.main(ControllerEvents.class);
    }

    OpenVRLibrary openVR;

    @Override
    public void settings() {
        size(800, 600);
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
                k_EButton_Oculus_Joystick,
                k_EButton_SteamVR_Trigger,
                k_EButton_Grip,
                k_EButton_A,
                k_EButton_ApplicationMenu,
        };

        String[] buttonNames = {
                "Joystick",
                "Trigger",
                "Grip",
                "A/B",
                "X/Y",
        };

        for (int controller = 0; controller < ControllerUtils.Hand.MAX; controller++) {

            for (int i = 0; i < buttons.length; i++) {

                float x = (float) (width / 2 + -Math.pow(-1, controller) * width / 4);
                int y = 100 + i * 100;

                fill(0);

                text(buttonNames[i], 20, y);

                if (openVR.isButtonPressed(controller, buttons[i])) {
                    fill(0);
                } else if (openVR.isButtonTouched(controller, buttons[i])) {
                    fill(127f);
                } else {
                    fill(255f);
                }

                ellipse(x, y, 50, 50);
            }
        }

        fill(255);
    }

    public void buttonPressed(int hand, int button) {
        System.out.println(hand + " : Pressed " + button);
    }

    public void buttonReleased(int hand, int button) {
        System.out.println(hand + " : Released " + button);
    }

    public void buttonTouched(int hand, int button) {
        System.out.println(hand + " : Touched " + button);
    }

    public void buttonUntouched(int hand, int button) {
        System.out.println(hand + " : Untouched " + button);
    }

    public boolean vrEvent(VREvent_t event) {
//        System.out.println("Event " + event.eventType);

        return false;
    }
}
