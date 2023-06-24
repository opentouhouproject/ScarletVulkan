package org.scarlet.vulkan.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.scarlet.vulkan.surface.SwapChain;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Render pass that outputs to the swap chain.
 */
public class SwapChainRenderPass {
    /**
     * The swap chain to output to.
     */
    private final SwapChain swapChain;

    /**
     * The render pass.
     */
    private final long renderPass;

    /**
     * Constructor.
     * @param swapChain The swap chain to output to.
     */
    public SwapChainRenderPass(SwapChain swapChain) {
        this.swapChain = swapChain;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.calloc(1, stack);

            // Color attachment.
            attachments.get(0)
                    .format(swapChain.getSurfaceFormat().getImageFormat())
                    .samples(VK_SAMPLE_COUNT_1_BIT)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            // Render sub-pass.
            VkAttachmentReference.Buffer colorReference = VkAttachmentReference.calloc(1, stack)
                    .attachment(0)
                    .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer subPass = VkSubpassDescription.calloc(1, stack)
                    .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                    .colorAttachmentCount(colorReference.remaining())
                    .pColorAttachments(colorReference);

            VkSubpassDependency.Buffer subPassDependencies = VkSubpassDependency.calloc(1, stack);
            subPassDependencies.get(0)
                    .srcSubpass(VK_SUBPASS_EXTERNAL)
                    .dstSubpass(0)
                    .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .srcAccessMask(0)
                    .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

            // Create the render pass.
            VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                    .pAttachments(attachments)
                    .pSubpasses(subPass)
                    .pDependencies(subPassDependencies);

            LongBuffer longBuffer = stack.mallocLong(1);
            vkCheck(vkCreateRenderPass(swapChain.getLogicalDevice().getDevice(), renderPassInfo, null, longBuffer),
                    "Failed to create render pass.");
            renderPass = longBuffer.get(0);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        vkDestroyRenderPass(swapChain.getLogicalDevice().getDevice(), renderPass, null);
    }

    /**
     * Get the render pass.
     * @return long - The handle of the render pass.
     */
    public long getRenderPass() {
        return renderPass;
    }
}