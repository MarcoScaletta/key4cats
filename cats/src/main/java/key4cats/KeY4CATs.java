package key4cats;


import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;
import de.uka.ilkd.key.gui.MainWindow;
import de.uka.ilkd.key.gui.WindowUserInterfaceControl;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;
import org.apache.commons.cli.HelpFormatter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static de.uka.ilkd.key.core.Main.loadCommandLineFiles;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeY4CATs {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeY4CATs.class);

    public enum ProofGenMode{ALL, SINGLE,FULL};

    enum KeYMode {AUTO, GUI};
//    final static String javafile;
    static Option HELP_OPTION = Option.builder("h")
        .required(true)
        .desc("Shows this help menu")
        .longOpt("help")
        .build();

    static Option ALL_PROOFS = Option.builder("all")
            .required(true)
            .desc("Generate proof for all the contract")
            .build();

    static Option FULL_PROOF = Option.builder("f")
            .required(true)
            .desc("Generate proof for <CONTRACT_NAME> and for all contracts assumed in <CONTRACT_NAME>")
            .longOpt("full-proof")
            .hasArg().argName("CONTRACT_NAME")
            .build();


    static Option SHOW_STATS = Option.builder("stats")
            .required(false)
            .desc("Show statistics")
            .longOpt("statistics")
            .build();

    static Option CATS_FILE = Option.builder("cats")
            .required(true)
            .desc("Load contract(s) from <CATS_FILE>")
            .longOpt("cats-file")
            .hasArg().argName("CATS_FILE")
            .build();

    static Option SINGLE_PROOF = Option.builder("s")
            .required(true)
            .desc("Generate single proof <CONTRACT_NAME>.key")
            .longOpt("single")
            .hasArg().argName("CONTRACT_NAME")
            .build();

    static Option INTERACTIVE = Option.builder("i")
            .desc("Execute KeY in interactive mode via gui")
            .longOpt("interactive")
            .hasArg().argName("MODE")
            .build();

    static Option JAVA_CLASS = Option.builder("class")
            .required(true)
            .desc("Refer to code in <JAVA_CLASS>.java")
            .longOpt("java-class")
            .hasArg().argName("JAVA_FILE")
            .build();

    static final OptionGroup LOAD_PROOF_OPTION = new OptionGroup().addOption(FULL_PROOF).addOption(SINGLE_PROOF).addOption(ALL_PROOFS);



    static final Options options = new Options();
    static {
//        LOAD_PROOF_OPTION.setRequired(true);
        options.addOptionGroup(LOAD_PROOF_OPTION);
        options.addOption(CATS_FILE);
        options.addOption(JAVA_CLASS);
        options.addOption(FULL_PROOF);
        options.addOption(INTERACTIVE);
        options.addOption(SHOW_STATS);
    }


    static ProofGenMode proofGenMode;
    static String contractName;
    static String catsFilename;
    static String javaClassFilename;
    static String keyHome = System.getenv("KEY");
    static KeYMode executionMode = KeYMode.AUTO;
    static boolean stats = false;
    static String directory;


    public static void main(String [] args) throws IOException {
        checkOptions(args);
        checkOptions();
        File catsFile = new File(keyHome + catsFilename);
        String directory = catsFile.getParent();
        final InputStream targetStream = new DataInputStream(new FileInputStream(catsFile));
        String catsFileContent = new String(targetStream.readAllBytes(), StandardCharsets.UTF_8);
        ProofCATsBuilder p = new ProofCATsBuilder(catsFileContent, contractName, javaClassFilename,proofGenMode);

        Set<String> contractNames = p.getContractIds();

        if(proofGenMode == ProofGenMode.SINGLE || proofGenMode == ProofGenMode.FULL){
            if(!contractNames.iterator().hasNext())
                throw new RuntimeException( "No contract to be proven (check what command you run)");
            for (String cName : contractNames) {
                prove(p, directory, cName);
            }
        }else {
            for (String contractName : contractNames) {
                File keyFile = new File(String.format("%s/%s.key", directory, contractName));
                DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
                dataOutputStream.writeBytes(p.assembleProof(contractName).toKeY());
                dataOutputStream.flush();
            }
        }
    }

    private static void prove(ProofCATsBuilder p, String directory, String contractName) throws IOException{
        generateProof(p,directory,contractName);
        if(executionMode == KeYMode.GUI) {
            LOGGER.info("Starting Interactive Mode (continue in the newly opened window)");
            openFileWithGUI(directory, contractName);
        }
        else if(executionMode == KeYMode.AUTO) {
            LOGGER.info("Proving the contract automatically");
            openFileWithCLI(directory, contractName);
        }else {
            throw new RuntimeException("Execution Mode should be GUI or AUTO but found: "+ executionMode);
        }
    }

    private static void generateProof(ProofCATsBuilder p, String directory, String contractName) throws IOException{
        File keyFile = new File(String.format("%s/%s.key", directory, contractName));
        LOGGER.info(String.format("Generating proof for %s in file %s", contractName,keyFile));
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
        dataOutputStream.writeBytes(p.assembleProof(contractName).toKeY());
        dataOutputStream.flush();
    }

    private static void checkOptions(){
        if((proofGenMode == ProofGenMode.ALL || proofGenMode == ProofGenMode.FULL) && executionMode == KeYMode.GUI)
            throw  new RuntimeException("Only one proof at a time can be loaded interactively.");
    }

    private static void checkOptions(String[] args){
        CommandLine cl;
        CommandLineParser parser = new DefaultParser();
        try {
            cl = parser.parse(new Options().addOption(HELP_OPTION), args);
            Options allOptions = new Options();
            allOptions.addOptions(options);
            allOptions.addOption(HELP_OPTION);

            if(cl.hasOption(HELP_OPTION)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.setOptionComparator(null);
                formatter.printHelp( "cats --java-file <JAVA_FILE> --cats-file <CATS_FILE> (--single |--full | --all) <CONTRACT_NAME> [-i]", allOptions);
                System.exit(0);
            }
        }catch (ParseException e1){
            try {
                cl = parser.parse(options, args);
                if(cl.hasOption(LOAD_PROOF_OPTION)){
                    if(cl.hasOption(ALL_PROOFS))
                        proofGenMode = ProofGenMode.ALL;
                    if(cl.hasOption(SINGLE_PROOF)) {
                        proofGenMode = ProofGenMode.SINGLE;
                        contractName = cl.getOptionValue(SINGLE_PROOF);
                    }
                    if(cl.hasOption(FULL_PROOF)) {
                        proofGenMode = ProofGenMode.FULL;
                        contractName = cl.getOptionValue(FULL_PROOF);
                    }
                }
                if (cl.hasOption(INTERACTIVE))
                    executionMode = KeYMode.GUI;
                else
                    executionMode = KeYMode.AUTO;
                if(cl.hasOption(CATS_FILE)){
                    catsFilename = cl.getOptionValue(CATS_FILE);
                }

                if(cl.hasOption(JAVA_CLASS)){
                    javaClassFilename = cl.getOptionValue(JAVA_CLASS);
                }
                if(cl.hasOption(SHOW_STATS)){
                    stats = true;
                }
            }catch (ParseException e){
                throw new RuntimeException("Cannot parse arguments\n" + e.getMessage());
            }
        }
    }




    private static void openFileWithGUI(String directory, String contractName) {
        File keyFile = new File(String.format("%s/%s.key", directory, contractName));
        WindowUserInterfaceControl windowUserInterfaceControl = MainWindow.getInstance().getUserInterface();
        loadCommandLineFiles(windowUserInterfaceControl, List.of(keyFile));
    }

    private static void openFileWithCLI(String directory, String contractName) {
        try {
            File keyFile = new File(String.format("%s/%s.key", directory, contractName));
            KeYEnvironment<DefaultUserInterfaceControl> env = KeYEnvironment.load(keyFile);
            env.getProofControl().startAndWaitForAutoMode(env.getLoadedProof());
            boolean proved = env.getLoadedProof().closed();
            LOGGER.info("Proof: " + (proved ? "CLOSED (proven)" : "OPEN (cannot prove)"));
            if(stats)
                LOGGER.info(env.getLoadedProof().getStatistics().toString());
            else
                LOGGER.info("Nodes: " +  env.getLoadedProof().countNodes());
        }catch (ProblemLoaderException e){
            throw new RuntimeException(e);
        }
    }

}
