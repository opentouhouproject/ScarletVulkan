package org.scarlet;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles mouse input.
 */
public class MouseInput {
    /**
     * Current mouse position.
     */
    private Vector2f currentPosition;

    /**
     * Previous mouse position.
     */
    private Vector2f previousPosition;

    /**
     *
     */
    private Vector2f displayVector;

    /**
     * Boolean flag indicating if mouse is in the window.
     */
    private boolean inWindow;

    /**
     * Boolean flag indicating if the left mouse button was pressed.
     */
    private boolean leftButtonPressed;

    /**
     * Boolean flag indicating if the right mouse button was pressed.
     */
    private boolean rightButtonPressed;

    /**
     * Constructor.
     * @param windowHandle The window handle.
     */
    public MouseInput(long windowHandle) {
        currentPosition = new Vector2f();
        previousPosition = new Vector2f(-1, -1);
        displayVector = new Vector2f();
        inWindow = false;
        leftButtonPressed = false;
        rightButtonPressed = false;

        // Set the cursor position callback.
        glfwSetCursorPosCallback(windowHandle, (handle, xpos, ypos) -> {
            currentPosition.x = (float) xpos;
            currentPosition.y = (float) ypos;
        });

        // Set the cursor entering window callback.
        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> inWindow = entered);

        // Set the mouse button callback.
        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    /**
     * Get the current mouse position.
     * @return Vector2f - The current mouse position.
     */
    public Vector2f getCurrentPos() {
        return currentPosition;
    }

    /**
     * Get the display vector.
     * @return Vector2f - The display vector.
     */
    public Vector2f getDisplayVector() {
        return displayVector;
    }

    /**
     * Get left mouse button press state.
     * @return Boolean - True if LMB was pressed, false otherwise.
     */
    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    /**
     * Get right mouse button press state.
     * @return Boolean - True if RMB was pressed, false otherwise.
     */
    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public void input() {
        // Reset the display vector.
        displayVector.x = 0;
        displayVector.y = 0;

        if (previousPosition.x > 0 && previousPosition.y > 0 && inWindow) {
            // Calculate the change in mouse position.
            double deltaX = currentPosition.x - previousPosition.x;
            double deltaY = currentPosition.y - previousPosition.y;

            // Determine if rotation is needed.
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;

            // Update the display vector.
            if (rotateX) {
                displayVector.y = (float) deltaX;
            }
            if (rotateY) {
                displayVector.x = (float) deltaY;
            }
        }

        // Update the previous mouse position.
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }
}