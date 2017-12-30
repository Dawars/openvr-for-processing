package me.dawars.openvr_for_processing.utils;

import processing.core.PMatrix3D;
import processing.core.PVector;
import vr.HmdMatrix34_t;
import vr.HmdMatrix44_t;
import vr.HmdVector3_t;

public class MathUtils {
    /**
     * Converts {@link HmdMatrix34_t} to {@link PMatrix3D}
     *
     * @param mat OpenVR matrix
     * @return Processing matrix
     */
    public static PMatrix3D GetPMatrix(HmdMatrix34_t mat) {
        return new PMatrix3D(
                mat.m[0], mat.m[1], mat.m[2], mat.m[3],
                mat.m[4], mat.m[5], mat.m[6], mat.m[7],
                mat.m[8], mat.m[9], mat.m[10], mat.m[11],
                0, 0, 0, 1
        );
    }

    /**
     * Converts {@link HmdMatrix44_t} to {@link PMatrix3D}
     *
     * @param mat OpenVR matrix
     * @return Processing matrix
     */
    public static PMatrix3D GetPMatrix(HmdMatrix44_t mat) {
        return new PMatrix3D(
                mat.m[0], mat.m[1], mat.m[2], mat.m[3],
                mat.m[4], mat.m[5], mat.m[6], mat.m[7],
                mat.m[8], mat.m[9], mat.m[10], mat.m[11],
                mat.m[12], mat.m[13], mat.m[14], mat.m[15]
        );
    }

    /**
     * Converts {@link HmdVector3_t} to {@link PVector}
     *
     * @param vec OpenVR vector
     * @return Processing vector
     */
    public static PVector GetPVector(HmdVector3_t vec) {
        return new PVector(vec.v[0], vec.v[1], vec.v[2]);
    }

    /**
     * Get the vector representing the position
     */
    public static PVector GetPosition(PMatrix3D mat) {
        return new PVector(mat.m03, mat.m13, mat.m23);
    }

    /**
     * Get the rotation around the X axel
     *
     * @param mat pose matrix
     * @return angle in radians
     */
    public static float GetRotationX(PMatrix3D mat) {
        return (float) Math.atan2(mat.m21, mat.m22);
    }

    /**
     * Get the rotation around the Y axel
     *
     * @param mat pose matrix
     * @return angle in radians
     */
    public static float GetRotationY(PMatrix3D mat) {
        return (float) Math.atan2(-mat.m20, Math.sqrt(mat.m21 * mat.m21 + mat.m22 * mat.m22));
    }

    /**
     * Get the rotation around the Z axel
     *
     * @param mat pose matrix
     * @return angle in radians
     */
    public static float GetRotationZ(PMatrix3D mat) {
        return (float) Math.atan2(mat.m10, mat.m00);
    }
}
