package org.scarlet.vulkan.shader;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.shaderc.Shaderc;
import org.scarlet.EngineLogger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import static java.util.logging.Level.INFO;

/**
 * Handles compilation of the shaders.
 */
public class ShaderCompiler {
    /**
     * Compile the shader if a change is detected.
     * @param glslShaderFile The shader file.
     * @param shaderType The shader type.
     */
    public static void compileShaderIfChanged(String glslShaderFile, int shaderType) {
        byte[] compiledShader;
        try {
            File glslFile = new File(glslShaderFile);
            File spirvFile = new File(glslFile + ".spv");
            if (!spirvFile.exists() || glslFile.lastModified() > spirvFile.lastModified()) {
                EngineLogger.getInstance().log(INFO, "Compiling [{}] to [{}].", glslFile.getPath(), spirvFile.getPath());
                String shaderCode = new String(Files.readAllBytes(glslFile.toPath()));
                compiledShader = compileShader(shaderCode, shaderType);
                Files.write(spirvFile.toPath(), compiledShader);
            }
            else {
                EngineLogger.getInstance().log(INFO, "Shader [{}] already compiled. Loading compiled version: [{}].", glslFile.getPath(), spirvFile.getPath());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Compile the shader using Shaderc.
     * @param shaderCode The shader code.
     * @param shaderType The shader type.
     * @return byte[] - The compiled shader.
     */
    public static byte[] compileShader(String shaderCode, int shaderType) {
        long compiler = 0;
        long options = 0;
        byte[] compiledShader;

        try {
            compiler = Shaderc.shaderc_compiler_initialize();
            options = Shaderc.shaderc_compile_options_initialize();

            long result = Shaderc.shaderc_compile_into_spv(
                    compiler,
                    shaderCode,
                    shaderType,
                    "shader.glsl",
                    "main",
                    options);

            if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                throw new RuntimeException("Shader compilation failed: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
            compiledShader = new byte[buffer.remaining()];
            buffer.get(compiledShader);
        } finally {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
        }

        return compiledShader;
    }
}