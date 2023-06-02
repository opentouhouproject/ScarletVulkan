package org.scarlet.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import org.scarlet.ApplicationProperties;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Encapsulates the Vulkan instance.
 */
public class Instance {
    public static final int MESSAGE_SEVERITY_BITMASK = VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;

    public static final int MESSAGE_TYPE_BITMASK = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;

    private final VkInstance vkInstance;
    private VkDebugUtilsMessengerCreateInfoEXT debugUtils;
    private long vkDebugHandle;

    /**
     * Constructor.
     * @param applicationProperties - The application properties.
     * @param engineProperties - The engine properties.
     */
    public Instance(ApplicationProperties applicationProperties, EngineProperties engineProperties) {
        EngineLogger.getInstance().log(Level.INFO, "Creating Vulkan instance.");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create the application information.
            VkApplicationInfo applicationInformation = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8(applicationProperties.getApplicationName()))
                    .applicationVersion(VK_MAKE_API_VERSION(
                            applicationProperties.getVariant(),
                            applicationProperties.getMajorVersion(),
                            applicationProperties.getMinorVersion(),
                            applicationProperties.getPatchVersion()))
                    .pEngineName(stack.UTF8(engineProperties.getEngineName()))
                    .engineVersion(VK_MAKE_API_VERSION(
                            engineProperties.getVariant(),
                            engineProperties.getMajorVersion(),
                            engineProperties.getMinorVersion(),
                            engineProperties.getPatchVersion()))
                    .apiVersion(engineProperties.getVulkanAPIVersion());

            // Check for validation layer support.
            List<String> validationLayers = getSupportedValidationLayers();
            int numberOfValidationLayers = validationLayers.size();
            boolean supportsValidation = engineProperties.isValidationEnabled();
            if (engineProperties.isValidationEnabled() && numberOfValidationLayers == 0) {
                supportsValidation = false;
                EngineLogger.getInstance().log(Level.WARNING, "Requested validation, but no supported validation layers found. Falling back to no validation.");
            }
            EngineLogger.getInstance().log(Level.INFO, "Validation: [%b].", supportsValidation);

            // Convert the validation layer String list to null terminated ASCII Strings.
            PointerBuffer validationLayersBuffer = null;
            if (supportsValidation) {
                validationLayersBuffer = stack.mallocPointer(numberOfValidationLayers);
                for (int i = 0; i < numberOfValidationLayers; i++) {
                    EngineLogger.getInstance().log(Level.INFO, "Using validation layer [%s].", validationLayers.get(i));
                    validationLayersBuffer.put(i, stack.ASCII(validationLayers.get(i)));
                }
            }

            // Get the GLFW extensions.
            PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
            if (glfwExtensions == null) {
                throw new RuntimeException("Failed to find the GLFW platform surface extensions.");
            }

            // Add debug extension if needed.
            PointerBuffer requiredExtensions;
            if (supportsValidation) {
                ByteBuffer vkDebugUtilsExtension = stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
                requiredExtensions = stack.mallocPointer(glfwExtensions.remaining() + 1);
                requiredExtensions.put(glfwExtensions).put(vkDebugUtilsExtension);
            }
            else {
                requiredExtensions = stack.mallocPointer(glfwExtensions.remaining());
                requiredExtensions.put(glfwExtensions);
            }
            requiredExtensions.flip();

            // Create a callback if debug is enabled.
            long extension = MemoryUtil.NULL;
            if (supportsValidation) {
                debugUtils = createDebugCallBack();
                extension = debugUtils.address();
            }

            // Create the instance info.
            VkInstanceCreateInfo instanceInformation = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(extension)
                    .pApplicationInfo(applicationInformation)
                    .ppEnabledLayerNames(validationLayersBuffer)
                    .ppEnabledExtensionNames(requiredExtensions);

            // Create the Vulkan instance.
            PointerBuffer pInstance = stack.mallocPointer(1);
            vkCheck(vkCreateInstance(instanceInformation, null, pInstance), "Error creating Vulkan instance.");
            vkInstance = new VkInstance(pInstance.get(0), instanceInformation);

