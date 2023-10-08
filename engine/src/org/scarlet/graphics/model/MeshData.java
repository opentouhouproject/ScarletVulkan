package org.scarlet.graphics.model;

/**
 * Contains the arrays storing the vertex and index information.
 */
public class MeshData {
    /**
     * The vertex data.
     */
    private float[] positions;

    /**
     * The texture coordinate data.
     */
    private float[] textureCoordinates;

    /**
     * The index data.
     */
    private int[] indices;

    /**
     * Constructor.
     * @param positions - The position data.
     * @param indices - The index data.
     */
    public MeshData(float[] positions, int[] indices) {
        this.positions = positions;
        this.indices = indices;
    }

    /**
     * Constructor.
     * @param positions - The position data.
     * @param textureCoordinates - The texture coordinate data.
     * @param indices - The index data.
     */
    public MeshData(float[] positions, float[] textureCoordinates, int[] indices) {
        this.positions = positions;
        this.textureCoordinates = textureCoordinates;
        this.indices = indices;
    }

    /**
     * Retrieve the positions list.
     * @return float[] - The list of positions.
     */
    public float[] getPositions() {
        return positions;
    }

    /**
     * Get the texture coordinates list.
     * @return float[] - The list of texture of coordinates.
     */
    public float[] getTextureCoordinates() {
        return textureCoordinates;
    }

    /**
     * Retrieve the index list.
     * @return int[] - The list of indices.
     */
    public int[] getIndices() {
        return indices;
    }
}