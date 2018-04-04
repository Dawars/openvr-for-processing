package examples.HelloVR;

import me.dawars.openvr_for_processing.OpenVRLibrary;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class HelloVR extends PApplet {

    private PShape cube;
    private PImage texture;
    private PShader texShader;

    private OpenVRLibrary openVR;

    private float m_fScale = 0.3f;
    private int m_iSceneVolumeWidth = 20;
    private int m_iSceneVolumeHeight = 20;
    private int m_iSceneVolumeDepth = 20;
    private float m_fScaleSpacing = 4;


    @Override
    public void settings() {
        size(1280, 720, P3D);
    }

    @Override
    public void setup() {
        frameRate(90);
        OpenVRLibrary.debugRenderer = OpenVRLibrary.OVR;
        openVR = new OpenVRLibrary(this);
        // shaders, textures

        texture = loadImage("cube_texture.png");
        texShader = loadShader("texfrag.glsl", "texvert.glsl");

        // create cube

        textureMode(NORMAL);
        cube = createShape();
        cube.beginShape(QUADS);
        cube.noStroke();
        cube.texture(texture);
// Front Face
        cube.vertex(-0.5f, -0.5f, 0.5f, 0.0f, 0.0f); // Bottom Left Of The Texture and Quad
        cube.vertex(0.5f, -0.5f, 0.5f, 1f, 0.0f); // Bottom Right Of The Texture and Quad
        cube.vertex(0.5f, 0.5f, 0.5f, 1f, 1f); // Top Right Of The Texture and Quad
        cube.vertex(-0.5f, 0.5f, 0.5f, 0.0f, 1f); // Top Left Of The Texture and Quad

        // Back Face
        cube.vertex(-0.5f, -0.5f, -0.5f, 1f, 0.0f); // Bottom Right Of The Texture and Quad
        cube.vertex(-0.5f, 0.5f, -0.5f, 1f, 1f); // Top Right Of The Texture and Quad
        cube.vertex(0.5f, 0.5f, -0.5f, 0.0f, 1f); // Top Left Of The Texture and Quad
        cube.vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.0f); // Bottom Left Of The Texture and Quad

        // Top Face
        cube.vertex(-0.5f, 0.5f, -0.5f, 0.0f, 1f); // Top Left Of The Texture and Quad
        cube.vertex(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f); // Bottom Left Of The Texture and Quad
        cube.vertex(0.5f, 0.5f, 0.5f, 1f, 0.0f); // Bottom Right Of The Texture and Quad
        cube.vertex(0.5f, 0.5f, -0.5f, 1f, 1f); // Top Right Of The Texture and Quad

        // Bottom Face
        cube.vertex(-0.5f, -0.5f, -0.5f, 1f, 1f);    // Top Right Of The Texture and Quad
        cube.vertex(0.5f, -0.5f, -0.5f, 0.0f, 1f);    // Top Left Of The Texture and Quad
        cube.vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.0f);    // Bottom Left Of The Texture and Quad
        cube.vertex(-0.5f, -0.5f, 0.5f, 1f, 0.0f);    // Bottom Right Of The Texture and Quad

        // Right face
        cube.vertex(0.5f, -0.5f, -0.5f, 1f, 0.0f);    // Bottom Right Of The Texture and Quad
        cube.vertex(0.5f, 0.5f, -0.5f, 1f, 1f);    // Top Right Of The Texture and Quad
        cube.vertex(0.5f, 0.5f, 0.5f, 0.0f, 1f);    // Top Left Of The Texture and Quad
        cube.vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.0f);    // Bottom Left Of The Texture and Quad

        // Left Face
        cube.vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.0f);    // Bottom Left Of The Texture and Quad
        cube.vertex(-0.5f, -0.5f, 0.5f, 1f, 0.0f);    // Bottom Right Of The Texture and Quad
        cube.vertex(-0.5f, 0.5f, 0.5f, 1f, 1f);    // Top Right Of The Texture and Quad
        cube.vertex(-0.5f, 0.5f, -0.5f, 0.0f, 1f);    // Top Left Of The Texture and Quad


        cube.endShape();


        noStroke();
        color(127f);
        tint(127f);
        fill(127f);
    }

    @Override
    public void draw() {
        background(0);
        scale(1, -1, 1); // convert Processing's left-handed coord system

//        camera(0, 0, 60, 0, 0, 0, 0, 1, 0);
        /*
        fill(255, 0, 0);
        sphere(0.5f);

        fill(255f);
*/
        shader(texShader);

        scale(m_fScale, m_fScale, m_fScale);

        float startX = -((float) m_iSceneVolumeWidth * m_fScaleSpacing) / 2.f;
        float startY = -((float) m_iSceneVolumeHeight * m_fScaleSpacing) / 2.f;
        float startZ = -((float) m_iSceneVolumeDepth * m_fScaleSpacing) / 2.f;

        for (int z = 0; z < m_iSceneVolumeDepth; z++) {
            for (int y = 0; y < m_iSceneVolumeHeight; y++) {
                for (int x = 0; x < m_iSceneVolumeWidth; x++) {
                    pushMatrix();
                    // todo check processing and openvr coord system
                    translate(startX + x * m_fScaleSpacing, startY + y * m_fScaleSpacing, startZ + z * m_fScaleSpacing);
                    cube.draw(g);
//                    box(1);
                    popMatrix();
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(HelloVR.class);
    }
}