            // Instantiate the debug extension.
            vkDebugHandle = VK_NULL_HANDLE;
            if (supportsValidation) {
                LongBuffer longBuff = stack.mallocLong(1);
                vkCheck(vkCreateDebugUtilsMessengerEXT(vkInstance, debugUtils, null, longBuff), "Error creating debug utils.");
                vkDebugHandle = longBuff.get(0);
            }
        }
    }

    /**
     * Cleanup the Vulkan instance.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying Vulkan instance.");
        if (vkDebugHandle != VK_NULL_HANDLE) {
            vkDestroyDebugUtilsMessengerEXT(vkInstance, vkDebugHandle, null);
        }
        if (debugUtils != null) {
            debugUtils.pfnUserCallback().free();
            debugUtils.free();
        }
        vkDestroyInstance(vkInstance, null);
    }

    /**
     * Getter for the Vulkan instance.
     * @return
     */
    public VkInstance getVulkanInstance() {
        return vkInstance;
    }

    /**
     * Create the callback for debugging.
     * @return VkDebugUtilsMessengerCreateInfoEXT - Callback creation info.
     */
    private static VkDebugUtilsMessengerCreateInfoEXT createDebugCallBack() {
        VkDebugUtilsMessengerCreateInfoEXT result = VkDebugUtilsMessengerCreateInfoEXT
                .calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
                .messageSeverity(MESSAGE_SEVERITY_BITMASK)
                .messageType(MESSAGE_TYPE_BITMASK)
                .pfnUserCallback(((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                    VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                    if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
                        EngineLogger.getInstance().log(Level.INFO, "VkDebugUtilsCallback: [%s].", callbackData.pMessageString());
                    }
                    else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
                        EngineLogger.getInstance().log(Level.WARNING, "VkDebugUtilsCallback: [%s].", callbackData.pMessageString());
                    }
                    else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
                        EngineLogger.getInstance().log(Level.SEVERE, "VkDebugUtilsCallback: [%s].", callbackData.pMessageString());
                    }
                    else {
                        EngineLogger.getInstance().log(Level.FINE, "VkDebugUtilsCallback: [%s].", callbackData.pMessageString());
                    }
                    return VK_FALSE;
                }));
        return result;
    }

    /**
     * Get a list of supported validation layers.
     * @return List<String> - The list of supported validation layers.
     */
    private List<String> getSupportedValidationLayers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Get the number of supported layers.
            IntBuffer numberOfLayersBuffer = stack.callocInt(1);
            vkEnumerateInstanceLayerProperties(numberOfLayersBuffer, null);
            int numberOfLayers = numberOfLayersBuffer.get(0);
            EngineLogger.getInstance().log(Level.INFO, "Vulkan instance supports [%d] layers.", numberOfLayers);

            // Get the supported layers.
            VkLayerProperties.Buffer layerPropertiesBuffer = VkLayerProperties.calloc(numberOfLayers, stack);
            vkEnumerateInstanceLayerProperties(numberOfLayersBuffer, layerPropertiesBuffer);
            List<String> supportedLayers = new ArrayList<>();
            for (int i = 0; i < numberOfLayers; i++) {
                VkLayerProperties layerProperties = layerPropertiesBuffer.get(i);
                String layerName = layerProperties.layerNameString();
                supportedLayers.add(layerName);
                EngineLogger.getInstance().log(Level.INFO, "Supported layer [%s].", layerName);
            }

            // Get the validation layers.
            List<String> validationLayers = new ArrayList<>();

            // Main validation layer.
            if (supportedLayers.contains("VK_LAYER_KHRONOS_validation")) {
                validationLayers.add("VK_LAYER_KHRONOS_validation");
                return validationLayers;
            }
            // First fallback validation layer.
            else if (supportedLayers.contains("VK_LAYER_LUNARG_standard_validation")) {
                validationLayers.add("VK_LAYER_LUNARG_standard_validation");
                return validationLayers;
            }
            // Second fallback validation layer set.
            else {
                List<String> requestedLayers = new ArrayList<>();
                requestedLayers.add("VK_LAYER_GOOGLE_threading");
                requestedLayers.add("VK_LAYER_LUNARG_parameter_validation");
                requestedLayers.add("VK_LAYER_LUNARG_object_tracker");
                requestedLayers.add("VK_LAYER_LUNARG_core_validation");
                requestedLayers.add("VK_LAYER_GOOGLE_unique_objects");
                validationLayers.addAll(requestedLayers.stream().filter(supportedLayers::contains).toList());
                return validationLayers;
            }
        }
    }
}