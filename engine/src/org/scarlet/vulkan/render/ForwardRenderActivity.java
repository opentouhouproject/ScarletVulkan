package org.scarlet.vulkan.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineProperties;
import org.scarlet.vulkan.buffer.CommandBuffer;
import org.scarlet.vulkan.buffer.CommandPool;
import org.scarlet.vulkan.concurrent.Fence;
import org.scarlet.vulkan.concurrent.SyncSemaphores;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.model.VertexBufferStructure;
import org.scarlet.vulkan.model.VulkanMesh;
import org.scarlet.vulkan.model.VulkanModel;
import org.scarlet.vulkan.pipeline.Pipeline;
import org.scarlet.vulkan.pipeline.PipelineCache;
import org.scarlet.vulkan.pipeline.PipelineCreationInformation;
import org.scarlet.vulkan.queue.Queue;
import org.scarlet.vulkan.shader.ShaderCompiler;
import org.scarlet.vulkan.shader.ShaderModuleData;
import org.scarlet.vulkan.shader.ShaderProgram;
import org.scarlet.vulkan.surface.ImageView;
import org.scarlet.vulkan.surface.SwapChain;

import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Forward rendering implementation.
 */
public class ForwardRenderActivity {
    /**
     * Fragment shader location.
     */
    private static final String FRAGMENT_SHADER_FILE_GLSL = "resources/shaders/fwd_fragment.glsl";

    /**
     * Fragment shader SPV location.
     */
    private static final String FRAGMENT_SHADER_FILE_SPV = FRAGMENT_SHADER_FILE_GLSL + ".spv";

    /**
     * Vertex shader location.
     */
    private static final String VERTEX_SHADER_FILE_GLSL = "resources/shaders/fwd_vertex.glsl";

    /**
     * Vertex shader SPV location.
     */
    private static final String VERTEX_SHADER_FILE_SPV = VERTEX_SHADER_FILE_GLSL + ".spv";

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
     * The shader program.
     */
    private final ShaderProgram shaderProgram;

    /**
     * The pipeline.
     */
    private final Pipeline pipeline;

    /**
     * Constructor.
     * @param swapChain The swap chain.
     * @param commandPool The command pool.
     * @param pipelineCache The pipeline cache.
     */
    public ForwardRenderActivity(SwapChain swapChain, CommandPool commandPool, PipelineCache pipelineCache) {
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

            EngineProperties engineProperties = EngineProperties.getInstance();
            if (engineProperties.isShaderRecompilation()) {
                ShaderCompiler.compileShaderIfChanged(VERTEX_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_vertex_shader);
                ShaderCompiler.compileShaderIfChanged(FRAGMENT_SHADER_FILE_GLSL, Shaderc.shaderc_glsl_fragment_shader);
            }
            shaderProgram = new ShaderProgram(logicalDevice, new ShaderModuleData[]{
                    new ShaderModuleData(VK_SHADER_STAGE_VERTEX_BIT, VERTEX_SHADER_FILE_SPV),
                    new ShaderModuleData(VK_SHADER_STAGE_FRAGMENT_BIT, FRAGMENT_SHADER_FILE_SPV)
            });
            PipelineCreationInformation pipelineCreationInformation = new PipelineCreationInformation(
                    renderPass.getRenderPass(), shaderProgram, 1, new VertexBufferStructure()
            );
            pipeline = new Pipeline(pipelineCache, pipelineCreationInformation);
            pipelineCreationInformation.cleanup();

            commandBuffers = new CommandBuffer[numberOfImages];
            fences = new Fence[numberOfImages];
            for (int i = 0; i < numberOfImages; i++) {
                commandBuffers[i] = new CommandBuffer(commandPool, true, false);
                fences[i] = new Fence(logicalDevice, true);
            }
        }
    }

    /**
     * Release resources.
     */
    public void cleanup() {
        pipeline.cleanup();
        shaderProgram.cleanup();
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
            SyncSemaphores syncSemaphores = swapChain.getSyncSemaphoresList()[frameNumber];
            queue.submit(stack.pointers(commandBuffer.getCommandBuffer()),
                    stack.longs(syncSemaphores.getImageAcquisitionSemaphore().getSemaphore()),
                    stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
                    stack.longs(syncSemaphores.getRenderCompleteSemaphore().getSemaphore()),
                    currentFence);
        }
    }

    /**
     * Retrieve and clear the command buffer for the current swap chain image.
     * Create the render pass information and start recording.
     * @param vulkanModelList The list of Vulkan models.
     */
    public void recordCommandBuffer(List<VulkanModel> vulkanModelList) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkExtent2D swapChainExtent = swapChain.getSwapChainExtent();
            int width = swapChainExtent.width();
            int height = swapChainExtent.height();
            int index = swapChain.getCurrentFrame();

            Fence fence = fences[index];
            CommandBuffer commandBuffer = commandBuffers[index];
            FrameBuffer frameBuffer = frameBuffers[index];

            fence.fenceWait();
            fence.reset();

            commandBuffer.reset();
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
            VkCommandBuffer commandBufferHandle = commandBuffer.getCommandBuffer();
            vkCmdBeginRenderPass(commandBufferHandle, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);

            vkCmdBindPipeline(commandBufferHandle, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getPipeline());

            VkViewport.Buffer viewport = VkViewport.calloc(1, stack)
                    .x(0)
                    .y(height)
                    .height(-height)
                    .width(width)
                    .minDepth(0.0f)
                    .maxDepth(0.1f);
            vkCmdSetViewport(commandBufferHandle, 0, viewport);

            VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack)
                    .extent(it -> it.width(width).height(height))
                    .offset(it -> it.x(0).y(0));
            vkCmdSetScissor(commandBufferHandle, 0, scissor);

            LongBuffer offsets = stack.mallocLong(1);
            offsets.put(0, 0L);
            LongBuffer vertexBuffer = stack.mallocLong(1);
            for (VulkanModel vulkanModel : vulkanModelList) {
                for (VulkanMesh mesh : vulkanModel.getVulkanMeshList()) {
                    vertexBuffer.put(0, mesh.getVertexBuffer().getBuffer());
                    vkCmdBindVertexBuffers(commandBufferHandle, 0, vertexBuffer, offsets);
                    vkCmdBindIndexBuffer(commandBufferHandle, mesh.getIndexBuffer().getBuffer(), 0, VK_INDEX_TYPE_UINT32);
                    vkCmdDrawIndexed(commandBufferHandle, mesh.getNumberOfIndices(), 1, 0, 0, 0);
                }
            }

            vkCmdEndRenderPass(commandBufferHandle);
            commandBuffer.endRecording();
        }
    }
}