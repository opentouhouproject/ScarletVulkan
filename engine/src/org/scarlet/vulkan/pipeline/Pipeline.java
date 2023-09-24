package org.scarlet.vulkan.pipeline;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.shader.ShaderModule;
import org.scarlet.vulkan.shader.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Representation of a pipeline.
 */
public class Pipeline {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * Handle to the pipeline.
     */
    private final long vkPipeline;

    /**
     * Handle to the pipeline layout.
     */
    private final long vkPipelineLayout;

    /**
     * Constructor.
     * @param pipelineCache - The pipeline cache.
     * @param pipelineCreationInformation - The pipeline information.
     */
    public Pipeline(PipelineCache pipelineCache, PipelineCreationInformation pipelineCreationInformation) {
        EngineLogger.getInstance().log(Level.INFO, "Creating pipeline.");
        logicalDevice = pipelineCache.getLogicalDevice();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer lp = stack.mallocLong(1);
            ByteBuffer main = stack.UTF8("main");
            ShaderModule[] shaderModules = pipelineCreationInformation.getShaderProgram().getShaderModules();
            int numberOfModules = shaderModules.length;
            VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(numberOfModules, stack);
            for (int i = 0; i < numberOfModules; i++) {
                shaderStages.get(i)
                        .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                        .stage(shaderModules[i].getShaderStage())
                        .module(shaderModules[i].getHandle())
                        .pName(main);
            }

            // Set up the input assembly stage.
            VkPipelineInputAssemblyStateCreateInfo vkPipelineInputAssemblyStateCreateInfo =
                    VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                            .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                            .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);

            // Set up the view ports and scissors.
            VkPipelineViewportStateCreateInfo vkPipelineViewportStateCreateInfo =
                    VkPipelineViewportStateCreateInfo.calloc(stack)
                            .viewportCount(1)
                            .scissorCount(1);

            // Set up the rasterization stage.
            VkPipelineRasterizationStateCreateInfo vkPipelineRasterizationStateCreateInfo =
                    VkPipelineRasterizationStateCreateInfo.calloc(stack)
                            .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                            .polygonMode(VK_POLYGON_MODE_FILL)
                            .cullMode(VK_CULL_MODE_NONE)
                            .frontFace(VK_FRONT_FACE_CLOCKWISE)
                            .lineWidth(1.0f);

            // Set up the multi-sampling.
            VkPipelineMultisampleStateCreateInfo vkPipelineMultisampleStateCreateInfo =
                    VkPipelineMultisampleStateCreateInfo.calloc(stack)
                            .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                            .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

            // Set up the color blending.
            VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachmentState =
                    VkPipelineColorBlendAttachmentState.calloc(pipelineCreationInformation.getNumberOfColorAttachments(), stack);
            for (int i = 0; i < pipelineCreationInformation.getNumberOfColorAttachments(); i++) {
                colorBlendAttachmentState.get(i)
                        .colorWriteMask(VK_COLOR_COMPONENT_R_BIT |
                                VK_COLOR_COMPONENT_G_BIT |
                                VK_COLOR_COMPONENT_B_BIT |
                                VK_COLOR_COMPONENT_A_BIT);
            }
            VkPipelineColorBlendStateCreateInfo colorBlendState =
                    VkPipelineColorBlendStateCreateInfo.calloc(stack)
                            .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                            .pAttachments(colorBlendAttachmentState);

            // Set up pipeline values that may change dynamically.
            VkPipelineDynamicStateCreateInfo vkPipelineDynamicStateCreateInfo =
                    VkPipelineDynamicStateCreateInfo.calloc(stack)
                            .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                            .pDynamicStates(stack.ints(
                                    VK_DYNAMIC_STATE_VIEWPORT,
                                    VK_DYNAMIC_STATE_SCISSOR));

            // Create the pipeline layout.
            VkPipelineLayoutCreateInfo pPipelineLayoutCreateInfo =
                    VkPipelineLayoutCreateInfo.calloc(stack)
                            .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);
            vkCheck(vkCreatePipelineLayout(logicalDevice.getDevice(), pPipelineLayoutCreateInfo, null, lp),
                    "Failed to create pipeline layout.");
            vkPipelineLayout = lp.get(0);

            // Create the pipeline.
            VkGraphicsPipelineCreateInfo.Buffer pipeline = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                    .pStages(shaderStages)
                    .pVertexInputState(pipelineCreationInformation.getVertexInputStateInformation().getVertexInfo())
                    .pInputAssemblyState(vkPipelineInputAssemblyStateCreateInfo)
                    .pViewportState(vkPipelineViewportStateCreateInfo)
                    .pRasterizationState(vkPipelineRasterizationStateCreateInfo)
                    .pMultisampleState(vkPipelineMultisampleStateCreateInfo)
                    .pColorBlendState(colorBlendState)
                    .pDynamicState(vkPipelineDynamicStateCreateInfo)
                    .layout(vkPipelineLayout)
                    .renderPass(pipelineCreationInformation.getVkRenderPass());
            vkCheck(vkCreateGraphicsPipelines(logicalDevice.getDevice(), pipelineCache.getVkPipelineCache(), pipeline, null, lp),
                    "Error creating graphics pipeline.");
            vkPipeline = lp.get(0);
        }
    }

    /**
     * Release resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying pipeline.");
        vkDestroyPipelineLayout(logicalDevice.getDevice(), vkPipelineLayout, null);
        vkDestroyPipeline(logicalDevice.getDevice(), vkPipeline, null);
    }

    /**
     * Get the pipeline handle.
     * @return long - The pipeline handle.
     */
    public long getPipeline() {
        return vkPipeline;
    }

    /**
     * Get the pipeline layout.
     * @return long - The pipeline layout handle.
     */
    public long getPipelineLayout() {
        return vkPipelineLayout;
    }
}