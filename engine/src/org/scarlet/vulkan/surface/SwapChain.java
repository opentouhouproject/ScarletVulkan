package org.scarlet.vulkan.surface;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;
import org.scarlet.Window;
import org.scarlet.vulkan.concurrent.SyncSemaphores;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.device.PhysicalDevice;
import org.scarlet.vulkan.queue.Queue;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * An array of images used as the destination of render operations.
 * Images may be presented to a surface.
 */
public class SwapChain {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * Image view array.
     */
    private final ImageView[] imageViews;

    /**
     * Semaphores for each image.
     */
    private final SyncSemaphores[] syncSemaphoresList;

    /**
     * Surface format.
     */
    private final SurfaceFormat surfaceFormat;

    /**
     * Swap chain extent.
     */
    private final VkExtent2D swapChainExtent;

    /**
     * Handle to the swap chain.
     */
    private final long swapChain;

    /**
     * Current frame number.
     */
    private int currentFrame;

    /**
     * Constructor.
     * @param device The logical device.
     * @param surface The surface.
     * @param window The window.
     * @param properties The engine properties.
     */
    public SwapChain(LogicalDevice device, Surface surface, Window window, EngineProperties properties) {
        EngineLogger.getInstance().log(Level.INFO, "Creating Vulkan swap chain.");
        this.logicalDevice = device;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PhysicalDevice physicalDevice = device.getPhysicalDevice();

            // Get surface capabilities.
            VkSurfaceCapabilitiesKHR surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc(stack);
            vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(
                    device.getPhysicalDevice().getDevice(), surface.getSurface(), surfaceCapabilities),
                    "Failed to get surface capabilities.");
            int minNumberOfImages = calculateNumberOfImages(surfaceCapabilities, properties.getImageCount());
            surfaceFormat = new SurfaceFormat(physicalDevice, surface);
            swapChainExtent = calculateSwapChainExtent(window, surfaceCapabilities);

