package org.scarlet;

import org.scarlet.vulkan.Renderer;
import org.scarlet.vulkan.Scene;

/**
 * Scarlet Vulkan engine class.
 */
public class Engine {
    private boolean isRunning;
    private ApplicationLogic applicationLogic;
    private Window window;
    private Scene scene;
    private Renderer renderer;

    /**
     * Constructor.
     * @param applicationProperties Application properties.
     * @param applicationLogic Implementation of the application logic.
     */
    public Engine(ApplicationProperties applicationProperties, ApplicationLogic applicationLogic) {
        isRunning = false;
        this.applicationLogic = applicationLogic;
        window = new Window(applicationProperties.getWindowTitle());
        scene = new Scene(window);
        renderer = new Renderer(applicationProperties, window, scene);
        this.applicationLogic.initialize(window, scene, renderer);
    }

    /**
     * Cleans up engine resources.
     */
    private void cleanup() {
        applicationLogic.cleanup();
        renderer.cleanup();
        window.cleanup();
    }

    /**
     * Starts the engine.
     */
    public void start() {
        isRunning = true;
        run();
    }

    /**
     * Stops the engine.
     */
    public void stop() {
        isRunning = false;
    }

    public void run() {
        // Get the engine properties.
        EngineProperties engineProperties = EngineProperties.getInstance();

        // Get the initial time.
        long initialTime = System.nanoTime();

        // Set the target time between frame updates in nanoseconds.
        double timeU = 1000000000d / engineProperties.getUpdatesPerSecond();

        // Set the change in time.
        double deltaU = 0;

        // Set the last update time.
        long updateTime = initialTime;

        // Start the main loop.
        while (isRunning && !window.shouldClose()) {
            // Poll for events.
            this.window.pollEvents();

            // Get the current time.
            long currentTime = System.nanoTime();

            // Calculate the time passed as percentage of the target update time.
            deltaU += (currentTime - initialTime) / timeU;

            // Update the initial time to the current time.
            initialTime = currentTime;

            // Check if a frame update is needed.
            if (deltaU >= 1) {
                // Check the time difference since the last update.
                long diffTimeNanos = currentTime - updateTime;

                // Handle the input.
                applicationLogic.handleInput(window, scene, diffTimeNanos);

                // Update the update time.
                updateTime = currentTime;

                // Decrement the time difference. Guarantees we don't rerun this too soon.
                deltaU--;
            }

            // Render the scene.
            renderer.render(window, scene);
        }

        // Cleanup resources.
        cleanup();
    }
}