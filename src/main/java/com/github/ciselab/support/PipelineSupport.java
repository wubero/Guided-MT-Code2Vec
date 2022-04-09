package com.github.ciselab.program;

import com.github.ciselab.lampion.cli.program.App;
import com.github.ciselab.lampion.core.program.Engine;
import com.github.ciselab.lampion.core.program.EngineResult;
import com.github.ciselab.lampion.core.transformations.TransformerRegistry;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.metric.MetricCategory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class PipelineSupport {

    public static final String path_bash = "C:/Program Files/Git/bin/bash.exe";
    public static final String resultFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/log.txt";
    public static final String configFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/src/main/resources/config.properties";
    public static final String dataDir = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/data/";

    private static long seed = 200;
    private static boolean removeAllComments = false;
    private static Engine.TransformationScope transformationScope = Engine.TransformationScope.global;
    private static long transformations = 100;
    private static Properties prop = new Properties();

    public static long getSeed() {
        return seed;
    }

    public static void initialize() {
        try (InputStream input = new FileInputStream(configFile)) {

            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(prop.get("seed") != null)
            seed = Long.parseLong((String) prop.get("seed"));
        if(prop.get("removeAllComments")!=null){
            removeAllComments = Boolean.parseBoolean((String) prop.get("removeAllComments"));
        }
        if(prop.get("transformationscope") != null){
            transformationScope = Engine.TransformationScope.valueOf(prop.getProperty("transformationscope"));
        }
        if(prop.get("transformations") != null) {
            transformations = Long.parseLong((String)prop.get("transformations"));
        }
    }

    /**
     * This method creates an engine and runs the transformations on the input directory.
     * This is done by first creating all transtormers in a TransformerRegistry and then creating a new Engine.
     * With this engine we can our CtModel that is created with a spoon launcher.
     * @param transformers the list of transformers.
     * @param input the input directory.
     * @return the directory which the transformation .java files are in.
     */
    public static String runTransformations(List<BaseTransformer> transformers, String input) {
        TransformerRegistry registry = new TransformerRegistry("fromGA");
        for(BaseTransformer i: transformers) {
            registry.registerTransformer(i);
        }
        String[] temp = input.split("_");
        String output = dataDir + temp[0] + (Integer.parseInt(temp[1])+1);
        String inputData = dataDir + input;
        Engine engine = new Engine(inputData, output, registry);
        engine.setNumberOfTransformationsPerScope(transformations, transformationScope);
        engine.setRandomSeed(seed);
        engine.setRemoveAllComments(removeAllComments);

        Launcher launcher = new spoon.Launcher();
        launcher.addInputResource(engine.getCodeDirectory());
        // The CodeRoot is the highest level of available information regarding the AST
        CtModel codeRoot = launcher.buildModel();
        // With the imports set to true, on second application the import will disappear, making Lambdas uncompilable.
        launcher.getFactory().getEnvironment().setAutoImports(false);
        //Further steps are in the method below.
        EngineResult result = engine.run(codeRoot);
        App.WriteAST(result, launcher);
        System.out.println("Transformer done");

        return output;
    }

    /**
     * Run 
     * @param dataset
     */
    public static void runCode2vec(String dataset) {
        System.out.println("Preprocessing file");
        String dir = dataDir + dataset;
        String preprocess = "source preprocess.sh " + dir + " " + dataset;
        runCode2VecCommand(preprocess);
        System.out.println("Eval model with preprocessed data");
        String eval = "python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test data/java-testPipeline/java-testPipeline.test.c2v --logs-path eval_log.txt";
        runCode2VecCommand(eval);
        System.out.println("Completed evaluation, results are in result.txt");
        // The evaluation writes to the result.txt file
    }

    /**
     * Get the weights for each metric from the config.properties file.
     * @return a list with all the weights.
     */
    public static List<Double> getWeights() {
        List<Double> weights = new ArrayList<>();
        for(MetricCategory i: MetricCategory.values()) {
            weights.add(Double.parseDouble(prop.getProperty(i.name())));
        }
        return weights;
    }

    /**
     * This method runs a command from the code2vec directory.
     * @param comm the command to be run.
     */
    private static void runCode2VecCommand(String comm) {
        // Path to your repository
        String path_repository = "cd code2vec/";
        // Git command you want to run

        String command = path_repository + " && " + comm + "&& exit";

        runBashCommand(command);
    }

    /**
     * This method runs a given command in git bash and prints the results.
     * @param command the command to be run in git bash.
     */
    private static void runBashCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(path_bash, "-c", command);

            System.out.println("Started process");
            Process process = processBuilder.start();

            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(" --- Command run successfully");
            } else {
                System.out.println(" --- Command run unsuccessfully");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(" --- Interruption in RunCommand: " + e);
            // Restore interrupted state
            Thread.currentThread().interrupt();
        }
    }
}
