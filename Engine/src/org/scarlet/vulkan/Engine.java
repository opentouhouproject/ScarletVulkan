package org.scarlet.vulkan;

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
     * @param windowTitle The window title.
     * @param applicationLogic Implementation of the application logic.
     */
    public Engine(String windowTitle, ApplicationLogic applicationLogic) {
        isRunning = false;
        this.applicationLogic = applicationLogic;
        window = new Window(windowTitle);
        scene = new Scene(window);
        renderer = new Renderer(window, scene);
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
        long initialTime = System.nanoTime();
        double timeU = 1000000000d / engineProperties.getUps();
        double deltaU = 0;

        long updateTime = initialTime;

        // Start the main loop.
        while (isRunning && !window.shouldClose()) {
            // Poll for events.
            this.window.pollEvents();

            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            initialTime = currentTime;

            if (deltaU >= 1) {
                long diffTimeNanos = currentTime - updateTime;
                applicationLogic.handleInput(window, scene, diffTimeNanos);
                updateTime = currentTime;
                deltaU--;
            }

            renderer.render(window, scene);
        }

        cleanup();
    }
}