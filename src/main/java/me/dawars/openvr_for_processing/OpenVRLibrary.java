package me.dawars.openvr_for_processing;


import com.jogamp.opengl.util.GLBuffers;
import me.dawars.openvr_for_processing.utils.ControllerUtils;
import me.dawars.openvr_for_processing.utils.MathUtils;
import me.dawars.openvr_for_processing.utils.Utils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PSurfaceJOGL;
import processing.opengl.Texture;
import vr.*;

import java.lang.reflect.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static me.dawars.openvr_for_processing.utils.ControllerUtils.*;
import static me.dawars.openvr_for_processing.utils.ControllerUtils.IsButtonPressedOrTouched;
import static vr.VR.ETrackedControllerRole.TrackedControllerRole_LeftHand;
import static vr.VR.ETrackedControllerRole.TrackedControllerRole_RightHand;
import static vr.VR.ETrackedDeviceClass.*;
import static vr.VR.ETrackedDeviceProperty.*;
import static vr.VR.ETrackingUniverseOrigin.TrackingUniverseStanding;
import static vr.VR.EVRButtonId.k_EButton_Max;
import static vr.VR.EVREventType.*;
import static vr.VR.*;

public class OpenVRLibrary {
    private String name = "OpenVR for Processing";
    /**
     * Shortcut to the fully qualified class name.
     */
    public static final String OVR = "me.dawars.openvr_for_processing.OpenVRLibrary";

    private PApplet parent;
    private PGraphicsOpenGL pg;

    private Method controllerButtonPressMethod;
    private Method controllerButtonUnpressMethod;
    private Method controllerButtonTouchMethod;
    private Method controllerButtonUntouchMethod;
    private Method vrEventMethod;
    private Method postInitMethod;

    private boolean isReady = false; // if initializing openvr is finished

    private int m_iValidPoseCount;
    private String m_strPoseClasses;
    private PMatrix3D[] m_rmat4DevicePose = new PMatrix3D[k_unMaxTrackedDeviceCount];
    private char[] m_rDevClassChar = new char[k_unMaxTrackedDeviceCount];
    private PMatrix3D m_mat4HMDPose;

    public OpenVRLibrary(PApplet parent) {
        this.parent = parent;
/*// TODO OVR renderer for hmd transformation
        if (!parent.sketchRenderer().equals(parent.P3D)) {
            System.out.println("Renderer must be P3D, call size(1280, 720, P3D); in setup");
        }*/

        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);
        parent.registerMethod("post", this);
        parent.registerMethod("dispose", this);

        registerEvents();
        if (debugRenderer.equals(OVR)) {
            pg = (PGraphicsOpenGL) parent.g;
        }
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


        // Process SteamVR events
        VREvent_t event = new VREvent_t();
        while (hmd.PollNextEvent.apply(event, event.size())) {
            ProcessVREvent(event);
        }

        // Process SteamVR controller state
        processControllerEvents();

