package org.scarlet.vulkan.shader;

/**
 * Data about the shader module.
 */
public class ShaderModuleData {
    /**
     * The shader stage.
     */
    private int shaderStage;

    /**
     * The path of the SPIR-V file.
     */
    private String shaderSPIRVFile;

    /**
     * Constructor.
     * @param shaderStage The shader stage.
     * @param shaderSPIRVFile The path of the SPIR-V file.
     */
    public ShaderModuleData(int shaderStage, String shaderSPIRVFile) {
        this.shaderStage = shaderStage;
        this.shaderSPIRVFile = shaderSPIRVFile;
    }

    /**
     * Retrieve the shader stage.
     * @return int - The shader stage.
     */
    public int getShaderStage() {
        return shaderStage;
    }

    /**
     * Retrieve the SPIR-V file path.
     * @return String - The SPIR-V file path.
     */
    public String getShaderSPIRVFile() {
        return shaderSPIRVFile;
    }
}