package org.scarlet.vulkan.model;

import org.scarlet.vulkan.buffer.VulkanBuffer;

/**
 * Contains the buffers storing the vertex and index information.
 */
public class VulkanMesh {
    /**
     * Buffer storing data for the vertices.
     */
    private VulkanBuffer vertexBuffer;

    /**
     * Buffer storing data for the indices.
     */
    private VulkanBuffer indexBuffer;

    /**
     * The total number of indices.
     */
    private int numberOfIndices;

    /**
     * Constructor.
     * @param vertexBuffer The vertex buffer.
     * @param indexBuffer The index buffer.
     * @param numberOfIndices The number of indices.
     */
    public VulkanMesh(VulkanBuffer vertexBuffer, VulkanBuffer indexBuffer, int numberOfIndices) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.numberOfIndices = numberOfIndices;
    }

    /**
     * Release resources.
     */
    public void cleanup() {
        vertexBuffer.cleanup();
        indexBuffer.cleanup();
    }
}