        /* vr camera */
        if (debugRenderer.equals(OVR)) {//  eye to head


            // matMVP = m_mat4ProjectionLeft * m_mat4eyePosLeft * m_mat4HMDPose;
            PMatrix3D matMVP = new PMatrix3D();

            PMatrix3D pose = GetDeviceToAbsoluteTrackingPose(k_unTrackedDeviceIndex_Hmd);


            for (int eye = 0; eye < EVREye.Max; eye++) {
                // camera eye pos
                PMatrix3D eyePos = MathUtils.GetPMatrix(hmd.GetEyeToHeadTransform.apply(0));
                eyePos.invert();
                matMVP.set(eyePos);
                matMVP.apply(pose);

                //set projection

                pg.modelview.set(matMVP);
                pg.modelviewInv.set(matMVP);
                pg.modelviewInv.invert();
                pg.updateProjmodelview();
            }
        }
    }

    public static String debugRenderer = PApplet.P3D;

    private final Texture eyeTextures[] = new Texture[2];
    private final PGraphicsOpenGL[] views = new PGraphicsOpenGL[2];

    private void postInit() {
        updateChaperoneData();
        updateControllerRole();

        // set projection matrix for hmd
        float m_fNearClip = 0.1f;
        float m_fFarClip = 30.0f;

        if (debugRenderer.equals(OVR)) {
            // projection matrix
            PMatrix3D proj = MathUtils.GetPMatrix(hmd.GetProjectionMatrix.apply(0, m_fNearClip, m_fFarClip));

            pg.setProjection(proj);
        }

        // init render targets

        if (debugRenderer.equals(OVR)) {
            IntBuffer width = GLBuffers.newDirectIntBuffer(1), height = GLBuffers.newDirectIntBuffer(1);
            hmd.GetRecommendedRenderTargetSize.apply(width, height);

            int w = width.get(0);
            int h = height.get(0);

            for (int eye = 0; eye < EVREye.Max; eye++) {
                views[eye] = (PGraphicsOpenGL) parent.createGraphics(w, h, parent.P3D);
                eyeTextures[eye] = views[eye].getTexture();
            }
        }

        callPostInit();
    }

    public void draw() {
        parent.text("FPS: " + parent.frameRate, parent.width - parent.textWidth("FPS: 90.00 ") - 10, 20);

    }


    public void post() {

        UpdateHMDMatrixPose();
    }

    private void ProcessVREvent(VREvent_t event) {

        if (callVREvent(event)) {
            return;
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
            case VREvent_TrackedDeviceActivated:
            case VREvent_TrackedDeviceDeactivated:
            case VREvent_TrackedDeviceRoleChanged:
            case VREvent_TrackedDeviceUpdated:
//                case VREvent_TrackedDeviceUserInteractionEnded:
//                case VREvent_TrackedDeviceUserInteractionStarted:
                System.out.println(Utils.GetVREventName(event.eventType));
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

    private TrackedDevicePose_t.ByReference trackedDevicePosesReference = new TrackedDevicePose_t.ByReference();
    public TrackedDevicePose_t[] trackedDevicePose
            = (TrackedDevicePose_t[]) trackedDevicePosesReference.toArray(VR.k_unMaxTrackedDeviceCount);


    private void UpdateHMDMatrixPose() {
        if (hmd == null)
            return;

        compositor.WaitGetPoses.apply(trackedDevicePosesReference, k_unMaxTrackedDeviceCount, null, 0);

        m_iValidPoseCount = 0;
        m_strPoseClasses = "";
        for (int nDevice = 0; nDevice < k_unMaxTrackedDeviceCount; ++nDevice) {
            if (trackedDevicePose[nDevice].bPoseIsValid != 0) {
                m_iValidPoseCount++;
                m_rmat4DevicePose[nDevice] = MathUtils.GetPMatrix(trackedDevicePose[nDevice].mDeviceToAbsoluteTracking);
                if (m_rDevClassChar[nDevice] == 0) {
                    switch (hmd.GetTrackedDeviceClass.apply(nDevice)) {
                        case TrackedDeviceClass_Controller:
                            m_rDevClassChar[nDevice] = 'C';
                            break;
                        case TrackedDeviceClass_HMD:
                            m_rDevClassChar[nDevice] = 'H';
                            break;
                        case TrackedDeviceClass_Invalid:
                            m_rDevClassChar[nDevice] = 'I';
                            break;
                        case TrackedDeviceClass_GenericTracker:
                            m_rDevClassChar[nDevice] = 'G';
                            break;
                        case TrackedDeviceClass_TrackingReference:
                            m_rDevClassChar[nDevice] = 'T';
                            break;
                        default:
                            m_rDevClassChar[nDevice] = '?';
                            break;
                    }
                }
                m_strPoseClasses += m_rDevClassChar[nDevice];
            }
        }

        if (trackedDevicePose[k_unTrackedDeviceIndex_Hmd].bPoseIsValid != 0) {
            m_mat4HMDPose = m_rmat4DevicePose[k_unTrackedDeviceIndex_Hmd];
            m_mat4HMDPose.invert();
        }
    }

    /*
     * Controller
     * TODO use these values
     */
    private int[] controllerIds = new int[Hand.MAX];

    private void updateControllerRole() {
       /* int nextHandId = 0;

        // Process SteamVR controller state
        for (int deviceId = 0; deviceId < k_unMaxTrackedDeviceCount; deviceId++) {
            // if not controller, skip
            if (hmd.GetTrackedDeviceClass.apply(deviceId) != TrackedDeviceClass_Controller)
                continue;

            int controllerRole = hmd.GetInt32TrackedDeviceProperty.apply(deviceId, Prop_ControllerRoleHint_Int32, errorBuffer);

            if (controllerRole == TrackedControllerRole_RightHand) {
                controllerIds[Hand.RIGHT] = deviceId;
            } else if (controllerRole == TrackedControllerRole_LeftHand) {
                controllerIds[Hand.LEFT] = deviceId;
            } else {
                controllerIds[nextHandId++] = deviceId;
            }
        }*/
    }

    private int[] lastControllerPacketNum = {-1, -1};
    private long[] lastButtonPressed = {0, 0};
    private long[] lastButtonTouched = {0, 0};

    private void processControllerEvents() {
        int nextHandId = 0;
        // Process SteamVR controller state
        for (int deviceId = 0; deviceId < k_unMaxTrackedDeviceCount; deviceId++) {
            // if not controller, skip
            if (hmd.GetTrackedDeviceClass.apply(deviceId) != TrackedDeviceClass_Controller)
                continue;

            if (!hmd.IsTrackedDeviceConnected.apply(deviceId))
                continue;

            // get hand
            int hand; // todo: make lookup function
            int controllerRole = hmd.GetInt32TrackedDeviceProperty.apply(deviceId, Prop_ControllerRoleHint_Int32, errorBuffer);

            if (controllerRole == TrackedControllerRole_RightHand) {
                hand = Hand.RIGHT;
            } else if (controllerRole == TrackedControllerRole_LeftHand) {
                hand = Hand.LEFT;
            } else {
                // neither, index manually
                // TODO assign hand in connection order for vive
                hand = nextHandId++;
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
                    if (IsButtonPressedOrTouched(state.ulButtonPressed, buttonId) != IsButtonPressedOrTouched(lastButtonPressed[hand],
                            buttonId)) { // state changed
                        if (IsButtonPressedOrTouched(state.ulButtonPressed, buttonId)) {
                            callButtonPressedEvent(hand, buttonId);
                        } else {
                            callButtonReleasedEvent(hand, buttonId);
                        }
                    }
                    // is touched
                    if (IsButtonPressedOrTouched(state.ulButtonTouched, buttonId) != IsButtonPressedOrTouched(lastButtonTouched[hand],
                            buttonId)) { // state changed
                        if (IsButtonPressedOrTouched(state.ulButtonTouched, buttonId)) {
                            callButtonTouchedEvent(hand, buttonId);
                        } else {
                            callButtonUntouchedEvent(hand, buttonId);
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

    /*
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
                MathUtils.GetPVector(rect.vCorners[0]),
                MathUtils.GetPVector(rect.vCorners[1]),
                MathUtils.GetPVector(rect.vCorners[2]),
                MathUtils.GetPVector(rect.vCorners[3])
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

    /*
     * Tracking
     */

    /**
     * Returns absolute pose as a {@link PMatrix3D}
     * May only work for HMD
     *
     * @param deviceId to get the pose for
     * @return absolute pose
     */
    public PMatrix3D GetDeviceToAbsoluteTrackingPose(int deviceId) {
        TrackedDevicePose_t trackedDevicePose_t = trackedDevicePose[deviceId];
        if (trackedDevicePose_t != null)
            return MathUtils.GetPMatrix(trackedDevicePose_t.mDeviceToAbsoluteTracking);
        else
            return null;
    }

    /*
     * Drawing
     */

    private boolean iconsLoaded = false;
    private PImage[] icons;

    /**
     * Gives the ready icon for the selected device
     *
     * @param deviceId to get icon for
     * @return device icon
     */
    public PImage getDeviceIcon(int deviceId) {
        if (!iconsLoaded) {
            // lazy loading icons

            icons = new PImage[k_unMaxTrackedDeviceCount];

            for (int trackedDevice = 0; trackedDevice < icons.length; trackedDevice++) {
                //If the device is not connected, pass.
                if (!hmd.IsTrackedDeviceConnected.apply(trackedDevice))
                    continue;
                String ready = hmd.GetTrackedDevicePropertyString(trackedDevice, Prop_NamedIconPathDeviceReady_String, errorBuffer);
//FIXME icon loading
                String driverPath = VR.VR_RuntimePath() + "drivers/" + hmd.GetTrackedDevicePropertyString(trackedDevice, Prop_ResourceRoot_String, errorBuffer);
                icons[trackedDevice] = parent.loadImage(driverPath + "/resources" + ready.replaceAll("\\{\\w*}", ""));

            }
            iconsLoaded = true;
        }

        return icons[deviceId];
    }


    public String getDeviceName(int deviceId) {
        return hmd.GetTrackedDevicePropertyString(deviceId, Prop_ModelNumber_String, errorBuffer);
    }

    public IVRSystem getHMD() {
        return hmd;
    }

    public IVRCompositor_FnTable getCompositor() {
        return compositor;
    }

    /*
     * Render Models
     */
    public void FindOrLoadRenderModel(String pchRenderModelName) {
      /*  CGLRenderModel * pRenderModel = NULL;
        for (std::vector < CGLRenderModel * >::iterator i = m_vecRenderModels.begin();
        i != m_vecRenderModels.end();
        i++ )
        {
            if (!stricmp(( * i)->GetName().c_str(), pchRenderModelName ) )
            {
                pRenderModel = *i;
                break;
            }
        }

        // load the model if we didn't find one
        if (!pRenderModel) {
            vr::RenderModel_t * pModel;
            vr::EVRRenderModelError error;
            while (1) {
                error = vr::VRRenderModels () -> LoadRenderModel_Async(pchRenderModelName, & pModel );
                if (error != vr::VRRenderModelError_Loading)
                    break;

                ThreadSleep(1);
            }

            if (error != vr::VRRenderModelError_None) {
                dprintf("Unable to load render model %s - %s\n", pchRenderModelName, vr::VRRenderModels
                () -> GetRenderModelErrorNameFromEnum(error) );
                return NULL; // move on to the next tracked device
            }

            vr::RenderModel_TextureMap_t * pTexture;
            while (1) {
                error = vr::VRRenderModels () -> LoadTexture_Async(pModel -> diffuseTextureId, & pTexture );
                if (error != vr::VRRenderModelError_Loading)
                    break;

                ThreadSleep(1);
            }

            if (error != vr::VRRenderModelError_None) {
                dprintf("Unable to load render texture id:%d for render model %s\n", pModel -> diffuseTextureId, pchRenderModelName);
                vr::VRRenderModels () -> FreeRenderModel(pModel);
                return NULL; // move on to the next tracked device
            }

            pRenderModel = new CGLRenderModel(pchRenderModelName);
            if (!pRenderModel -> BInit( * pModel, *pTexture ) )
            {
                dprintf("Unable to create GL model from render model %s\n", pchRenderModelName);
                delete pRenderModel;
                pRenderModel = NULL;
            }
		else
            {
                m_vecRenderModels.push_back(pRenderModel);
            }
            vr::VRRenderModels () -> FreeRenderModel(pModel);
            vr::VRRenderModels () -> FreeTexture(pTexture);
        }
        return pRenderModel;*/
    }
    /*
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
        try {
            postInitMethod = parent.getClass().getMethod("postInit");
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
    }

    public void callButtonPressedEvent(int hand, int buttonId) {
        if (controllerButtonPressMethod != null) {
            try {
                controllerButtonPressMethod.invoke(parent, hand, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonPressed() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonPressMethod = null;
            }
        }
    }

    public void callButtonReleasedEvent(int hand, int buttonId) {
        if (controllerButtonUnpressMethod != null) {
            try {
                controllerButtonUnpressMethod.invoke(parent, hand, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonReleased() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonUnpressMethod = null;
            }
        }
    }

    public void callButtonTouchedEvent(int hand, int buttonId) {
        if (controllerButtonTouchMethod != null) {
            try {
                controllerButtonTouchMethod.invoke(parent, hand, buttonId);
            } catch (Exception e) {
                System.err.println("Disabling buttonTouchedEvent() for " + name + " because of an error.");
                e.printStackTrace();
                controllerButtonTouchMethod = null;
            }
        }
    }

    public void callButtonUntouchedEvent(int hand, int buttonId) {
        if (controllerButtonUntouchMethod != null) {
            try {
                controllerButtonUntouchMethod.invoke(parent, hand, buttonId);
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

    /**
     * Event called after OpenVR is initialized
     */
    private void callPostInit() {
        if (postInitMethod != null) {
            try {
                postInitMethod.invoke(parent);
            } catch (Exception e) {
                System.err.println("Disabling postInit() for " + name + " because of an error.");
                e.printStackTrace();
                postInitMethod = null;
            }
        }
    }

    public void dispose() {
        System.out.println("Dispose is called");
        VR.VR_Shutdown();
    }
}

