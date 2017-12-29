package me.dawars.openvr_for_processing;


import com.jogamp.opengl.util.GLBuffers;
import processing.core.PApplet;
import processing.core.PVector;
import vr.*;

import java.lang.reflect.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import static vr.VR.EVRButtonId.k_EButton_Grip;
import static vr.VR.EVREventType.*;
import static vr.VR.EVREventType.VREvent_ChaperoneSettingsHaveChanged;
import static vr.VR.EVREventType.VREvent_ChaperoneTempDataHasChanged;
import static vr.VR.k_unMaxTrackedDeviceCount;

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
        }
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

                // Controller
                case VREvent_ButtonPress:
                    callButtonPressedEvent(event.trackedDeviceIndex, event.data.controller);
                    break;
                case VREvent_ButtonUnpress:
                    callButtonReleasedEvent(event.trackedDeviceIndex, event.data.controller);
                    break;
                case VREvent_ButtonTouch:
                    callButtonTouchedEvent(event.trackedDeviceIndex, event.data.controller);
                    break;
                case VREvent_ButtonUntouch:
                    callButtonUntouchedEvent(event.trackedDeviceIndex, event.data.controller);
                    break;
            }
        }

        /*
        // Process SteamVR controller state
        for (int unDevice = 0; unDevice < k_unMaxTrackedDeviceCount; unDevice++) {
            VRControllerState_t state = new VRControllerState_t();
            if (hmd.GetControllerState.apply(unDevice, state, state.size())) {
                callControllerEvent(state);

//                m_rbShowTrackedDevice[unDevice] = state.ulButtonPressed == 0;
            }
        }*/
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
                Utils.GetVector(rect.vCorners[0]),
                Utils.GetVector(rect.vCorners[1]),
                Utils.GetVector(rect.vCorners[2]),
                Utils.GetVector(rect.vCorners[3])
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
            controllerButtonPressMethod = parent.getClass().getMethod("buttonPressed", int.class, VREvent_Controller_t.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            controllerButtonUnpressMethod = parent.getClass().getMethod("buttonReleased", int.class, VREvent_Controller_t.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            controllerButtonTouchMethod = parent.getClass().getMethod("buttonTouched", int.class, VREvent_Controller_t.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            controllerButtonUntouchMethod = parent.getClass().getMethod("buttonUntouched", int.class, VREvent_Controller_t.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        try {
            vrEventMethod = parent.getClass().getMethod("vrEvent", VREvent_t.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
    }

    public void callButtonPressedEvent(int trackedDeviceIndex, VREvent_Controller_t event) {
        if (controllerButtonPressMethod != null) {
            try {
                controllerButtonPressMethod.invoke(parent, trackedDeviceIndex, event);
            } catch (Exception e) {
                System.err.println("Disabling buttonPressed() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonPressMethod = null;
            }
        }
    }

    public void callButtonReleasedEvent(int trackedDeviceIndex, VREvent_Controller_t event) {
        if (controllerButtonUnpressMethod != null) {
            try {
                controllerButtonUnpressMethod.invoke(parent, trackedDeviceIndex, event);
            } catch (Exception e) {
                System.err.println("Disabling buttonReleased() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonUnpressMethod = null;
            }
        }
    }

    public void callButtonTouchedEvent(int trackedDeviceIndex, VREvent_Controller_t event) {
        if (controllerButtonTouchMethod != null) {
            try {
                controllerButtonTouchMethod.invoke(parent, trackedDeviceIndex, event);
            } catch (Exception e) {
                System.err.println("Disabling buttonTouchedEvent() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonTouchMethod = null;
            }
        }
    }

    public void callButtonUntouchedEvent(int trackedDeviceIndex, VREvent_Controller_t event) {
        if (controllerButtonUntouchMethod != null) {
            try {
                controllerButtonUntouchMethod.invoke(parent, trackedDeviceIndex, event);
            } catch (Exception e) {
                System.err.println("Disabling buttonUntouhed() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonUntouchMethod = null;
            }
        }
    }

    /**
     * Advanced method for handling VREvents
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

