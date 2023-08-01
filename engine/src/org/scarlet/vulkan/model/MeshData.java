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
}