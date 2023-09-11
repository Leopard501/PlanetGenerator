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
    public static ArrayList<Integer> seeds;

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
        int seed = (int) Main.app.random(1024);
        Main.app.randomSeed(seed);

        seeds.add(seed);
        planet = new Planet(seed);
    }

    @Override
    public void keyReleased() {
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
    }
}