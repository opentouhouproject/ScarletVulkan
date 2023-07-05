package org.scarlet.vulkan.buffer;

/**
 * Records inheritance information.
 */
public class InheritanceInformation {
    /**
     * The render pass.
     */
    private long renderPass;

    /**
     * The frame buffer.
     */
    private long frameBuffer;

    /**
     * The sub-pass.
     */
    private int subPass;

    /**
     * Constructor.
     * @param renderPass The render pass.
     * @param frameBuffer The frame buffer.
     * @param subPass The sub-pass.
     */
    public InheritanceInformation(long renderPass, long frameBuffer, int subPass) {
        this.renderPass = renderPass;
        this.frameBuffer = frameBuffer;
        this.subPass = subPass;
    }

    /**
     * Get the render pass.
     * @return long - The handle to the render pass.
     */
    public long getRenderPass() {
        return renderPass;
    }

    /**
     * Get the frame buffer.
     * @return long - The handle to the frame buffer.
     */
    public long getFrameBuffer() {
        return frameBuffer;
    }

    /**
     * Get the sub pass.
     * @return int - The sub pass.
     */
    public int getSubPass() {
        return subPass;
    }
}