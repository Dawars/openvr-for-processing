package me.dawars.openvr_for_processing.utils;

import vr.VR;

public class ControllerUtils {

    public static class Hand {
        public static final int INVALID = -1;
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int MAX = 2;
    }

    public static boolean IsButtonPressedOrTouched(long ulButtonState, int buttonId) {
        return (ulButtonState & VR.ButtonMaskFromId(buttonId)) != 0;
    }
}