            // Create the surface.
            VkSwapchainCreateInfoKHR vkSwapChainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface.getSurface())
                    .minImageCount(minNumberOfImages)
                    .imageFormat(surfaceFormat.getImageFormat())
                    .imageColorSpace(surfaceFormat.getColorSpace())
                    .imageExtent(swapChainExtent)
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .preTransform(surfaceCapabilities.currentTransform())
                    .compositeAlpha(KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .clipped(true);
            if (properties.isVSyncEnabled()) {
                vkSwapChainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_FIFO_KHR);
            }
            else {
                vkSwapChainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR);
            }
            LongBuffer pointerBuffer = stack.mallocLong(1);
            vkCheck(KHRSwapchain.vkCreateSwapchainKHR(
                    device.getDevice(), vkSwapChainCreateInfo, null, pointerBuffer),
                    "Failed to create swap chain.");
            swapChain = pointerBuffer.get(0);

            // Create the image views.
            imageViews = createImageViews(stack, device, swapChain, surfaceFormat.getImageFormat());
            int numberOfImages = imageViews.length;
            syncSemaphoresList = new SyncSemaphores[numberOfImages];
            for (int i = 0; i < numberOfImages; i++) {
                syncSemaphoresList[i] = new SyncSemaphores(logicalDevice);
            }
            currentFrame = 0;
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying Vulkan swap chain.");
        Arrays.stream(imageViews).forEach(ImageView::cleanup);
        Arrays.stream(syncSemaphoresList).forEach(SyncSemaphores::cleanup);
        KHRSwapchain.vkDestroySwapchainKHR(logicalDevice.getDevice(), swapChain, null);
    }

    /**
     * Calculate the number of images.
     * @param surfaceCapabilities Surface capabilities.
     * @param requestedNumberOfImages User requested number of images.
     * @return int - Actual number of images.
     */
    private int calculateNumberOfImages(VkSurfaceCapabilitiesKHR surfaceCapabilities, int requestedNumberOfImages) {
        int maxNumberOfImages = surfaceCapabilities.maxImageCount();
        int minNumberOfImages = surfaceCapabilities.minImageCount();
        int result = minNumberOfImages;
        if (maxNumberOfImages != 0) {
            result = Math.min(requestedNumberOfImages, maxNumberOfImages);
        }
        result = Math.max(result, minNumberOfImages);
        EngineLogger.getInstance().log(Level.INFO,
                "Requested [%d], got [%d] images. Surface capabilities, max number of images: [%d], min number of images [%d].",
                requestedNumberOfImages, result, maxNumberOfImages, minNumberOfImages);
        return result;
    }

    /**
     * Calculate the 2D extent.
     * @param window - The window.
     * @param surfaceCapabilities - The surface capabilities.
     * @return VkExtent2D - The 2D extent.
     */
    private VkExtent2D calculateSwapChainExtent(Window window, VkSurfaceCapabilitiesKHR surfaceCapabilities) {
        VkExtent2D extent = VkExtent2D.calloc();
        if (surfaceCapabilities.currentExtent().width() == 0xFFFFFFFF) {
            // Surface size is undefined. Set to the window size if within bounds.
            int width = Math.min(window.getWidth(), surfaceCapabilities.maxImageExtent().width());
            width = Math.max(width, surfaceCapabilities.minImageExtent().width());

            int height = Math.min(window.getHeight(), surfaceCapabilities.maxImageExtent().height());
            height = Math.max(height, surfaceCapabilities.minImageExtent().height());

            extent.width(width);
            extent.height(height);
        }
        else {
            // Surface is already defined.
            extent.set(surfaceCapabilities.currentExtent());
        }
        return extent;
    }

    /**
     * Create the image view array.
     * @param stack - The memory stack.
     * @param device - The logical device.
     * @param swapChain - The swap chain.
     * @param format - The format.
     * @return ImageView[] - The array of image views.
     */
    private ImageView[] createImageViews(MemoryStack stack, LogicalDevice device, long swapChain, int format) {
        ImageView[] result;

        // Get the number of surface images.
        IntBuffer numberOfSurfaceImages = stack.mallocInt(1);
        vkCheck(KHRSwapchain.vkGetSwapchainImagesKHR(
                logicalDevice.getDevice(), swapChain, numberOfSurfaceImages, null),
                "Failed to get the number of surface images.");
        int numberOfImages = numberOfSurfaceImages.get(0);

        // Get the surface images.
        LongBuffer swapChainImages = stack.mallocLong(numberOfImages);
        vkCheck(KHRSwapchain.vkGetSwapchainImagesKHR(
                logicalDevice.getDevice(), swapChain, numberOfSurfaceImages, swapChainImages),
                "Failed to get surface images.");

        // Create the image view array.
        result = new ImageView[numberOfImages];
        ImageViewData imageViewData = new ImageViewData()
                .format(format)
                .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
        for (int i = 0; i < numberOfImages; i++) {
            result[i] = new ImageView(device, swapChainImages.get(i), imageViewData);
        }

        return result;
    }

    /**
     * Set the index of the next presentable image.
     * @return boolean - True if resize is required, false otherwise.
     */
    public boolean acquireNextImage() {
        boolean resize = false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuffer = stack.mallocInt(1);
            int error = KHRSwapchain.vkAcquireNextImageKHR(
                    logicalDevice.getDevice(), swapChain, ~0L,
                    syncSemaphoresList[currentFrame].getImageAcquisitionSemaphore().getSemaphore(),
                    MemoryUtil.NULL, intBuffer);
            if (error == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            }
            else if (error == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
                // Not optimal, but swap chain can still be used.
            }
            else if (error != VK_SUCCESS) {
                throw new RuntimeException("Failed to acquire image: " + error);
            }
            currentFrame = intBuffer.get(0);
        }
        return resize;
    }

    /**
     * Queues an image for presentation.
     * @param queue The Vulkan queue.
     * @return boolean - True if resize is required, false otherwise.
     */
    public boolean presentImage(Queue queue) {
        boolean resize = false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                    .pWaitSemaphores(stack.longs(syncSemaphoresList[currentFrame].getRenderCompleteSemaphore().getSemaphore()))
                    .swapchainCount(1)
                    .pSwapchains(stack.longs(swapChain))
                    .pImageIndices(stack.ints(currentFrame));
            int error = KHRSwapchain.vkQueuePresentKHR(queue.getQueue(), presentInfo);
            if (error == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            }
            else if (error == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
                // Not optimal, but swap chain can still be used.
            }
            else if (error != VK_SUCCESS) {
                throw new RuntimeException("Failed to present KHR: " + error);
            }
        }
        currentFrame = (currentFrame + 1) % imageViews.length;
        return resize;
    }

    /**
     * Get the logical device.
     * @return LogicalDevice - The logical device.
     */
    public LogicalDevice getLogicalDevice() {
        return logicalDevice;
    }

    /**
     * Get the image views.
     * @return ImageView[] - The image views.
     */
    public ImageView[] getImageViews() {
        return imageViews;
    }

    /**
     * Get the sync semaphores.
     * @return SyncSemaphores[] - The sync semaphores list.
     */
    public SyncSemaphores[] getSyncSemaphoresList() {
        return syncSemaphoresList;
    }

    /**
     * Get the surface format.
     * @return SurfaceFormat - The surface format.
     */
    public SurfaceFormat getSurfaceFormat() {
        return surfaceFormat;
    }

    /**
     * Get the swap chain extent.
     * @return VkExtent2D - The swap chain extent.
     */
    public VkExtent2D getSwapChainExtent() {
        return swapChainExtent;
    }

    /**
     * Get the current frame number.
     * @return int - The current frame number.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
}