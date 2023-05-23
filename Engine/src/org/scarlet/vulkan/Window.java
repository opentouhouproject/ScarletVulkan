package org.scarlet.vulkan;

import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

/**
 * Represents the application window.
 */
public class Window {
    /**
     * Window handle.
     */
    private long windowHandle;

    /**
     * The window height.
     */
    private int height;

    /**
     * The window width.
     */
    private int width;

    /**
     * GLFW key callback.
     */
    private GLFWKeyCallbackI keyCallback;

    /**
     * Mouse input.
     */
    private MouseInput mouseInput;

    /**
     * Tracks if window was resized.
     */
    private boolean resized;

    /**
     * Constructor.
     * @param title The window title.
     */
    public Window(String title) {
        this(title, null);
    }

    /**
     * Constructor.
     * @param title The window title.
     * @param keyCallback The GLFW key callback.
     */
    public Window(String title, GLFWKeyCallbackI keyCallback) {
        // Initialize GLFW.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Check if Vulkan is minimally available.
        if (!glfwVulkanSupported()) {
            throw new IllegalStateException("Cannot find a compatible Vulkan installable client driver (ICD).");
        }

        // Get the video mode settings.
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        width = videoMode.width();
        height = videoMode.height();

        // Set the window hints.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create the window.
        windowHandle = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window.");
        }

        // Set how window resizing is handled.
        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resize(w, h));

        // Set how key presses are handled.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            // Exit when escape key is released.
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }

            // Pass key press handling to the callback.
            if (keyCallback != null) {
                keyCallback.invoke(window, key, scancode, action, mods);
            }
        });

        // Set how the mouse input is handled.
        mouseInput = new MouseInput(windowHandle);
    }

    /**
     * Cleanup the resources.
     */
    public void cleanup() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    /**
     * Getter for the windowHandle field.
     * @return long - The handle for the window.
     */
    public long getWindowHandle() {
        return windowHandle;
    }

    /**
     * Getter for the height field.
     * @return int - The window height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter for the width field.
     * @return int - The window width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for the mouse input handler.
     * @return MouseInput - The mouse input handler object.
     */
    public MouseInput getMouseInput() {
        return mouseInput;
    }

    /**
     * Getter for the resized field.
     * @return boolean - Flag indicating if window was resized.
     */
    public boolean isResized() {
        return resized;
    }

    /**
     * Setter for the resized field.
     * @param resized The resized state.
     */
    public void setResized(boolean resized) {
        this.resized = resized;
    }

    /**
     * Resize the window dimensions.
     * @param width The new window width.
     * @param height The new window height.
     */
    public void resize(int width, int height) {
        resized = true;
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if a key is pressed.
     * @param keyCode The key code.
     * @return Boolean - True if key is pressed, false otherwise.
     */
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    /**
     * Poll the user input events.
     */
    public void pollEvents() {
        glfwPollEvents();
        mouseInput.input();
    }

    /**
     * Returns the value of the close flag of the specified window.
     * @return Boolean - The window close flag.
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }
}