package planet;

import core.Main;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;
import java.io.File;

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

    public final long seed;

    private final Surface surface;
    private final Liquid liquid;
    private final Gas gas;
    private final Life life;
    private final Ice ice;
    private final Lights lights;
    private final Lighting lighting;

    public Planet(long seed) {
        this.seed = seed;
        Main.app.randomSeed(seed);

        surface = new Surface(seed);
        liquid = new Liquid(seed);
        gas = new Gas(seed);
        ice = new Ice(isCool(liquid), seed);
        lighting = new Lighting(
                ice.shape.equals(Ice.Shape.EyeballSheet) ?
                        0 :
                        Main.app.random(-0.01f, 0.01f),
                seed);
        life = new Life(lighting.star, isHabitable(liquid), seed);
        lights = new Lights(hasComplexLife(life), seed);
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
        displaySecretMessage(HEIGHT + 100 + 30 * 7);
        displaySettings();
    }

    public void savePlanet() {
        createImage().save(new File("").getAbsolutePath() + "/saved planets/" + seed + ".png");
    }

    public void saveScreen() {
        PGraphics graphics = Main.app.createGraphics(Main.WIDTH, Main.HEIGHT);
        graphics.beginDraw();

        graphics.imageMode(PConstants.CENTER);
        graphics.textAlign(PConstants.CENTER);
        graphics.background(0);

        displaySeedToGraphics(graphics);
        graphics.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);
        surface.displayTextToGraphics(HEIGHT + 100, graphics);
        liquid.displayTextToGraphics(HEIGHT + 100 + 30, graphics);
        gas.displayTextToGraphics(HEIGHT + 100 + 30 * 2, graphics);
        ice.displayTextToGraphics(HEIGHT + 100 + 30 * 3, graphics);
        life.displayTextToGraphics(HEIGHT + 100 + 30 * 4, graphics);
        lights.displayTextToGraphics(HEIGHT + 100 + 30 * 5, graphics);
        displayStarTypeToGraphics(HEIGHT + 100 + 30 * 6, graphics);
        displayStarTypeToGraphics(HEIGHT + 100 + 30 * 7, graphics);

        graphics.endDraw();
        graphics.save(new File("").getAbsolutePath() + "/screenshots/" + seed + ".png");
    }

    private void displaySettings() {
        Main.app.textAlign(PConstants.LEFT);
        Main.app.fill(255);

        if (!showClouds) Main.app.text("Clouds hidden", 10, Main.HEIGHT - 10);
        if (dayNightStatus.equals(DayNightStatus.Day)) Main.app.text("Day only", 10, Main.HEIGHT - 10 - 20);
        if (dayNightStatus.equals(DayNightStatus.Night)) Main.app.text("Night only", 10, Main.HEIGHT - 10 - 20);
        if (Main.entryMode) Main.app.text("Entry mode", 10, Main.HEIGHT - 10 - 20 * 2);

        Main.app.textAlign(PConstants.CENTER);
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
                        newColorColor = new Color(lighting.shadow.getRGB());
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
        Main.app.fill(getSeedColor().getRGB());
        Main.app.text("Planet #" + seed + (Main.entryMode ? "_" : ""), Main.WIDTH / 2f, HEIGHT - 100);
    }

    private void displaySeedToGraphics(PGraphics graphics) {
        graphics.textSize(32);
        graphics.fill(getSeedColor().getRGB());
        graphics.text("Planet #" + seed + (Main.entryMode ? "_" : ""), Main.WIDTH / 2f, HEIGHT - 100);
    }

    private void displayStarType(float height) {
        Main.app.fill(255);
        Main.app.textSize(20);
        Main.app.text(lighting.starDescription, Main.WIDTH / 2f, height);
    }

    private void displayStarTypeToGraphics(float height, PGraphics graphics) {
        graphics.fill(255);
        graphics.textSize(20);
        graphics.text(lighting.starDescription, Main.WIDTH / 2f, height);
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

    private Color getSeedColor() {
        switch ("" + seed) {
            case "666" -> {
                return Color.RED;
            }
            // "ASCII"
            case "6583677373" -> {
                return new Color(0x00FF33);
            }
            // "EARTH"
            case "6965828472" -> {
                return new Color(0x006FFF);
            }
            // "MARS"
            case "77658283" -> {
                return new Color(203, 70, 30);
            }
            default -> {
                return Color.YELLOW;
            }
        }
    }

    private String getSecretMessage() {
        switch ("" + seed) {
            case "666" -> {
                return "65-83-67-73-73";
            }
            // "ASCII"
            case "6583677373" -> {
                return "Your Digital Home";
            }
            // "EARTH"
            case "6965828472" -> {
                return "An Uneasy Future";
            }
            // "MARS"
            case "77658283" -> {
                return "False Hope";
            }
            default -> {
                return "";
            }
        }
    }

    private void displaySecretMessage(float height) {
        Main.app.fill(getSeedColor().getRGB());
        Main.app.textSize(20);
        Main.app.text(getSecretMessage(), Main.WIDTH / 2f, height);
    }

    private void displaySecretMessageToGraphics(float height, PGraphics graphics) {
        graphics.fill(getSeedColor().getRGB());
        graphics.textSize(20);
        graphics.text(getSecretMessage(), Main.WIDTH / 2f, height);
    }
}
