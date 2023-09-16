package planet;

import core.Main;
import planet.components.*;
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
    public static final int IMG_SIZE = 16;

    public boolean showClouds = true;
    public DayNightStatus dayNightStatus = DayNightStatus.Normal;

    public final int seed;

    public Surface surface;
    public Liquid liquid;
    public Gas gas;
    public Life life;
    public Ice ice;
    public Lights lights;
    public Lighting lighting;
    public Moon[] moons;

    public Planet(int seed) {
        this.seed = seed;
        Main.app.randomSeed(seed);

        surface = new Surface();
        liquid = new Liquid();
        gas = new Gas();
        ice = new Ice(isCool(liquid));
        float rotationSpeed = ice.shape.equals(Ice.Shape.EyeballSheet) ?
                0 : Main.app.random(-0.01f, 0.01f);
        lighting = new Lighting(rotationSpeed);
        life = new Life(lighting.star, isHabitable(liquid));
        lights = new Lights(hasComplexLife(life));

        Main.distribute(
                () -> moons = new Moon[(int) Main.app.random(1)],
                () -> moons = new Moon[(int) Main.app.random(1, 2)],
                () -> moons = new Moon[(int) Main.app.random(2, 3)]
        );

        moons = new Moon[(int) Main.app.random(5)];
        for (int i = 0; i < moons.length; i++) {
            moons[i] = new Moon(
                    surface.type != null && surface.type.equals(Surface.Type.Ice),
                    rotationSpeed);
        }
    }

    public static Planet createPlanet(int seed) {
        Main.app.randomSeed(seed);
        if (Main.app.random(4) < 1) return new BarrenPlanet(seed);
        if (Main.app.random(4) < 1) return new GasGiant(seed);

        return new Planet(seed);
    }

    public void update() {
        lighting.update();
        for (Moon moon : moons) moon.update();
    }

    public void display() {
        for (Moon moon : moons) if (!moon.isBehind) {
            Main.app.image(moon.sprite,
                    Math.round((moon.screenPosition + Main.WIDTH / 2f) / 10) * 10,
                    HEIGHT, 160, 160);
        }
        Main.app.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);
        for (int i = moons.length - 1; i >= 0; i--) {
            Moon moon = moons[i];
            if (moon.isBehind) {
                Main.app.image(moon.sprite,
                        Math.round((moon.screenPosition + Main.WIDTH / 2f) / 10) * 10,
                        HEIGHT, 160, 160);
            }
        }

        int descCount = 1;
        displaySeed();
        surface.displayText(HEIGHT + 100);
        if (liquid.description != null) {
            liquid.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        } if (gas.description != null) {
            gas.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        } if (ice.description != null) {
            ice.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        } if (life.description != null) {
            life.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        } if (lights.description != null) {
            lights.displayText(HEIGHT + 100 + 30 * descCount);
            descCount++;
        }
        displayStarType(HEIGHT + 100 + 30 * descCount);
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

        int descCount = 1;
        displaySeedToGraphics(graphics);
        graphics.image(createImage(), Main.WIDTH / 2f, HEIGHT, 160, 160);
        surface.displayTextToGraphics(HEIGHT + 100, graphics);
        if (liquid.description != null) {
            liquid.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        } if (gas.description != null) {
            gas.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        } if (ice.description != null) {
            ice.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        } if (life.description != null) {
            life.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        } if (lights.description != null) {
            lights.displayTextToGraphics(HEIGHT + 100 + 30 * descCount, graphics);
            descCount++;
        }
        displayStarTypeToGraphics(HEIGHT + 100 + 30 * descCount, graphics);

        graphics.endDraw();
        graphics.save(new File("").getAbsolutePath() + "/screenshots/" + seed + ".png");
    }

    protected void displaySettings() {
        Main.app.textAlign(PConstants.LEFT);

        if (!showClouds) Main.app.text("Clouds hidden", 10, Main.HEIGHT - 10);
        if (dayNightStatus.equals(DayNightStatus.Day)) Main.app.text("Day only", 10, Main.HEIGHT - 10 - 20);
        if (dayNightStatus.equals(DayNightStatus.Night)) Main.app.text("Night only", 10, Main.HEIGHT - 10 - 20);
        if (Main.entryMode) Main.app.text("Entry mode", 10, Main.HEIGHT - 10 - 20 * 2);

        Main.app.textAlign(PConstants.CENTER);
    }

    protected PImage createImage() {
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
                        img.pixels[i] = planet.components.Component.mapColor(
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

    protected int mergeTransparently(int newColor, int baseColor) {
        Color newColorColor = new Color(newColor, true);
        int newAlpha = newColorColor.getAlpha();
        newColorColor = new Color(newColorColor.getRGB());
        return planet.components.Component.mapColor(
                new Color(baseColor), newColorColor,
                newAlpha, 255).getRGB();
    }

    protected void displaySeed() {
        Main.app.textSize(32);
        Main.app.fill(Color.YELLOW.getRGB());
        Main.app.text("Planet #" + seed + (Main.entryMode ? "_" : ""), Main.WIDTH / 2f, HEIGHT - 100);
    }

    protected void displaySeedToGraphics(PGraphics graphics) {
        graphics.textSize(32);
        graphics.fill(Color.YELLOW.getRGB());
        graphics.text("Planet #" + seed + (Main.entryMode ? "_" : ""), Main.WIDTH / 2f, HEIGHT - 100);
    }

    protected void displayStarType(float height) {
        Main.app.fill(255);
        Main.app.textSize(20);
        Main.app.text(lighting.star.name() + " Type Star", Main.WIDTH / 2f, height);
    }

    protected void displayStarTypeToGraphics(float height, PGraphics graphics) {
        graphics.fill(255);
        graphics.textSize(20);
        graphics.text(lighting.star.name() + " Type Star", Main.WIDTH / 2f, height);
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
