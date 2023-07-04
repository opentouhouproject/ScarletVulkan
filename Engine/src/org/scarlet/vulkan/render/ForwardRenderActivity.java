package org.scarlet.vulkan.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.scarlet.vulkan.buffer.CommandBuffer;
import org.scarlet.vulkan.buffer.CommandPool;
import org.scarlet.vulkan.concurrent.Fence;
import org.scarlet.vulkan.concurrent.SyncSemaphores;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.queue.Queue;
import org.scarlet.vulkan.surface.ImageView;
import org.scarlet.vulkan.surface.SwapChain;

import java.nio.LongBuffer;
import java.util.Arrays;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Forward rendering implementation.
 */
public class ForwardRenderActivity {
    /**
     * Swap chain.
     */
    private final SwapChain swapChain;

    /**
     * List of frame buffers.
     */
    private final FrameBuffer[] frameBuffers;

    /**
     * The swap chain render pass.
     */
    private final SwapChainRenderPass renderPass;

    /**
     * The command buffers.
     */
    private final CommandBuffer[] commandBuffers;

    /**
     * The fences.
     */
    private final Fence[] fences;

    /**
     * Constructor.
     * @param swapChain The swap chain.
     */
    public ForwardRenderActivity(SwapChain swapChain, CommandPool commandPool) {
        this.swapChain = swapChain;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LogicalDevice logicalDevice = swapChain.getLogicalDevice();
            VkExtent2D swapChainExtent = swapChain.getSwapChainExtent();
            ImageView[] imageViews = swapChain.getImageViews();
            int numberOfImages = imageViews.length;

            renderPass = new SwapChainRenderPass(swapChain);

            LongBuffer attachments = stack.mallocLong(1);
            frameBuffers = new FrameBuffer[numberOfImages];
            for (int i = 0; i < numberOfImages; i++) {
                attachments.put(0, imageViews[i].getImageView());
                frameBuffers[i] = new FrameBuffer(
                        logicalDevice, swapChainExtent.width(), swapChainExtent.height(),
                        attachments, renderPass.getRenderPass());
            }

            commandBuffers = new CommandBuffer[numberOfImages];
            fences = new Fence[numberOfImages];
            for (int i = 0; i < numberOfImages; i++) {
                commandBuffers[i] = new CommandBuffer(commandPool, true, false);
                fences[i] = new Fence(logicalDevice, true);
                recordCommandBuffer(commandBuffers[i], frameBuffers[i], swapChainExtent.width(), swapChainExtent.height());
            }
        }
    }

    /**
     * Release resources.
     */
    public void cleanup() {
        Arrays.stream(frameBuffers).forEach(FrameBuffer::cleanup);
        renderPass.cleanup();
        Arrays.stream(commandBuffers).forEach(CommandBuffer::cleanup);
        Arrays.stream(fences).forEach(Fence::cleanup);
    }

    /**
     * Submits the command buffer and semaphores to the queue.
     * @param queue The queue to submit to.
     */
    public void submit(Queue queue) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int frameNumber = swapChain.getCurrentFrame();
            CommandBuffer commandBuffer = commandBuffers[frameNumber];
            Fence currentFence = fences[frameNumber];
            currentFence.fenceWait();
            currentFence.reset();
            SyncSemaphores syncSemaphores = swapChain.getSyncSemaphoresList()[frameNumber];
            queue.submit(stack.pointers(commandBuffer.getCommandBuffer()),
                    stack.longs(syncSemaphores.getImageAcquisitionSemaphore().getSemaphore()),
                    stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
                    stack.longs(syncSemaphores.getRenderCompleteSemaphore().getSemaphore()),
                    currentFence);
        }
    }

    /**
     *
     * @param commandBuffer The command buffer.
     * @param frameBuffer The frame buffer.
     * @param width The width.
     * @param height The height.
     */
    private void recordCommandBuffer(CommandBuffer commandBuffer, FrameBuffer frameBuffer, int width, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
            clearValues.apply(0, v -> v.color()
                    .float32(0, 0.5f)
                    .float32(1, 0.7f)
                    .float32(2, 0.9f)
                    .float32(3, 1));

            VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                    .renderPass(renderPass.getRenderPass())
                    .pClearValues(clearValues)
                    .renderArea(a -> a.extent().set(width, height))
                    .framebuffer(frameBuffer.getFrameBuffer());

            commandBuffer.beginRecording();
            vkCmdBeginRenderPass(commandBuffer.getCommandBuffer(), renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
            vkCmdEndRenderPass(commandBuffer.getCommandBuffer());
            commandBuffer.endRecording();
        }
    }
}