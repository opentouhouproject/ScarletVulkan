package org.scarlet.vulkan.model;

import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.scarlet.vulkan.Constants;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.graphics.Constants.SVK_FORMAT_POSITION_TEXTURE_SFLOAT;

/**
 * Defines how to extract data from the underlying buffer.
 */
public class VertexBufferStructure extends VertexInputStateInformation {
    /**
     * The number of position components.
     */
    private static final int POSITION_COMPONENTS = 3;

    /**
     * The number of texture coordinate components.
     */
    private static final int TEXTURE_COORDINATE_COMPONENTS = 2;

    /**
     * The vertex input attribute description buffer.
     */
    private final VkVertexInputAttributeDescription.Buffer vertexInputAttributes;

    /**
     * The vertex input binding description buffer.
     */
    private final VkVertexInputBindingDescription.Buffer vertexInputBindings;

    /**
     * Default constructor.
     */
    public VertexBufferStructure() {
        this(new VertexBufferStructureData(SVK_FORMAT_POSITION_TEXTURE_SFLOAT));
    }

    /**
     * Constructor.
     * @param vertexBufferStructureData - The settings data.
     */
    public VertexBufferStructure(VertexBufferStructureData vertexBufferStructureData) {
        vertexInputAttributes = VkVertexInputAttributeDescription.calloc(vertexBufferStructureData.getNumberOfAttributes());
        vertexInputBindings = VkVertexInputBindingDescription.calloc(1);
        vertexInfo = VkPipelineVertexInputStateCreateInfo.calloc();

        int i = 0;
        int offset = 0;
        // Check if the position attribute is enabled.
        if (vertexBufferStructureData.isPositionEnabled()) {
            vertexInputAttributes.get(i)
                    .binding(0)
                    .location(i)
                    .format(VK_FORMAT_R32G32B32_SFLOAT)
                    .offset(offset);
            i++;
            offset += POSITION_COMPONENTS * Constants.FLOAT_LENGTH;
        }

        // Check if the texture coordinates attribute is enabled.
        if (vertexBufferStructureData.isTextureCoordinatesEnabled()) {
            vertexInputAttributes.get(i)
                    .binding(0)
                    .location(i)
                    .format(VK_FORMAT_R32G32_SFLOAT)
                    .offset(offset);
            offset += TEXTURE_COORDINATE_COMPONENTS * Constants.FLOAT_LENGTH;
        }

        vertexInputBindings.get(0)
                .binding(0)
                .stride(offset)
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