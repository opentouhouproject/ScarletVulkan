package org.scarlet.vulkan.shader;

import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.LogicalDevice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Groups a set of shader modules under a single class.
 */
public class ShaderProgram {
    /**
     * The shader module list.
     */
    private final ShaderModule[] shaderModules;

    /**
     * Constructor.
     * @param device The logical device.
     * @param data The shader module data list.
     */
    public ShaderProgram(LogicalDevice device, ShaderModuleData[] data) {
        try {
            int numberOfModules = data != null ? data.length : 0;
            shaderModules = new ShaderModule[numberOfModules];
            for (int i = 0; i < numberOfModules; i++) {
                byte[] moduleContents = Files.readAllBytes(new File(data[i].getShaderSPIRVFile()).toPath());
                shaderModules[i] = new ShaderModule(device, data[i].getShaderStage(), moduleContents);
            }
        } catch (IOException ex) {
            EngineLogger.getInstance().log(Level.SEVERE, "Error reading shader files.", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Cleanup the shader modules.
     */
    public void cleanup() {
        for (ShaderModule module : shaderModules) {
            module.cleanup();
        }
    }

    /**
     * Get the shader modules.
     * @return ShaderModule[] - The shader modules.
     */
    public ShaderModule[] getShaderModules() {
        return shaderModules;
    }
}