package org.scarlet.vulkan.surface;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;
import org.scarlet.Window;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.device.PhysicalDevice;

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
     * Surface format.
     */
    private final SurfaceFormat surfaceFormat;

    /**
     * Handle to the swap chain.
     */
    private final long swapChain;

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
            int numberOfImages = calculateNumberOfImages(surfaceCapabilities, properties.getImageCount());
            surfaceFormat = new SurfaceFormat(physicalDevice, surface);
            VkExtent2D swapChainExtent = calculateSwapChainExtent(window, surfaceCapabilities);

            // Create the surface.
            VkSwapchainCreateInfoKHR vkSwapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface.getSurface())
                    .minImageCount(numberOfImages)
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
                vkSwapchainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_FIFO_KHR);
            }
            else {
                vkSwapchainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR);
            }
            LongBuffer pointerBuffer = stack.mallocLong(1);
            vkCheck(KHRSwapchain.vkCreateSwapchainKHR(
                    device.getDevice(), vkSwapchainCreateInfo, null, pointerBuffer),
                    "Failed to create swap chain.");
            swapChain = pointerBuffer.get(0);

            // Create the image views.
            imageViews = createImageViews(stack, device, swapChain, surfaceFormat.getImageFormat());
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying Vulkan swap chain.");
        Arrays.stream(imageViews).forEach(ImageView::cleanup);
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
}