package me.dawars.openvr_for_processing.utils;

import static vr.VR.EVREventType.*;

public class Utils {

    public static String GetVREventName(int event) {
        switch (event) {
            case VREvent_None:
                return "VREvent_None";
            case VREvent_TrackedDeviceActivated:
                return "VREvent_TrackedDeviceActivated";

            case VREvent_TrackedDeviceDeactivated:
                return "VREvent_TrackedDeviceDeactivated";

            case VREvent_TrackedDeviceUpdated:
                return "VREvent_TrackedDeviceUpdated";

            case VREvent_TrackedDeviceUserInteractionStarted:
                return "VREvent_TrackedDeviceUserInteractionStarted";

            case VREvent_TrackedDeviceUserInteractionEnded:
                return "VREvent_TrackedDeviceUserInteractionEnded";

            case VREvent_IpdChanged:
                return "VREvent_IpdChanged";

            case VREvent_EnterStandbyMode:
                return "VREvent_EnterStandbyMode";

            case VREvent_LeaveStandbyMode:
                return "VREvent_LeaveStandbyMode";

            case VREvent_TrackedDeviceRoleChanged:
                return "VREvent_TrackedDeviceRoleChanged";

            case VREvent_WatchdogWakeUpRequested:
                return "VREvent_WatchdogWakeUpRequested";
/*

            case VREvent_LensDistortionChanged:
                return "VREvent_LensDistortionChanged";

            case VREvent_PropertyChanged:
                return "VREvent_PropertyChanged";

            case VREvent_WirelessDisconnect:
                return "VREvent_WirelessDisconnect";

            case VREvent_WirelessReconnect:
                return "VREvent_WirelessReconnect";
*/


            case VREvent_ButtonPress:
                return "VREvent_ButtonPress";
            // data is controller
            case VREvent_ButtonUnpress:
                return "VREvent_ButtonUnpress";
            // data is controller
            case VREvent_ButtonTouch:
                return "VREvent_ButtonTouch";
            // data is controller
            case VREvent_ButtonUntouch:
                return "VREvent_ButtonUntouch";
            // data is controller
/*
            case VREvent_DualAnalog_Press:
                return "VREvent_DualAnalog_Press";
            // data is dualAnalog
            case VREvent_DualAnalog_Unpress:
                return "VREvent_DualAnalog_Unpress;";
            // data is dualAnalog
            case VREvent_DualAnalog_Touch:
                return "VREvent_DualAnalog_Touch";
            // data is dualAnalog
            case VREvent_DualAnalog_Untouch:
                return "VREvent_DualAnalog_Untouch";
            // data is dualAnalog
            case VREvent_DualAnalog_Move:
                return "VREvent_DualAnalog_Move";
// data is dualAnalog
            case VREvent_DualAnalog_ModeSwitch1:
                return "VREvent_DualAnalog_ModeSwitch1";
            // data is dualAnalog
            case VREvent_DualAnalog_ModeSwitch2:
                return "VREvent_DualAnalog_ModeSwitch2";
            // data is dualAnalog
            case VREvent_DualAnalog_Cancel:
                return "VREvent_DualAnalog_Cancel";
            // data is dualAnalog*/

            case VREvent_MouseMove:
                return "VREvent_MouseMove";
            // data is mouse
            case VREvent_MouseButtonDown:
                return "VREvent_MouseButtonDown";
            // data is mouse
            case VREvent_MouseButtonUp:
                return "VREvent_MouseButtonUp";
            // data is mouse
            case VREvent_FocusEnter:
                return "VREvent_FocusEnter";
            // data is overlay
            case VREvent_FocusLeave:
                return "VREvent_FocusLeave";
            // data is overlay
            case VREvent_Scroll:
                return "VREvent_Scroll";
            // data is mouse
            case VREvent_TouchPadMove:
                return "VREvent_TouchPadMove";
            // data is mouse
            case VREvent_OverlayFocusChanged:
                return "VREvent_OverlayFocusChanged";
            // data is overlay, global event

            case VREvent_InputFocusCaptured:
                return "VREvent_InputFocusCaptured";
            // data is process DEPRECATED
            case VREvent_InputFocusReleased:
                return "VREvent_InputFocusReleased";
            // data is process DEPRECATED
            case VREvent_SceneFocusLost:
                return "VREvent_SceneFocusLost";
            // data is process
            case VREvent_SceneFocusGained:
                return "VREvent_SceneFocusGained";
            // data is process
            // The App actually drawing the scene changed (usually to or from the compositor)
            case VREvent_SceneApplicationChanged:
                return "VREvent_SceneApplicationChanged";
            // data is process
            // New app got access to draw the scene
            case VREvent_SceneFocusChanged:
                return "VREvent_SceneFocusChanged";
            // data is process
            case VREvent_InputFocusChanged:
                return "VREvent_InputFocusChanged";
            // data is process
            case VVREvent_SceneApplicationSecondaryRenderingStarted:
                return "VVREvent_SceneApplicationSecondaryRenderingStarted";
            // data is process

            // Sent to the scene application to request hiding render models temporarily
            case VREvent_HideRenderModels:
                return "VREvent_HideRenderModels";

            // Sent to the scene application to request restoring render model visibility
            case VREvent_ShowRenderModels:
                return "VREvent_ShowRenderModels";

/*
            case VREvent_ConsoleOpened:
                return "VREvent_ConsoleOpened";

            case VREvent_ConsoleClosed:
                return "VREvent_ConsoleClosed";*/


            case VREvent_OverlayShown:
                return "VREvent_OverlayShown";

            case VREvent_OverlayHidden:
                return "VREvent_OverlayHidden";

            case VREvent_DashboardActivated:
                return "VREvent_DashboardActivated";

            case VREvent_DashboardDeactivated:
                return "VREvent_DashboardDeactivated";

            case VREvent_DashboardThumbSelected:
                return "VREvent_DashboardThumbSelected";
            // Sent to the overlay manager - data is overlay
            case VREvent_DashboardRequested:
                return "VREvent_DashboardRequested";
            // Sent to the overlay manager - data is overlay
            case VREvent_ResetDashboard:
                return "VREvent_ResetDashboard";
            // Send to the overlay manager
            // data is the notification ID
            case VREvent_RenderToast:
                return "VREvent_RenderToast";
            // Send to the dashboard to render a toast
            // Sent to overlays when a SetOverlayRaw or SetOverlayFromFile call finishes loading
            case VREvent_ImageLoaded:
                return "VREvent_ImageLoaded";

            // Sent to keyboard renderer in the dashboard to invoke it
            case VREvent_ShowKeyboard:
                return "VREvent_ShowKeyboard";

            // Sent to keyboard renderer in the dashboard to hide it
            case VREvent_HideKeyboard:
                return "VREvent_HideKeyboard";

            // Sent to an overlay when IVROverlay::SetFocusOverlay is called on it
            case VREvent_OverlayGamepadFocusGained:
                return "VREvent_OverlayGamepadFocusGained";

            // Send to an overlay when it previously had focus and IVROverlay::SetFocusOverlay is called on something else
            case VREvent_OverlayGamepadFocusLost:
                return "VREvent_OverlayGamepadFocusLost";

            case VREvent_OverlaySharedTextureChanged:
                return "VREvent_OverlaySharedTextureChanged";

            case VREvent_DashboardGuideButtonDown:
                return "VREvent_DashboardGuideButtonDown";

            case VREvent_DashboardGuideButtonUp:
                return "VREvent_DashboardGuideButtonUp";

            case VREvent_ScreenshotTriggered:
                return "VREvent_ScreenshotTriggered";

            case VREvent_ImageFailed:
                return "VREvent_ImageFailed";
/*
            case VREvent_DashboardOverlayCreated:
                return "VREvent_DashboardOverlayCreated";*/

            case VREvent_RequestScreenshot:
                return "VREvent_RequestScreenshot";

            case VREvent_ScreenshotTaken:
                return "VREvent_ScreenshotTaken";

            case VREvent_ScreenshotFailed:
                return "VREvent_ScreenshotFailed";

            case VREvent_SubmitScreenshotToDashboard:
                return "VREvent_SubmitScreenshotToDashboard";

            case VREvent_ScreenshotProgressToDashboard:
                return "VREvent_ScreenshotProgressToDashboard";
/*

            case VREvent_PrimaryDashboardDeviceChanged:
                return "VREvent_PrimaryDashboardDeviceChanged";
*/


            case VREvent_Notification_Shown:
                return "VREvent_Notification_Shown";

            case VREvent_Notification_Hidden:
                return "VREvent_Notification_Hidden";

            case VREvent_Notification_BeginInteraction:
                return "VREvent_Notification_BeginInteraction";

            case VREvent_Notification_Destroyed:
                return "VREvent_Notification_Destroyed";


            case VREvent_Quit:
                return "VREvent_Quit";
            // data is process
            case VREvent_ProcessQuit:
                return "VREvent_ProcessQuit";
            // data is process
            case VREvent_QuitAborted_UserPrompt:
                return "VREvent_QuitAborted_UserPrompt";
            // data is process
            case VREvent_QuitAcknowledged:
                return "VREvent_QuitAcknowledged";
            // data is process
            case VREvent_DriverRequestedQuit:
                return "VREvent_DriverRequestedQuit";
            // The driver has requested that SteamVR shut down

            case VREvent_ChaperoneDataHasChanged:
                return "VREvent_ChaperoneDataHasChanged";

            case VREvent_ChaperoneUniverseHasChanged:
                return "VREvent_ChaperoneUniverseHasChanged";

            case VREvent_ChaperoneTempDataHasChanged:
                return "VREvent_ChaperoneTempDataHasChanged";

            case VREvent_ChaperoneSettingsHaveChanged:
                return "VREvent_ChaperoneSettingsHaveChanged";

            case VREvent_SeatedZeroPoseReset:
                return "VREvent_SeatedZeroPoseReset";


            case VREvent_AudioSettingsHaveChanged:
                return "VREvent_AudioSettingsHaveChanged";


            case VREvent_BackgroundSettingHasChanged:
                return "VREvent_BackgroundSettingHasChanged";

            case VREvent_CameraSettingsHaveChanged:
                return "VREvent_CameraSettingsHaveChanged		";

            case VREvent_ReprojectionSettingHasChanged:
                return "VREvent_ReprojectionSettingHasChanged	";

            case VREvent_ModelSkinSettingsHaveChanged:
                return "VREvent_ModelSkinSettingsHaveChanged	";

            case VREvent_EnvironmentSettingsHaveChanged:
                return "VREvent_EnvironmentSettingsHaveChanged	";
/*
            case VREvent_PowerSettingsHaveChanged:
                return "VREvent_PowerSettingsHaveChanged		";


            case VREvent_EnableHomeAppSettingsHaveChanged:
                return "VREvent_EnableHomeAppSettingsHaveChanged";

            case VREvent_SteamVRSectionSettingChanged:
                return "VREvent_SteamVRSectionSettingChanged";

            case VREvent_LighthouseSectionSettingChanged:
                return "VREvent_LighthouseSectionSettingChanged";

            case VREvent_NullSectionSettingChanged:
                return "VREvent_NullSectionSettingChanged";

            case VREvent_UserInterfaceSectionSettingChanged:
                return "VREvent_UserInterfaceSectionSettingChanged";

            case VREvent_NotificationsSectionSettingChanged:
                return "VREvent_NotificationsSectionSettingChanged";

            case VREvent_KeyboardSectionSettingChanged:
                return "VREvent_KeyboardSectionSettingChanged";

            case VREvent_PerfSectionSettingChanged:
                return "VREvent_PerfSectionSettingChanged";

            case VREvent_DashboardSectionSettingChanged:
                return "VREvent_DashboardSectionSettingChanged";*/


            case VREvent_StatusUpdate:
                return "VREvent_StatusUpdate";


            case VREvent_MCImageUpdated:
                return "VREvent_MCImageUpdated";


            case VREvent_FirmwareUpdateStarted:
                return "VREvent_FirmwareUpdateStarted";

            case VREvent_FirmwareUpdateFinished:
                return "VREvent_FirmwareUpdateFinished";


            case VREvent_KeyboardClosed:
                return "VREvent_KeyboardClosed";

            case VREvent_KeyboardCharInput:
                return "VREvent_KeyboardCharInput";

            case VREvent_KeyboardDone:
                return "VREvent_KeyboardDone";
            // Sent when DONE button clicked on keyboard

            case VREvent_ApplicationTransitionStarted:
                return "VREvent_ApplicationTransitionStarted";

            case VREvent_ApplicationTransitionAborted:
                return "VREvent_ApplicationTransitionAborted";

            case VREvent_ApplicationTransitionNewAppStarted:
                return "VREvent_ApplicationTransitionNewAppStarted";

            case VREvent_ApplicationListUpdated:
                return "VREvent_ApplicationListUpdated";

            case VREvent_ApplicationMimeTypeLoad:
                return "VREvent_ApplicationMimeTypeLoad";
/*
            case VREvent_ApplicationTransitionNewAppLaunchComplete:
                return "VREvent_ApplicationTransitionNewAppLaunchComplete";

            case VREvent_ProcessConnected:
                return "VREvent_ProcessConnected";

            case VREvent_ProcessDisconnected:
                return "VREvent_ProcessDisconnected";*/


            case VREvent_Compositor_MirrorWindowShown:
                return "VREvent_Compositor_MirrorWindowShown";

            case VREvent_Compositor_MirrorWindowHidden:
                return "VREvent_Compositor_MirrorWindowHidden";

            case VREvent_Compositor_ChaperoneBoundsShown:
                return "VREvent_Compositor_ChaperoneBoundsShown";

            case VREvent_Compositor_ChaperoneBoundsHidden:
                return "VREvent_Compositor_ChaperoneBoundsHidden";


            case VREvent_TrackedCamera_StartVideoStream:
                return "VREvent_TrackedCamera_StartVideoStream";

            case VREvent_TrackedCamera_StopVideoStream:
                return "VREvent_TrackedCamera_StopVideoStream";

            case VREvent_TrackedCamera_PauseVideoStream:
                return "VREvent_TrackedCamera_PauseVideoStream";

            case VREvent_TrackedCamera_ResumeVideoStream:
                return "VREvent_TrackedCamera_ResumeVideoStream";
/*
            case VREvent_TrackedCamera_EditingSurface:
                return "VREvent_TrackedCamera_EditingSurface";
*/


            case VREvent_PerformanceTest_EnableCapture:
                return "VREvent_PerformanceTest_EnableCapture";

            case VREvent_PerformanceTest_DisableCapture:
                return "VREvent_PerformanceTest_DisableCapture";

            case VREvent_PerformanceTest_FidelityLevel:
                return "VREvent_PerformanceTest_FidelityLevel";

/*
            case VREvent_MessageOverlay_Closed:
                return "VREvent_MessageOverlay_Closed";

            case VREvent_MessageOverlayCloseRequested:
                return "VREvent_MessageOverlayCloseRequested";*/
            default:
                return "Unknown Event";
        }
    }
}
