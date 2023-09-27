package org.scarlet.vulkan.shader;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Stores a handle to a shader module.
 */
public class ShaderModule {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

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
     * @param logicalDevice The logical device.
     * @param shaderStage The shader stage.
     * @param code The shader code.
     */
    public ShaderModule(LogicalDevice logicalDevice, int shaderStage, byte[] code) {
        this.logicalDevice = logicalDevice;
        this.shaderStage = shaderStage;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer pCode = stack.malloc(code.length).put(0, code);
            VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(pCode);
            LongBuffer lp = stack.mallocLong(1);
            vkCheck(vkCreateShaderModule(logicalDevice.getDevice(), moduleCreateInfo, null, lp),
                    "Failed to create shader module.");
            handle = lp.get(0);
        }
    }

    /**
     * Destroy the shader module.
     */
    public void cleanup() {
        vkDestroyShaderModule(logicalDevice.getDevice(), handle, null);
    }

    /**
     * Get the shader stage.
     * @return int - The shader stage.
     */
    public int getShaderStage() {
        return shaderStage;
    }

    /**
     * Get the shader handle.
     * @return long - The shader handle.
     */
    public long getHandle() {
        return handle;
    }
}