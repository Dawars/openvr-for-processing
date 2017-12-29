package me.dawars.openvr_for_processing;


import com.jogamp.opengl.util.GLBuffers;
import me.dawars.openvr_for_processing.utils.ControllerUtils;
import me.dawars.openvr_for_processing.utils.MathUtils;
import processing.core.PApplet;
import processing.core.PVector;
import vr.*;

import java.lang.reflect.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static me.dawars.openvr_for_processing.utils.ControllerUtils.IsButtonPressedOrTouched;
import static vr.VR.ETrackedControllerRole.TrackedControllerRole_LeftHand;
import static vr.VR.ETrackedControllerRole.TrackedControllerRole_RightHand;
import static vr.VR.ETrackedDeviceClass.TrackedDeviceClass_Controller;
import static vr.VR.ETrackedDeviceProperty.Prop_Axis0Type_Int32;
import static vr.VR.ETrackedDeviceProperty.Prop_ControllerRoleHint_Int32;
import static vr.VR.EVRButtonId.k_EButton_Max;
import static vr.VR.EVRControllerAxisType.*;
import static vr.VR.EVREventType.*;
import static vr.VR.*;

public class OpenVRLibrary {
    private String name = "OpenVR for Processing";
    /**
     * Shortcut to the fully qualified class name.
     */
    public static final String OVR = "me.dawars.openvr_for_processing.OpenVRLibrary";

    private PApplet parent;

    private Method controllerButtonPressMethod;
    private Method controllerButtonUnpressMethod;
    private Method controllerButtonTouchMethod;
    private Method controllerButtonUntouchMethod;
    private Method vrEventMethod;

    private boolean isReady = false; // if initializing openvr is finished

    public OpenVRLibrary(PApplet parent) {
        this.parent = parent;
        parent.registerMethod("dispose", this);
        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);
        parent.registerMethod("post", this);

