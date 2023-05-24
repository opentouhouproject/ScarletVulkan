package org.scarlet.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.scarlet.EngineLogger;

import java.nio.ByteBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1;

/**
 * Encapsulates the Vulkan instance.
 */
public class Instance {
    /**
     * Constructor.
     * @param validate
     */
    public Instance(boolean validate) {
        EngineLogger.getInstance().log(Level.INFO, "Creating Vulkan instance.");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create the application information.
            ByteBuffer applicationShortName = stack.UTF8("Scarlet Vulkan");
            VkApplicationInfo applicationInformation = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(applicationShortName)
                    .applicationVersion(VK_MAKE_VERSION(0, 0, 1))
                    .pEngineName(applicationShortName)
                    .engineVersion(VK_MAKE_VERSION(0, 0, 2))
                    .apiVersion(VK_API_VERSION_1_1);
        }
    }
}