package org.scarlet.vulkan.pipeline;

import org.scarlet.vulkan.model.VertexInputStateInformation;
import org.scarlet.vulkan.shader.ShaderProgram;

/**
 * Stores the configuration for pipeline creation.
 */
public class PipelineCreationInformation {
    /**
     * The handle to the render pass.
     */
    private final long vkRenderPass;

    /**
     * The shader program.
     */
    private final ShaderProgram shaderProgram;

    /**
     * The number of color attachments.
     */
    private final int numberOfColorAttachments;

    /**
     * Vertex input state information.
     */
    private final VertexInputStateInformation vertexInputStateInformation;

    /**
     * Constructor.
     * @param vkRenderPass The render pass.
     * @param shaderProgram The shader program.
     * @param numberOfColorAttachments The number of color attachments.
     * @param vertexInputStateInformation The vertex input state information.
     */
    public PipelineCreationInformation(long vkRenderPass, ShaderProgram shaderProgram, int numberOfColorAttachments, VertexInputStateInformation vertexInputStateInformation) {
        this.vkRenderPass = vkRenderPass;
        this.shaderProgram = shaderProgram;
        this.numberOfColorAttachments = numberOfColorAttachments;
        this.vertexInputStateInformation = vertexInputStateInformation;
    }

    /**
     * Cleanup resources.
     */
    public void cleanup() {
        vertexInputStateInformation.cleanup();
    }

    /**
     * Get the render pass handle.
     * @return long - The render pass handle.
     */
    public long getVkRenderPass() {
        return vkRenderPass;
    }

    /**
     * Get the shader program.
     * @return ShaderProgram - The shader program.
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    /**
     * Get the number of color attachments.
     * @return int - The number of color attachments.
     */
    public int getNumberOfColorAttachments() {
        return numberOfColorAttachments;
    }

    /**
     * Get the vertex input state information.
     * @return VertexInputStateInformation - The vertex inout state information.
     */
    public VertexInputStateInformation getVertexInputStateInformation() {
        return vertexInputStateInformation;
    }
}