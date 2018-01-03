package me.dawars.openvr_for_processing.utils;

import com.jogamp.opengl.math.Matrix4;
import examples.HelloVR.HelloVR;
import processing.core.*;

import static processing.core.PConstants.QUADS;

public class GeometryUtils {

    public static void AddCubeToScene(PMatrix3D mat, PShape shape) {
        // Matrix4 mat( outermat.data() );
/*
         A = new PVector(mat.m03, mat.m13, mat.m23, mat.m33);
        PVector B = new PVector(mat.m00 + mat.m03, mat.m10 + mat.m13, mat.m20 + mat.m23, mat.m30 + mat.m33);
        PVector C = mat * PVector(1, 1, 0, 1);
        PVector D = mat * PVector(0, 1, 0, 1);
        PVector E = mat * PVector(0, 0, 1, 1);
        PVector F = mat * PVector(1, 0, 1, 1);
        PVector G = mat * PVector(1, 1, 1, 1);
        PVector H = mat * PVector(0, 1, 1, 1);

        // triangles instead of quads
        AddCubeVertex(E.x, E.y, E.z, 0, 1, shape); //Front
        AddCubeVertex(F.x, F.y, F.z, 1, 1, shape);
        AddCubeVertex(G.x, G.y, G.z, 1, 0, shape);
        AddCubeVertex(G.x, G.y, G.z, 1, 0, shape);
        AddCubeVertex(H.x, H.y, H.z, 0, 0, shape);
        AddCubeVertex(E.x, E.y, E.z, 0, 1, shape);

        AddCubeVertex(B.x, B.y, B.z, 0, 1, shape); //Back
        AddCubeVertex(A.x, A.y, A.z, 1, 1, shape);
        AddCubeVertex(D.x, D.y, D.z, 1, 0, shape);
        AddCubeVertex(D.x, D.y, D.z, 1, 0, shape);
        AddCubeVertex(C.x, C.y, C.z, 0, 0, shape);
        AddCubeVertex(B.x, B.y, B.z, 0, 1, shape);

        AddCubeVertex(H.x, H.y, H.z, 0, 1, shape); //Top
        AddCubeVertex(G.x, G.y, G.z, 1, 1, shape);
        AddCubeVertex(C.x, C.y, C.z, 1, 0, shape);
        AddCubeVertex(C.x, C.y, C.z, 1, 0, shape);
        AddCubeVertex(D.x, D.y, D.z, 0, 0, shape);
        AddCubeVertex(H.x, H.y, H.z, 0, 1, shape);

        AddCubeVertex(A.x, A.y, A.z, 0, 1, shape); //Bottom
        AddCubeVertex(B.x, B.y, B.z, 1, 1, shape);
        AddCubeVertex(F.x, F.y, F.z, 1, 0, shape);
        AddCubeVertex(F.x, F.y, F.z, 1, 0, shape);
        AddCubeVertex(E.x, E.y, E.z, 0, 0, shape);
        AddCubeVertex(A.x, A.y, A.z, 0, 1, shape);

        AddCubeVertex(A.x, A.y, A.z, 0, 1, shape); //Left
        AddCubeVertex(E.x, E.y, E.z, 1, 1, shape);
        AddCubeVertex(H.x, H.y, H.z, 1, 0, shape);
        AddCubeVertex(H.x, H.y, H.z, 1, 0, shape);
        AddCubeVertex(D.x, D.y, D.z, 0, 0, shape);
        AddCubeVertex(A.x, A.y, A.z, 0, 1, shape);

        AddCubeVertex(F.x, F.y, F.z, 0, 1, shape); //Right
        AddCubeVertex(B.x, B.y, B.z, 1, 1, shape);
        AddCubeVertex(C.x, C.y, C.z, 1, 0, shape);
        AddCubeVertex(C.x, C.y, C.z, 1, 0, shape);
        AddCubeVertex(G.x, G.y, G.z, 0, 0, shape);
        AddCubeVertex(F.x, F.y, F.z, 0, 1, shape);*/
    }

}
