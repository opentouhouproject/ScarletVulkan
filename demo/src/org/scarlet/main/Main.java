package org.scarlet.main;

import org.scarlet.ApplicationLogic;
import org.scarlet.Engine;
import org.scarlet.Window;
import org.scarlet.vulkan.*;
import org.scarlet.vulkan.model.MeshData;
import org.scarlet.vulkan.model.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.scarlet.graphics.Constants.SVK_FORMAT_POSITION_SFLOAT;

/**
 * Main application class.
 */
public class Main implements ApplicationLogic {
    private static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger("Demo");
        logger.log(Level.INFO, "Starting demo application.");
        DemoProperties.getInstance().setVertexStructure(SVK_FORMAT_POSITION_SFLOAT);
        Engine engine = new Engine(DemoProperties.getInstance(), new Main());
        engine.start();
    }

    @Override
    public void initialize(Window window, Scene scene, Renderer renderer) {
        String modelID = "TriangleModel";
        MeshData meshData = new MeshData(new float[] {
                -0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f},
                new int[] {0, 1, 2});
        List<MeshData> meshDataList = new ArrayList<>();
        meshDataList.add(meshData);
        ModelData modelData = new ModelData(modelID, meshDataList);
        List<ModelData> modelDataList = new ArrayList<>();
        modelDataList.add(modelData);
        renderer.loadModels(modelDataList);
    }

    @Override
    public void cleanup() {
        // To be implemented.
    }

    @Override
    public void handleInput(Window window, Scene scene, long diffTimeMillis) {
        // To be implemented.
    }
}