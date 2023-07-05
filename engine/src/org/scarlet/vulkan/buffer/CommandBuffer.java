package org.scarlet.vulkan.buffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineLogger;

import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * A buffer that stores recorded commands.
 */
public class CommandBuffer {
    /**
     * The command pool.
     */
    private final CommandPool commandPool;

    /**
     * Flag indicating if one time submit.
     */
    private final boolean oneTimeSubmit;

    /**
     * Handle to the command buffer.
     */
    private final VkCommandBuffer commandBuffer;

    /**
     * Flag indicating if it is a primary level command buffer.
     */
    private boolean primary;

    /**
     * Constructor.
     * @param commandPool The command pool.
     * @param primary Flag indicating primary level.
     * @param oneTimeSubmit Flag indicating one time submission.
     */
    public CommandBuffer(CommandPool commandPool, boolean primary, boolean oneTimeSubmit) {
        EngineLogger.getInstance().log(Level.INFO, "Creating command buffer.");

        this.commandPool = commandPool;
        this.primary = primary;
        this.oneTimeSubmit = oneTimeSubmit;
        VkDevice device = commandPool.getDevice().getDevice();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo commandBufferAllocateInformation = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool.getCommandPool())
                    .level(primary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    .commandBufferCount(1);
            PointerBuffer pointerBuffer = stack.mallocPointer(1);
            vkCheck(vkAllocateCommandBuffers(device, commandBufferAllocateInformation, pointerBuffer),
                    "Failed to allocate render command buffer.");
            commandBuffer = new VkCommandBuffer(pointerBuffer.get(0), device);
        }
    }

    /**
     * Begin the command buffer.
     */
    public void beginRecording() {
        beginRecording(null);
    }

    /**
     * Begin the command buffer.
     * @param inheritanceInformation The inheritance information for secondary buffers.
     */
    public void beginRecording(InheritanceInformation inheritanceInformation) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo commandBufferBeginInfo = VkCommandBufferBeginInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            if (oneTimeSubmit) {
                commandBufferBeginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }
            if (!primary) {
                if (inheritanceInformation == null) {
                    throw new RuntimeException("Secondary buffers must declare inheritance information.");
                }
                VkCommandBufferInheritanceInfo inheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack)
                        .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO)
                        .renderPass(inheritanceInformation.getRenderPass())
                        .subpass(inheritanceInformation.getSubPass())
                        .framebuffer(inheritanceInformation.getFrameBuffer());
                commandBufferBeginInfo.pInheritanceInfo(inheritanceInfo);
                commandBufferBeginInfo.flags(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT);
            }
            vkCheck(vkBeginCommandBuffer(commandBuffer, commandBufferBeginInfo),
                    "Failed to begin command buffer.");
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying command buffer.");
        vkFreeCommandBuffers(commandPool.getDevice().getDevice(), commandPool.getCommandPool(), commandBuffer);
    }

    /**
     * End the command recording.
     */
    public void endRecording() {
        vkCheck(vkEndCommandBuffer(commandBuffer),
                "Failed to end command buffer.");
    }

    /**
     * Get the command buffer.
     * @return VkCommandBuffer - The command buffer.
     */
    public VkCommandBuffer getCommandBuffer() {
        return commandBuffer;
    }

    /**
     * Reset the command buffer.
     */
    public void reset() {
        vkResetCommandBuffer(commandBuffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }
}