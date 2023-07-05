package org.scarlet.vulkan.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Links attachments to the render pass.
 */
public class FrameBuffer {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * The handle to the frame buffer.
     */
    private final long frameBuffer;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     * @param width Frame width.
     * @param height Frame height.
     * @param attachments Attachments.
     * @param renderPass The render pass.
     */
    public FrameBuffer(LogicalDevice logicalDevice, int width, int height, LongBuffer attachments, long renderPass) {
        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFramebufferCreateInfo frameBufferInfo = VkFramebufferCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .pAttachments(attachments)
                    .width(width)
                    .height(height)
                    .layers(1)
                    .renderPass(renderPass);

            LongBuffer longBuffer = stack.mallocLong(1);
            vkCheck(vkCreateFramebuffer(logicalDevice.getDevice(), frameBufferInfo, null, longBuffer),
                    "Failed to create FrameBuffer.");
            frameBuffer = longBuffer.get(0);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        vkDestroyFramebuffer(logicalDevice.getDevice(), frameBuffer, null);
    }

    /**
     * Get the handle to the frame buffer.
     * @return long - The handle to the frame buffer.
     */
    public long getFrameBuffer() {
        return frameBuffer;
    }
}