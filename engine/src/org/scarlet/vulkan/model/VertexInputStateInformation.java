package org.scarlet.vulkan.model;

import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;

/**
 * Vertex input state information class.
 * Specific vertex format implementations may differ.
 */
public abstract class VertexInputStateInformation {
    /**
     * Pipeline vertex input state information.
     */
    protected VkPipelineVertexInputStateCreateInfo vertexInfo;

    /**
     * Release resources.
     */
    public void cleanup() {
        vertexInfo.free();
    }

    /**
     * Getter for the pipeline vertex input state information.
     * @return VkPipelineVertexInputStateCreateInfo - The vertexInfo field.
     */
    public VkPipelineVertexInputStateCreateInfo getVertexInfo() {
        return vertexInfo;
    }
}