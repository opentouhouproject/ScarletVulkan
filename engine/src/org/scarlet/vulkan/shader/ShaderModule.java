package org.scarlet.vulkan.shader;

/**
 * Stores a handle to a shader module.
 */
public class ShaderModule {
    /**
     * The shader stage.
     */
    private int shaderStage;

    /**
     * The handle to the shader module.
     */
    private long handle;

    /**
     * Constructor.
     * @param shaderStage The shader stage.
     * @param handle The shader module handle.
     */
    public ShaderModule(int shaderStage, long handle) {
        this.shaderStage = shaderStage;
        this.handle = handle;
    }

    /**
     * Retrieve the shader handle.
     * @return long - The shader handle.
     */
    public long getHandle() {
        return handle;
    }
}