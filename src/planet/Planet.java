package planet;

import core.Main;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;

import static processing.core.PConstants.ARGB;

public class Planet {

    public enum DayNightStatus {
        Normal,
        Day,
        Night
    }

    static final float HEIGHT = Main.HEIGHT * 0.35f;
    static final int IMG_SIZE = 16;

    public boolean showClouds = true;
    public DayNightStatus dayNightStatus = DayNightStatus.Normal;

    public final int seed;

    private final Surface surface;
    private final Liquid liquid;
    private final Gas gas;
    private final Life life;
    private final Ice ice;
    private final Lights lights;
    private final Lighting lighting;

    public Planet(int seed) {
        this.seed = seed;
        Main.app.randomSeed(seed);

        surface = new Surface();
        liquid = new Liquid();
        gas = new Gas();
        ice = new Ice(isCool(liquid));
        lighting = new Lighting(
                ice.shape.equals(Ice.Shape.EyeballSheet) ?
                        0 :
                        Main.app.random(-0.01f, 0.01f));
        life = new Life(lighting.star, isHabitable(liquid));
        lights = new Lights(hasComplexLife(life));
    }

    public void update() {
        lighting.update();
    }

    public void display() {
        Main.app.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);

        displaySeed();
        surface.displayText(HEIGHT + 100);
        liquid.displayText(HEIGHT + 100 + 30);
        gas.displayText(HEIGHT + 100 + 30 * 2);
        ice.displayText(HEIGHT + 100 + 30 * 3);
        life.displayText(HEIGHT + 100 + 30 * 4);
        lights.displayText(HEIGHT + 100 + 30 * 5);
        displayStarType(HEIGHT + 100 + 30 * 6);
    }

    private PImage createImage() {
        PImage img = Main.app.createImage(16, 16, ARGB);
        img.loadPixels();

        PImage shadow = lighting.getImage(surface.sprite, dayNightStatus);
        shadow.loadPixels();
        ice.sprite.loadPixels();
        liquid.sprite.loadPixels();
        surface.sprite.loadPixels();
        life.sprite.loadPixels();
        gas.sprite.loadPixels();
        lights.sprite.loadPixels();

        for (int x = 0; x < IMG_SIZE; x++) {
            for (int y = 0; y < IMG_SIZE; y++) {
                int i = x + y * IMG_SIZE;

                // In shadow
                if ((shadow.pixels[i] >> 24 & 255) > 0) {
                    img.pixels[i] = shadow.pixels[i];

                    // Glowy liquids
                    if (liquid.glows &&
                            (ice.sprite.pixels[i] >> 24 & 255) == 0 &&
                            (liquid.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = liquid.sprite.pixels[i];
                    }

                    // City lights
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
                    // Ice
                    if ((ice.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = ice.sprite.pixels[i];
                    // Liquid
                    } else if ((liquid.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = liquid.sprite.pixels[i];
                    // Surface
                    } else {
                        img.pixels[i] = surface.sprite.pixels[i];
                        // Life
                        if ((life.sprite.pixels[i] >> 24 & 255) > 0) {
                            img.pixels[i] = mergeTransparently(life.sprite.pixels[i], img.pixels[i]);
                        }
                    }
                    // Gas
                    if (showClouds && (gas.sprite.pixels[i] >> 24 & 255) > 0) {
                        img.pixels[i] = mergeTransparently(gas.sprite.pixels[i], img.pixels[i]);
                    }
                }
            }
        }

        return img;
    }

    private int mergeTransparently(int newColor, int baseColor) {
        Color newColorColor = new Color(newColor, true);
        int newAlpha = newColorColor.getAlpha();
        newColorColor = new Color(newColorColor.getRGB());
        return Component.mapColor(
                new Color(baseColor), newColorColor,
                newAlpha, 255).getRGB();
    }

    private void displaySeed() {
        Main.app.textSize(32);
        Main.app.fill(Color.YELLOW.getRGB());
        Main.app.text("Planet #" + seed + (Main.entryMode ? "_" : ""), Main.WIDTH / 2f, HEIGHT - 100);
    }

    private void displayStarType(float height) {
        Main.app.fill(255);
        Main.app.textSize(20);
        Main.app.text(lighting.star.name() + " Type Star", Main.WIDTH / 2f, height);
    }

    private boolean isHabitable(Liquid liquid) {
        if (Main.app.random(5) < 1 || liquid.type == null) return true;

        return !liquid.type.equals(Liquid.Type.MoltenMetal) &&
                !liquid.type.equals(Liquid.Type.MoltenRock) &&
                !liquid.type.equals(Liquid.Type.LiquidMethane) &&
                !liquid.type.equals(Liquid.Type.LiquidNitrogen) &&
                !liquid.type.equals(Liquid.Type.Mercury);
    }

    private boolean isCool(Liquid liquid) {
        if (liquid.type == null) return false;

        return !liquid.type.equals(Liquid.Type.MoltenMetal) &&
                !liquid.type.equals(Liquid.Type.MoltenRock);
    }

    private boolean hasComplexLife(Life life) {
        if (life == null || life.type == null || life.shape.equals(Life.Shape.None)) return false;
        if (Main.app.random(5) < 1) return true;

        return life.type.equals(Life.Type.ConiferousPlants) ||
                life.type.equals(Life.Type.DeciduousPlants);
    }
}
