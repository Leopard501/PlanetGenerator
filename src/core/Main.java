package core;

import planet.Planet;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends PApplet {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    public static PApplet app;
    public static HashMap<String, PImage> sprites;
    public static ArrayList<Long> seeds;

    public static boolean entryMode;

    private Planet planet;
    private int seedIdx;

    public static void main(String[] args) {
        PApplet.main("core.Main", args);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        noSmooth();

        app = this;
    }

    @Override
    public void setup() {
        imageMode(CENTER);
        textAlign(CENTER);
        surface.setTitle("Planet Generator");

        sprites = Loader.loadSprites();
        seeds = new ArrayList<>();

        genPlanet();
    }

    @Override
    public void draw() {
        update();
        display();
    }

    private void update() {
        planet.update();
    }

    private void display() {
        background(0);
        planet.display();
    }

    private void genPlanet() {
        Main.app.randomSeed(Main.app.millis());
        long seed = (int) Main.app.random(1024);
        Main.app.randomSeed(seed);

        seeds.add(seed);
        planet = new Planet(seed);
    }

    @Override
    public void keyReleased() {
        if (entryMode) {
            if (key == '\n') {
                entryMode = false;
                seeds.add(planet.seed);
                seedIdx++;
            }
            if ((int) key >= 48 && (int) key <= 57) {
                int entered = ((int) key) - 48;
                long newSeed = planet.seed;

                if (newSeed == 0) newSeed = entered;
                else {
                    newSeed *= 10;
                    newSeed += entered;
                }

                planet = new Planet(newSeed);
            } else if (((int) key >= 97 && (int) key <= 122)) {
                int entered = ((int) key) - 32;
                long newSeed = planet.seed;

                if (newSeed == 0) newSeed = entered;
                else {
                    newSeed *= 100;
                    newSeed += entered;
                }

                planet = new Planet(newSeed);
            }
            return;
        }
        if (key == ' ') {
            seedIdx++;
            if (seedIdx == seeds.size()) genPlanet();
            else planet = new Planet(seeds.get(seedIdx));
        }
        if (key == 'b') {
            if (seedIdx > 0) seedIdx--;
            planet = new Planet(seeds.get(seedIdx));
        }
        if (key == 'c') planet.showClouds = !planet.showClouds;
        if (key == 'd') {
            if (planet.dayNightStatus.equals(Planet.DayNightStatus.Day)) {
                planet.dayNightStatus = Planet.DayNightStatus.Normal;
            } else {
                planet.dayNightStatus = Planet.DayNightStatus.Day;
            }
        }
        if (key == 'n') {
            if (planet.dayNightStatus.equals(Planet.DayNightStatus.Night)) {
                planet.dayNightStatus = Planet.DayNightStatus.Normal;
            } else {
                planet.dayNightStatus = Planet.DayNightStatus.Night;
            }
        }
        if (key == 'e' || key == '\n') {
            entryMode = true;
            planet = new Planet(0);
        }
        if (key == 'p') {
            planet.savePlanet();
        }
        if (key == 's') {
            planet.saveScreen();
        }
    }
}