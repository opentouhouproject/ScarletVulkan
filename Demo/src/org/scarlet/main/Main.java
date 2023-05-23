package org.scarlet.main;

import org.scarlet.vulkan.ApplicationLogic;
import org.scarlet.vulkan.Engine;
import org.scarlet.vulkan.Renderer;
import org.scarlet.vulkan.Scene;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class.
 */
public class Main implements ApplicationLogic {
    private static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger("Demo");
        logger.log(Level.INFO, "Starting demo application.");
        Engine engine = new Engine("Scarlet Vulkan Demo", new Main());
        engine.start();
    }

    @Override
    public void initialize(Window window, Scene scene, Renderer renderer) {
        // To be implemented.
    }

    @Override
    public void cleanup() {
        // To be implemented.
    }

    @Override
    public void handleInput(Window window, Scene scene, long diffTimeMillis) {
        // To be implemented.
    }
}