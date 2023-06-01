package org.scarlet.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkLayerProperties;
import org.scarlet.ApplicationProperties;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Encapsulates the Vulkan instance.
 */
public class Instance {
    private boolean supportsValidation = true;

    /**
     * Constructor.
     * @param applicationProperties - The application properties.
     * @param engineProperties - The engine properties.
     * @param validate - A Boolean indicating if validation layers are enabled.
     */
    public Instance(ApplicationProperties applicationProperties, EngineProperties engineProperties, boolean validate) {
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
            if (validate && numberOfValidationLayers == 0) {
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
        }
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