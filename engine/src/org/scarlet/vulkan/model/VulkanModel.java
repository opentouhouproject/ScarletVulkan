package org.scarlet.vulkan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information for 3D models.
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
     * @return List<VulkanMesh> - vulkanMeshList
     */
    public List<VulkanMesh> getVulkanMeshList() {
        return vulkanMeshList;
    }
}