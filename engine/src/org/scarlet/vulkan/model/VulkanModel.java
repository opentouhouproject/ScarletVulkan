package org.scarlet.vulkan.model;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkBufferCopy;
import org.scarlet.graphics.model.MeshData;
import org.scarlet.graphics.model.ModelData;
import org.scarlet.vulkan.Constants;
import org.scarlet.vulkan.buffer.CommandBuffer;
import org.scarlet.vulkan.buffer.CommandPool;
import org.scarlet.vulkan.buffer.TransferBuffer;
import org.scarlet.vulkan.buffer.VulkanBuffer;
import org.scarlet.vulkan.concurrent.Fence;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.queue.Queue;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Contains information for 3D models.
 * Holds references to the information loaded in GPU buffers.
 */
public class VulkanModel {
    /**
     * The model ID.
     */
    private String modelID;

    /**
     * A list of meshes.
     */
    private List<VulkanMesh> vulkanMeshList;

    /**
     * Create Vulkan models from model data.
     * @param modelDataList The list of model data.
     * @param commandPool The command pool.
     * @param queue The queue.
     * @return List&lt;VulkanModel&gt; - A list of Vulkan models.
     */
    public static List<VulkanModel> transformModels(List<ModelData> modelDataList, CommandPool commandPool, Queue queue) {
        List<VulkanModel> vulkanModelList = new ArrayList<>();
        LogicalDevice device = commandPool.getDevice();
        CommandBuffer commandBuffer = new CommandBuffer(commandPool, true, true);
        List<VulkanBuffer> stagingBufferList = new ArrayList<>();

        commandBuffer.beginRecording();
        for (ModelData modelData : modelDataList) {
            VulkanModel vulkanModel = new VulkanModel(modelData.getModelID());
            vulkanModelList.add(vulkanModel);

            // Transform meshes loading their data into GPU buffers.
            for (MeshData meshData : modelData.getMeshDataList()) {
                TransferBuffer verticesBuffers = createVerticesBuffers(device, meshData);
                TransferBuffer indicesBuffers = createIndicesBuffers(device, meshData);
                stagingBufferList.add(verticesBuffers.getSourceBuffer());
                stagingBufferList.add(indicesBuffers.getSourceBuffer());
                recordTransferCommand(commandBuffer, verticesBuffers);
                recordTransferCommand(commandBuffer, indicesBuffers);
                VulkanMesh vulkanMesh = new VulkanMesh(
                        verticesBuffers.getDestinationBuffer(),
                        indicesBuffers.getDestinationBuffer(),
                        meshData.getIndices().length);
                vulkanModel.getVulkanMeshList().add(vulkanMesh);
            }
        }
        commandBuffer.endRecording();

        Fence fence = new Fence(device, true);
        fence.reset();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            queue.submit(stack.pointers(commandBuffer.getCommandBuffer()), null, null, null, fence);
        }
        fence.fenceWait();
        fence.cleanup();
        commandBuffer.cleanup();

        stagingBufferList.forEach(VulkanBuffer::cleanup);

        return vulkanModelList;
    }

    /**
     * Create source and destination buffers for transferring data.
     * @param device The logical device.
     * @param meshData The mesh data.
     * @return TransferBuffer - The source and destination buffers.
     */
    public static TransferBuffer createVerticesBuffers(LogicalDevice device, MeshData meshData) {
        boolean textureCoordinatesFlag = true;
        float[] positions = meshData.getPositions();
        float[] textureCoordinates = meshData.getTextureCoordinates();

        int numberOfElements = positions.length;
        // Check if texture coordinates are available.
        if (textureCoordinates == null || textureCoordinates.length == 0) {
            textureCoordinatesFlag = false;
        }
        else {
            numberOfElements += textureCoordinates.length;
        }
        int bufferSize = numberOfElements * Constants.FLOAT_LENGTH;

        VulkanBuffer sourceBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT |
                        VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        VulkanBuffer destinationBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT |
                        VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
                VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

        long mappedMemory = sourceBuffer.map();
        FloatBuffer data = MemoryUtil.memFloatBuffer(mappedMemory, (int) sourceBuffer.getRequestedSize());
        for (int row = 0; row < positions.length / 3; row++) {
            // Add the position data.
            int positionIndex = row * 3;
            data.put(positions[positionIndex]);
            data.put(positions[positionIndex + 1]);
            data.put(positions[positionIndex + 2]);

            // Add the texture coordinate data.
            if (textureCoordinatesFlag) {
                int textureCoordinateIndex = row * 2;
                data.put(textureCoordinates[textureCoordinateIndex]);
                data.put(textureCoordinates[textureCoordinateIndex + 1]);
            }
        }
        sourceBuffer.unMap();

        return new TransferBuffer(sourceBuffer, destinationBuffer);
    }

    /**
     * Record the transfer command.
     * @param commandBuffer The command buffer.
     * @param transferBuffer The transfer buffers.
     */
    public static void recordTransferCommand(CommandBuffer commandBuffer, TransferBuffer transferBuffer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1, stack)
                    .srcOffset(0)
                    .dstOffset(0)
                    .size(transferBuffer.getSourceBuffer().getRequestedSize());
            vkCmdCopyBuffer(commandBuffer.getCommandBuffer(),
                    transferBuffer.getSourceBuffer().getBuffer(),
                    transferBuffer.getDestinationBuffer().getBuffer(),
                    copyRegion);
        }
    }

    /**
     * Create source and destination buffers for transferring data.
     * @param device The logical device.
     * @param meshData The mesh data.
     * @return TransferBuffer - The source and destination buffers.
     */
    public static TransferBuffer createIndicesBuffers(LogicalDevice device, MeshData meshData) {
        int[] indices = meshData.getIndices();
        int numberOfIndices = indices.length;
        int bufferSize = numberOfIndices * Constants.INT_LENGTH;

        VulkanBuffer sourceBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT |
                        VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        VulkanBuffer destinationBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT |
                        VK_BUFFER_USAGE_INDEX_BUFFER_BIT,
                VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

        long mappedMemory = sourceBuffer.map();
        IntBuffer data = MemoryUtil.memIntBuffer(mappedMemory, (int) sourceBuffer.getRequestedSize());
        data.put(indices);
        sourceBuffer.unMap();

        return new TransferBuffer(sourceBuffer, destinationBuffer);
    }

    /**
     * Constructor.
     * @param modelID The model ID.
     */
    public VulkanModel(String modelID) {
        this.modelID = modelID;
        vulkanMeshList = new ArrayList<>();
    }

    /**
     * Release resources.
     */
    public void cleanup() {
        vulkanMeshList.forEach(VulkanMesh::cleanup);
    }

    /**
     * Retrieve the model ID.
     * @return String - modelID.
     */
    public String getModelID() {
        return modelID;
    }

    /**
     * Retrieve the mesh list.
     * @return List&lt;VulkanMesh&gt; - vulkanMeshList
     */
    public List<VulkanMesh> getVulkanMeshList() {
        return vulkanMeshList;
    }
}