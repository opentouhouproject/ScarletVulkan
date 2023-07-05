package org.scarlet;

import org.scarlet.vulkan.Renderer;
import org.scarlet.vulkan.Scene;

/**
 * Interface for the main application.
 */
public interface ApplicationLogic {
    /**
     * Handles the initialization of the application.
     * @param window
     * @param scene
     * @param renderer
     */
    public void initialize(Window window, Scene scene, Renderer renderer);

    /**
     * Handles resource cleanup of the application.
     */
    public void cleanup();

    /**
     * Handles the user input for the application.
     * @param window
     * @param scene
     * @param diffTimeMillis
     */
    public void handleInput(Window window, Scene scene, long diffTimeMillis);
}