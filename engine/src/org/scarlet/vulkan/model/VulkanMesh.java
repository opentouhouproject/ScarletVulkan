package org.scarlet.vulkan.model;

import org.scarlet.vulkan.buffer.VulkanBuffer;

/**
 * References to the GPU buffers storing the vertex and index information.
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

    /**
     * Getter for the vertex buffer.
     * @return VulkanBuffer - The vertex buffer.
     */
    public VulkanBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    /**
     * Getter for the index buffer.
     * @return VulkanBuffer - The index buffer.
     */
    public VulkanBuffer getIndexBuffer() {
        return indexBuffer;
    }

    /**
     * Getter for the number of indices.
     * @return int - The number of indices.
     */
    public int getNumberOfIndices() {
        return numberOfIndices;
    }
}