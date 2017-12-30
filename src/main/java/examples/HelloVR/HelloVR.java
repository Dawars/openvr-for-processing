package examples.HelloVR;

import me.dawars.openvr_for_processing.OpenVRLibrary;
import processing.core.PApplet;
import processing.core.PImage;

public class HelloVR extends PApplet {

    private PImage texture;

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
        openVR = new OpenVRLibrary(this);

        // shaders, textures

        texture = loadImage("cube_texture.png");

        color(127f);
        fill(127f);
    }

    @Override
    public void draw() {
        background(0);

        sphere(0.01f);

        scale(m_fScale, m_fScale, m_fScale);

        float startX = -((float) m_iSceneVolumeWidth * m_fScaleSpacing) / 2.f;
        float startY = -((float) m_iSceneVolumeHeight * m_fScaleSpacing) / 2.f;
        float startZ = -((float) m_iSceneVolumeDepth * m_fScaleSpacing) / 2.f;

        for (int z = 0; z < m_iSceneVolumeDepth; z++) {
            for (int y = 0; y < m_iSceneVolumeHeight; y++) {
                for (int x = 0; x < m_iSceneVolumeWidth; x++) {
                    pushMatrix();
                    translate(startX + x * m_fScaleSpacing, startY + y * m_fScaleSpacing, startZ + z * m_fScaleSpacing);
                    box(1);
                    popMatrix();
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(HelloVR.class);
    }
}
