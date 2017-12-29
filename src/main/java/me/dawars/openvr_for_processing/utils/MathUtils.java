package me.dawars.openvr_for_processing.utils;

import processing.core.PMatrix3D;
import processing.core.PVector;
import vr.HmdMatrix34_t;
import vr.HmdVector3_t;

public class MathUtils {
    /**
     * Converts {@link HmdMatrix34_t} to {@link PMatrix3D}
     *
     * @param mat OpenVR matrix
     * @return Processing matrix
     */
    public static PMatrix3D GetMatrix(HmdMatrix34_t mat) {
        return new PMatrix3D(
                mat.m[0], mat.m[1], mat.m[2], mat.m[3],
                mat.m[4], mat.m[5], mat.m[6], mat.m[7],
                mat.m[8], mat.m[9], mat.m[10], mat.m[11],
                0, 0, 0, 1
        );
    }

    /**
     * Get the vector representing the position
     */
    public static PVector GetPosition(HmdMatrix34_t matrix) {
        PMatrix3D mat = GetMatrix(matrix);

        return new PVector(mat.m03, mat.m13, mat.m23);
    }

    /**
     * Converts {@link HmdVector3_t} to {@link PVector}
     *
     * @param vec OpenVR vector
     * @return Processing vector
     */
    public static PVector GetVector(HmdVector3_t vec) {
        return new PVector(vec.v[0], vec.v[1], vec.v[2]);
    }
}
