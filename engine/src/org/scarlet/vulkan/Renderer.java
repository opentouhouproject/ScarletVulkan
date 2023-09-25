package org.scarlet.vulkan;

import org.scarlet.ApplicationProperties;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;
import org.scarlet.Window;
import org.scarlet.vulkan.buffer.CommandPool;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.device.PhysicalDevice;
import org.scarlet.vulkan.device.PhysicalDeviceFactory;
import org.scarlet.vulkan.model.ModelData;
import org.scarlet.vulkan.model.VulkanModel;
import org.scarlet.vulkan.pipeline.PipelineCache;
import org.scarlet.vulkan.queue.GraphicsQueue;
import org.scarlet.vulkan.queue.PresentQueue;
import org.scarlet.vulkan.render.ForwardRenderActivity;
import org.scarlet.vulkan.surface.Surface;
import org.scarlet.vulkan.surface.SwapChain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles rendering.
 */
public class Renderer {
    /**
     * The Vulkan instance.
     */
    private final Instance instance;

    /**
     * The physical device.
     */
    private final PhysicalDevice physicalDevice;

    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * The graphics queue.
     */
    private final GraphicsQueue graphicsQueue;

    /**
     * The surface to render results on.
     */
    private final Surface surface;

    /**
     * The swap chain for the surface.
     */
    private final SwapChain swapChain;

    /**
     * The command pool.
     */
    private final CommandPool commandPool;

    /**
     * The presentation queue.
     */
    private final PresentQueue presentQueue;

    /**
     * The pipeline cache.
     */
    private final PipelineCache pipelineCache;

    /**
     * The forward render activity.
     */
    private final ForwardRenderActivity forwardRenderActivity;

    /**
     * The list of Vulkan models.
     */
    private final List<VulkanModel> vulkanModels;

    /**
     * Constructor.
     * @param window The application window.
     * @param scene The scene to render.
     */
    public Renderer(ApplicationProperties applicationProperties, Window window, Scene scene) {
        instance = new Instance(applicationProperties, EngineProperties.getInstance());
        physicalDevice = PhysicalDeviceFactory.create(instance, EngineProperties.getInstance().getDeviceName());
        logicalDevice = new LogicalDevice(physicalDevice);
        surface = new Surface(physicalDevice, window.getWindowHandle());
        graphicsQueue = new GraphicsQueue(logicalDevice, 0);
        swapChain = new SwapChain(logicalDevice, surface, window, EngineProperties.getInstance());
        presentQueue = new PresentQueue(logicalDevice, surface, 0);
        commandPool = new CommandPool(logicalDevice, graphicsQueue.getQueueFamilyIndex());
        pipelineCache = new PipelineCache(logicalDevice);
        forwardRenderActivity = new ForwardRenderActivity(swapChain, commandPool, pipelineCache);
        vulkanModels = new ArrayList<>();
    }

    /**
     * Cleanup resources.
     */
    public void cleanup() {
        presentQueue.waitIdle();
        graphicsQueue.waitIdle();
        logicalDevice.waitIdle();

        vulkanModels.forEach(VulkanModel::cleanup);
        pipelineCache.cleanup();
        forwardRenderActivity.cleanup();
        commandPool.cleanup();
        swapChain.cleanup();
        surface.cleanup();
        logicalDevice.cleanup();
        physicalDevice.cleanup();
        instance.cleanup();
    }

    /**
     * Load the model data.
     * @param modelDataList - List of model data.
     */
    public void loadModels(List<ModelData> modelDataList) {
        EngineLogger.getInstance().log(Level.INFO, "Loading %d model(s).", modelDataList.size());
        vulkanModels.addAll(VulkanModel.transformModels(modelDataList, commandPool, graphicsQueue));
        EngineLogger.getInstance().log(Level.INFO, "Loaded %d model(s).", modelDataList.size());
    }

    /**
     * Renders the scene into the application window.
     * @param window The application window.
     * @param scene The scene.
     */
    public void render(Window window, Scene scene) {
        swapChain.acquireNextImage();
        forwardRenderActivity.recordCommandBuffer(vulkanModels);
        forwardRenderActivity.submit(presentQueue);
        swapChain.presentImage(graphicsQueue);
    }
}