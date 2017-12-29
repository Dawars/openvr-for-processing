package me.dawars.openvr_for_processing.utils;

import vr.VR;

public class ControllerUtils {

    public static class Side {
        public static final int Left = 0;
        public static final int Right = 1;
        public static final int Max = 2;
    }

    public static boolean IsButtonPressedOrTouched(long ulButtonState, int buttonId) {
        return (ulButtonState & VR.ButtonMaskFromId(buttonId)) != 0;
    }
}