        registerEvents();
    }

    private IVRCompositor_FnTable compositor;
    private IVRChaperone_FnTable chaperone;

    private IVRSystem hmd;
    private IntBuffer errorBuffer = GLBuffers.newDirectIntBuffer(1);

    public void pre() {
        // init JOpenVR
        while (!isReady) {
            System.out.println("Initializing OpenVR...");
            hmd = VR.VR_Init(errorBuffer, VR.EVRApplicationType.VRApplication_Scene);

            int error = errorBuffer.get(0);
            if (error != VR.EVRInitError.VRInitError_None) {

                System.out.println(VR.VR_GetVRInitErrorAsEnglishDescription(error));
                parent.delay(1000);
                continue;
            }

            chaperone = new IVRChaperone_FnTable(VR.VR_GetGenericInterface(VR.IVRChaperone_Version, errorBuffer));
            System.out.println(VR.VR_GetVRInitErrorAsEnglishDescription(error));

            compositor = new IVRCompositor_FnTable(VR.VR_GetGenericInterface(VR.IVRCompositor_Version, errorBuffer));
            System.out.println(VR.VR_GetVRInitErrorAsEnglishDescription(error));


            isReady = true;
            postInit();
        }
    }

    private void postInit() {
        updateChaperoneData();
        updateControllerRole();
    }

    public void draw() {
        parent.text(VR.VR_RuntimePath(), 10, 20);
    }

    public void post() {
        // process events
        VREvent_t event = new VREvent_t();
        while (hmd.PollNextEvent.apply(event, event.size())) {

            if (callVREvent(event)) {
                continue;
            }

//            System.out.println(Utils.GetVREventName(event.eventType));

            switch (event.eventType) {
                //Handle quiting the app from Steam
                case VREvent_DriverRequestedQuit:
                case VREvent_Quit:
                    parent.exit();
                    break;

                case VREvent_ChaperoneDataHasChanged:
                case VREvent_ChaperoneUniverseHasChanged:
                case VREvent_ChaperoneTempDataHasChanged:
                case VREvent_ChaperoneSettingsHaveChanged:
                    updateChaperoneData();
                    break;

                // Device connected
//                case VREvent_TrackedDeviceActivated:
//                case VREvent_TrackedDeviceDeactivated:
                case VREvent_TrackedDeviceRoleChanged:
//                case VREvent_TrackedDeviceUpdated:
//                case VREvent_TrackedDeviceUserInteractionEnded:
//                case VREvent_TrackedDeviceUserInteractionStarted:

                    updateControllerRole();
                    break;

                // Controller
                /*
                // Can't use because buttonId is always 0
                case VREvent_ButtonPress:
                    callButtonPressedEvent(event.trackedDeviceIndex, event.data.controller.button);
                    break;
                case VREvent_ButtonUnpress:
                    callButtonReleasedEvent(event.trackedDeviceIndex, event.data.controller.button);
                    break;
                case VREvent_ButtonTouch:
                    callButtonTouchedEvent(event.trackedDeviceIndex, event.data.controller.button);
                    break;
                case VREvent_ButtonUntouch:
                    callButtonUntouchedEvent(event.trackedDeviceIndex, event.data.controller.button);
                    break;*/
            }
        }

        processControllerEvents();
    }

    /* Controller */

    int[] controllerIds = new int[ControllerUtils.Side.Max];

    private void updateControllerRole() {

        // Process SteamVR controller state
        for (int deviceId = 0; deviceId < k_unMaxTrackedDeviceCount; deviceId++) {
            // if not controller, skip
            if (hmd.GetTrackedDeviceClass.apply(deviceId) != TrackedDeviceClass_Controller)
                continue;

            int controllerRole = hmd.GetInt32TrackedDeviceProperty.apply(deviceId, Prop_ControllerRoleHint_Int32, errorBuffer);

            if (controllerRole == TrackedControllerRole_RightHand) {
                controllerIds[ControllerUtils.Side.Right] = deviceId;
            } else if (controllerRole == TrackedControllerRole_LeftHand) {
                controllerIds[ControllerUtils.Side.Left] = deviceId;
            } else {
                // neither
            }
        }
    }

    // FIXME per hand values
    private int[] lastControllerPacketNum = {-1, -1};
    private long[] lastButtonPressed = {0, 0};
    private long[] lastButtonTouched = {0, 0};

    private void processControllerEvents() {

        // Process SteamVR controller state
        for (int deviceId = 0; deviceId < k_unMaxTrackedDeviceCount; deviceId++) {
            // if not controller, skip
            if (hmd.GetTrackedDeviceClass.apply(deviceId) != TrackedDeviceClass_Controller)
                continue;

            // get hand
            int hand = -1;
            int controllerRole = hmd.GetInt32TrackedDeviceProperty.apply(deviceId, Prop_ControllerRoleHint_Int32, errorBuffer);

            if (controllerRole == TrackedControllerRole_RightHand) {
                hand = ControllerUtils.Side.Right;
            } else if (controllerRole == TrackedControllerRole_LeftHand) {
                hand = ControllerUtils.Side.Left;
            } else {
                System.err.println("No hand for controller");
                // neither
            }

            // getting controller state for every button on deviceId
            VRControllerState_t state = new VRControllerState_t();
            if (hmd.GetControllerState.apply(deviceId, state, state.size()) && state.unPacketNum != lastControllerPacketNum[hand]) {
                // checking every analog button
                /*for (int buttonId = 0; buttonId < k_unControllerStateAxisCount; buttonId++) {
                    int type = hmd.GetInt32TrackedDeviceProperty.apply(deviceId, Prop_Axis0Type_Int32 + buttonId, errorBuffer);
//                    System.out.println("Axis " + buttonId + " type: " + hmd.GetControllerAxisTypeNameFromEnum.apply(type));


                    switch (type) {
                        case k_eControllerAxis_None: // no analog values
                            break;
                        case k_eControllerAxis_TrackPad:
                            System.out.println("Trackpad: " + state.rAxis[buttonId].x + " " + state.rAxis[buttonId].y);
                            break;
                        case k_eControllerAxis_Joystick:
                            System.out.println("Joystick: " + state.rAxis[buttonId].x + " " + state.rAxis[buttonId].y);
                            break;
                        case k_eControllerAxis_Trigger:
                            System.out.println("Trigger: " + state.rAxis[buttonId].x);
                            break;
                    }
                }*/

                // checking every digital button
                for (int buttonId = 0; buttonId < k_EButton_Max; buttonId++) {
                    // is pressed
                    if (IsButtonPressedOrTouched(state.ulButtonPressed, buttonId) != IsButtonPressedOrTouched(lastButtonPressed[hand], buttonId)) { // state changed
                        if (IsButtonPressedOrTouched(state.ulButtonPressed, buttonId)) {
                            callButtonPressedEvent(deviceId, buttonId);
                        } else {
                            callButtonReleasedEvent(deviceId, buttonId);
                        }
                    }
                    // is touched
                    if (IsButtonPressedOrTouched(state.ulButtonTouched, buttonId) != IsButtonPressedOrTouched(lastButtonTouched[hand], buttonId)) { // state changed
                        if (IsButtonPressedOrTouched(state.ulButtonTouched, buttonId)) {
                            callButtonTouchedEvent(deviceId, buttonId);
                        } else {
                            callButtonUntouchedEvent(deviceId, buttonId);
                        }
                    }
                }

                lastButtonPressed[hand] = state.ulButtonPressed;
                lastButtonTouched[hand] = state.ulButtonTouched;

                lastControllerPacketNum[hand] = state.unPacketNum; // update packet num
            }
        }
    }

    // Helper functions, probably should move to separate class (how to access hmd, singleton?)

    /**
     * Helper function, tells if a button is pressed
     *
     * @param hand   which controller
     * @param button which button on controller
     * @return whether it is pressed
     */
    public boolean isButtonPressed(int hand, int button) {
        return ControllerUtils.IsButtonPressedOrTouched(lastButtonPressed[hand], button);
    }

    /**
     * Helper function, tells if a button is touched
     *
     * @param hand   which controller
     * @param button which button on controller
     * @return whether it is touched
     */
    public boolean isButtonTouched(int hand, int button) {
        return ControllerUtils.IsButtonPressedOrTouched(lastButtonTouched[hand], button);
    }

    /**
     * Chaperone
     */
    private PVector playArea = new PVector();
    private PVector[] playAreaRect;

    private void updateChaperoneData() {
        System.out.println("Updating chaperone data");
        FloatBuffer w = GLBuffers.newDirectFloatBuffer(1);
        FloatBuffer h = GLBuffers.newDirectFloatBuffer(1);
        chaperone.GetPlayAreaSize.apply(w, h);

        playArea.set(w.get(0), h.get(0));

        HmdQuad_t rect = new HmdQuad_t();
        chaperone.GetPlayAreaRect.apply(rect);
        playAreaRect = new PVector[]{
                MathUtils.GetVector(rect.vCorners[0]),
                MathUtils.GetVector(rect.vCorners[1]),
                MathUtils.GetVector(rect.vCorners[2]),
                MathUtils.GetVector(rect.vCorners[3])
        };
    }

    /**
     * Returns the size of the play area in meters
     *
     * @return width and height of the play area
     */
    public PVector getPlayArea() {
        return playArea;
    }

    /**
     * Returns the corners of the play area in meters
     *
     * @return play area corners
     */
    public PVector[] getPlayAreaRect() {
        return playAreaRect;
    }

    /**
     * Events
     */

    private void registerEvents() {
        // check to see if the host applet implements
        // public void controllerEvent(VREvent_Controller_t f)
        try {
            controllerButtonPressMethod = parent.getClass().getMethod("buttonPressed", int.class, int.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            controllerButtonUnpressMethod = parent.getClass().getMethod("buttonReleased", int.class, int.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            controllerButtonTouchMethod = parent.getClass().getMethod("buttonTouched", int.class, int.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            controllerButtonUntouchMethod = parent.getClass().getMethod("buttonUntouched", int.class, int.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            vrEventMethod = parent.getClass().getMethod("vrEvent", VREvent_t.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
    }

    // TODO add Left and Right enum for controllers
    public void callButtonPressedEvent(int deviceId, int buttonId) {
        if (controllerButtonPressMethod != null) {
            try {
                controllerButtonPressMethod.invoke(parent, deviceId, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonPressed() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonPressMethod = null;
            }
        }
    }

    public void callButtonReleasedEvent(int deviceId, int buttonId) {
        if (controllerButtonUnpressMethod != null) {
            try {
                controllerButtonUnpressMethod.invoke(parent, deviceId, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonReleased() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonUnpressMethod = null;
            }
        }
    }

    public void callButtonTouchedEvent(int deviceId, int buttonId) {
        if (controllerButtonTouchMethod != null) {
            try {
                controllerButtonTouchMethod.invoke(parent, deviceId, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonTouchedEvent() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonTouchMethod = null;
            }
        }
    }

    public void callButtonUntouchedEvent(int deviceId, int buttonId) {
        if (controllerButtonUntouchMethod != null) {
            try {
                controllerButtonUntouchMethod.invoke(parent, deviceId, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonUntouched() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonUntouchMethod = null;
            }
        }
    }

    /**
     * Advanced method for handling VREvents
     *
     * @param event VREvent
     * @return true if the event is consumed, default behaviour will be skipped
     */
    private boolean callVREvent(VREvent_t event) {
        if (vrEventMethod != null) {
            try {
                return (boolean) vrEventMethod.invoke(parent, event);
            } catch (Exception e) {
                System.err.println("Disabling vrEvent() for " + name + " because of an error.");
                e.printStackTrace();
                vrEventMethod = null;
            }
        }
        return false;
    }

    public void dispose() {
        System.out.println("Dispose is called");
        VR.VR_Shutdown();
    }
}

