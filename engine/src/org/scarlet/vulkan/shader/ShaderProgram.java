package org.scarlet.vulkan.shader;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.LogicalDevice;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Groups a set of shader modules under a single class.
 */
public class ShaderProgram {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

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
            this.logicalDevice = device;
            int numberOfModules = data != null ? data.length : 0;
            shaderModules = new ShaderModule[numberOfModules];
            for (int i = 0; i < numberOfModules; i++) {
                byte[] moduleContents = Files.readAllBytes(new File(data[i].getShaderSPIRVFile()).toPath());
                long moduleHandle = createShaderModule(moduleContents);
                shaderModules[i] = new ShaderModule(data[i].getShaderStage(), moduleHandle);
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
            vkDestroyShaderModule(logicalDevice.getDevice(), module.getHandle(), null);
        }
    }

    /**
     * Get the shader modules.
     * @return ShaderModule[] - The shader modules.
     */
    public ShaderModule[] getShaderModules() {
        return shaderModules;
    }

    /**
     * Create a shader module.
     * @param code The shader code.
     * @return long - The handle to the shader module.
     */
    private long createShaderModule(byte[] code) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer pCode = stack.malloc(code.length).put(0, code);
            VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(pCode);
            LongBuffer lp = stack.mallocLong(1);
            vkCheck(vkCreateShaderModule(logicalDevice.getDevice(), moduleCreateInfo, null, lp),
                    "Failed to create shader module.");
            return lp.get(0);
        }
    }
}