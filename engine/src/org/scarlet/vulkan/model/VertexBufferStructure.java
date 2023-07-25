package org.scarlet.vulkan.model;

import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.scarlet.vulkan.Constants;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Defines how to extract data from the underlying buffer.
 */
public class VertexBufferStructure extends VertexInputStateInformation {
    /**
     * The number of attributes.
     */
    private static final int NUMBER_OF_ATTRIBUTES = 1;

    /**
     * The number of position components.
     */
    private static final int POSITION_COMPONENTS = 3;

    /**
     * The vertex input attribute description buffer.
     */
    private final VkVertexInputAttributeDescription.Buffer vertexInputAttributes;

    /**
     * The vertex input binding description buffer.
     */
    private final VkVertexInputBindingDescription.Buffer vertexInputBindings;

    /**
     * Constructor.
     */
    public VertexBufferStructure() {
        vertexInputAttributes = VkVertexInputAttributeDescription.calloc(NUMBER_OF_ATTRIBUTES);
        vertexInputBindings = VkVertexInputBindingDescription.calloc(1);
        vertexInfo = VkPipelineVertexInputStateCreateInfo.calloc();

        int i = 0;
        // Position.
        vertexInputAttributes.get(i)
                .binding(0)
                .location(i)
                .format(VK_FORMAT_R32G32B32_SFLOAT)
                .offset(0);
        vertexInputBindings.get(0)
                .binding(0)
                .stride(POSITION_COMPONENTS * Constants.FLOAT_LENGTH)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
        vertexInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pVertexBindingDescriptions(vertexInputBindings)
                .pVertexAttributeDescriptions(vertexInputAttributes);
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        super.cleanup();
        vertexInputBindings.free();
        vertexInputAttributes.free();
    }
}