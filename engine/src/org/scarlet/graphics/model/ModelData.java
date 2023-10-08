package org.scarlet.graphics.model;

import java.util.List;

/**
 * Contains information for 3D models.
 */
public class ModelData {
    /**
     * The model ID.
     */
    private String modelID;

    /**
     * A list of meshes.
     */
    private List<MeshData> meshDataList;

    /**
     * Constructor.
     * @param modelID The model ID.
     * @param meshDataList The mesh list.
     */
    public ModelData(String modelID, List<MeshData> meshDataList) {
        this.modelID = modelID;
        this.meshDataList = meshDataList;
    }

    /**
     * Retrieve the model ID.
     * @return String - modelID
     */
    public String getModelID() {
        return modelID;
    }

    /**
     * Retrieve the mesh data list.
     * @return List&lt;MeshData&gt; - meshDataList
     */
    public List<MeshData> getMeshDataList() {
        return meshDataList;
    }
}