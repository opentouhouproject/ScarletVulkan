package org.scarlet.vulkan.model;

/**
 * Contains the arrays storing the vertex and index information.
 */
public class MeshData {
    /**
     * The vertex data.
     */
    private float[] vertices;

    /**
     * The index data.
     */
    private int[] indices;

    /**
     * Constructor.
     * @param vertices The vertex data.
     * @param indices The index data.
     */
    public MeshData(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    /**
     * Retrieve the vertex list.
     * @return float[] - The list of vertices.
     */
    public float[] getVertices() {
        return vertices;
    }

    /**
     * Retrieve the index list.
     * @return int[] - The list of indices.
     */
    public int[] getIndices() {
        return indices;
    }
}