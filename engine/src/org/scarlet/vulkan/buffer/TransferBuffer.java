package org.scarlet.vulkan.buffer;

/**
 * Encapsulation of the source and destination buffers.
 */
public class TransferBuffer {
    /**
     * The source buffer.
     */
    private VulkanBuffer sourceBuffer;

    /**
     * The destination buffer.
     */
    private VulkanBuffer destinationBuffer;

    /**
     * Constructor.
     * @param source The source buffer.
     * @param destination The destination buffer.
     */
    public TransferBuffer(VulkanBuffer source, VulkanBuffer destination) {
        sourceBuffer = source;
        destinationBuffer = destination;
    }

    /**
     * Retrieve the source buffer.
     * @return VulkanBuffer - The source buffer.
     */
    public VulkanBuffer getSourceBuffer() {
        return sourceBuffer;
    }

    /**
     * Retrieve the destination buffer.
     * @return VulkanBuffer - The destination buffer.
     */
    public VulkanBuffer getDestinationBuffer() {
        return destinationBuffer;
    }
}