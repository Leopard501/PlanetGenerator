package planet;

import core.Main;
import planet.components.Component;
import planet.components.GasGiantClouds;
import planet.components.Lights;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;
import java.io.File;

import static processing.core.PConstants.ARGB;

public class GasGiant extends Planet {

    public GasGiantClouds gasGiantClouds;

    public GasGiant(int seed) {
        super(seed);

        gasGiantClouds = new GasGiantClouds();

        boolean hasProbes = Main.app.random(3) < 1;
        if (hasProbes) {
            lights.shape = Lights.Shape.Campfires;
            lights.type = Lights.Type.LED;
            lights.low = Lights.Type.LED.color.get();
            lights.high = lights.low;
        } else lights.shape = Lights.Shape.None;
        lights.createAssets();
        if (hasProbes) lights.description = "Sparse Probes";
    }

    @Override
    public void display() {
        Main.app.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);

        int descCount = 1;
        displaySeed();
        gasGiantClouds.displayText(HEIGHT + 100);
        if (gas.description != null) {
            gas.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        } if (lights.description != null) {
            lights.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        }
        displayStarType(HEIGHT + 100 + 30 * descCount);
        displaySettings();
    }

    @Override
    public void saveScreen() {
        PGraphics graphics = Main.app.createGraphics(Main.WIDTH, Main.HEIGHT);
        graphics.beginDraw();

        graphics.imageMode(PConstants.CENTER);
        graphics.textAlign(PConstants.CENTER);
        graphics.background(0);

        int descCount = 1;
        displaySeedToGraphics(graphics);
        graphics.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);
        gasGiantClouds.displayTextToGraphics(HEIGHT + 100, graphics);
        if (gas.description != null) {
            gas.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        } if (lights.description != null) {
            lights.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        }
        displayStarTypeToGraphics(HEIGHT + 100 + 30 * descCount, graphics);

        graphics.endDraw();
        graphics.save(new File("").getAbsolutePath() + "/screenshots/" + seed + ".png");
    }

    @Override
    protected PImage createImage() {
        PImage img = Main.app.createImage(16, 16, ARGB);
        img.loadPixels();

        PImage shadow = lighting.getImage(surface.sprite, dayNightStatus);
        shadow.loadPixels();
        gasGiantClouds.sprite.loadPixels();
        gas.sprite.loadPixels();
        lights.sprite.loadPixels();

        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int i = x + y * IMG_SIZE;
                // In shadow
                if ((shadow.pixels[i] >> 24 & 255) > 0) {
                    img.pixels[i] = shadow.pixels[i];
                    // Glow
                    float alpha = 255 - new Color(gasGiantClouds.baseSprite.pixels[i]).getRed();
                    alpha *= gasGiantClouds.glowLevel;
                    img.pixels[i] = Component.mapColor(
                            new Color(img.pixels[i]), GasGiantClouds.GLOW_COLOR,
                            alpha, 255).getRGB();
                    // Probe lights
                    if ((ice.sprite.pixels[i] >> 24 & 255) == 0 &&
                            (liquid.sprite.pixels[i] >> 24 & 255) == 0 &&
                            (lights.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = mergeTransparently(lights.sprite.pixels[i], img.pixels[i]);
                    }
                    // Dark clouds
                    if (showClouds && (gas.sprite.pixels[i] >> 24 & 255) > 0) {
                        Color newColorColor = new Color(gas.sprite.pixels[i], true);
                        int newAlpha = newColorColor.getAlpha();
                        newColorColor = new Color(lighting.star.shadow.getRGB());
                        img.pixels[i] = Component.mapColor(
                                new Color(img.pixels[i]), newColorColor,
                                newAlpha, 255).getRGB();
                    }
                // Not in shadow
                } else {
                    // Gas Giant Clouds
                    img.pixels[i] = gasGiantClouds.sprite.pixels[i];
                    // Gas
                    if (showClouds && (gas.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = mergeTransparently(gas.sprite.pixels[i], img.pixels[i]);
                    }
                }
            }
        }

        return img;
    }
}
