package key4cats;


import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;
import de.uka.ilkd.key.gui.MainWindow;
import de.uka.ilkd.key.gui.WindowUserInterfaceControl;
import de.uka.ilkd.key.proof.Proof;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;
import de.uka.ilkd.key.proof.io.ProofSaver;
import org.apache.commons.cli.HelpFormatter;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static de.uka.ilkd.key.core.Main.loadCommandLineFiles;

import org.apache.commons.cli.*;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeY4CATs {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(KeY4CATs.class);

    public enum ProofGenMode{SINGLE,FULL,ALL}

    public enum LoadingMode{CAT, PO}


    public static final PrintStream unmutedOut = System.out;
    public static final PrintStream mutedOut = new PrintStream(OutputStream.nullOutputStream());

    enum KeYMode {AUTO, GUI}

    static Option HELP_OPTION = Option.builder("h")
        .desc("Shows this help menu")
        .longOpt("help")
        .build();

    static Option FULL_PROOF = Option.builder("f")
            .desc("Targets <CAT_ID> and also all contracts assumed by <CAT_ID> from file <CATSL_FILE>")
            .longOpt("full")
            .hasArg().argName("CAT_ID")
            .build();

    static Option SHOW_STATS = Option.builder("stats")
            .required(false)
            .desc("Show statistics of verification")
            .longOpt("statistics")
            .build();

    static Option SINGLE_CAT = Option.builder("s")
            .desc("Targets single contract <CAT_ID> from file <CATSL_FILE>")
            .longOpt("single")
            .numberOfArgs(2)
            .hasArg().argName("CAT_ID")
            .build();

    static Option BENCHMARK = Option.builder("b")
            .desc("Warming up before verify single contract")
            .longOpt("benchmark")
            .build();

    static Option CATSL = Option.builder("catsl")
            .required(true)
            .desc("Select <CATSL_FILE> to load")
            .longOpt("catsl-file")
            .hasArg().argName("CATSL_FILE")
            .build();

    static Option NO_VER = Option.builder("no-ver")
            .desc("Only generates Proof Obligations (.key files)")
            .longOpt("no-verification")
            .build();

    static Option INTERACTIVE = Option.builder("i")
            .desc("Interactive mode via KeY GUI (working for verification of single CAT)")
            .longOpt("interactive")
            .build();

    static Option JAVA_CLASS = Option.builder("java")
            .required(true)
            .desc("Refer to code in <JAVA_CLASS>.java")
            .longOpt("java-file")
            .hasArg().argName("JAVA_CLASS")
            .build();

    static Option PROOF_OBLIGATION = Option.builder("po")
            .desc("Load <PROOF_OBLIGATION> (.key/.proof file)")
            .longOpt("proof-obligation")
            .hasArg().argName("<PROOF_OBLIGATION>")
            .build();


    static final OptionGroup TARGET_OPTION = new MyOptionGroup().addOption(SINGLE_CAT).addOption(FULL_PROOF);
    static final OptionGroup PO_OR_BENCHMARK = new MyOptionGroup().addOption(BENCHMARK).addOption(NO_VER);
    static final OptionGroup MAIN_OPTION_GROUP = new MyOptionGroup().addOption(CATSL).addOption(PROOF_OBLIGATION).addOption(HELP_OPTION);


    static final Options loadPOOptions = new Options();
    static final Options mainOptions = new Options();
    static final Options loadCATOptions = new Options();
    static final Options helpOption = new Options();

    static {
        MAIN_OPTION_GROUP.setRequired(true);
        mainOptions.addOptionGroup(MAIN_OPTION_GROUP);

        loadCATOptions.addOptionGroup(TARGET_OPTION);
        loadCATOptions.addOptionGroup(PO_OR_BENCHMARK);
//        loadCATOptions.addOption(NO_VER);
        loadCATOptions.addOption(BENCHMARK);
        loadCATOptions.addOption(CATSL);
        loadCATOptions.addOption(JAVA_CLASS);
        loadCATOptions.addOption(INTERACTIVE);
        loadCATOptions.addOption(SHOW_STATS);
        loadCATOptions.addOption(HELP_OPTION);


        loadPOOptions.addOption(PROOF_OBLIGATION);
        loadPOOptions.addOption(INTERACTIVE);
        loadPOOptions.addOption(SHOW_STATS);

        helpOption.addOption(HELP_OPTION);

    }

    static ProofGenMode proofGenMode;
    static boolean requiredVerification;
    static boolean benchmarkRequired;
    static String contractName;
    static String catsFilename;
    static String javaClassFilename;
    static String proofObligationFileName;
//    static String keyHome = System.getenv("KEY4CATs");
    static KeYMode executionMode = KeYMode.AUTO;
    static LoadingMode loadingMode;
    static boolean stats = false;
    static String directory;


    public static void main(String [] args) throws IOException {
        checkOptions(args);
        checkOptions();
        try {
            if(loadingMode == LoadingMode.PO){
                directory = new File(proofObligationFileName).getParent();
                LOGGER.info(String.format("Loading proof obligation from %s", proofObligationFileName));
                prove( proofObligationFileName);
            }
            else {
                File catsFile = new File( catsFilename);
                directory = catsFile.getParent();
                ProofCATsBuilder p = new ProofCATsBuilder(catsFile, contractName, javaClassFilename, proofGenMode);
                Set<String> contractNames = p.getContractIds();
                if(!contractNames.iterator().hasNext())
                    throw new RuntimeException( "No contract to be proven (check what command you run)");
                if (proofGenMode == ProofGenMode.SINGLE) {
                    LOGGER.info(String.format("Single proof for \"%s\"", contractName));
                    p.generateProof(directory, contractName);

                    if (requiredVerification) {
                        if(benchmarkRequired){
                            int warmUpTime = 10;
                            LOGGER.info("BENCHMARK WAS REQUESTED");
                            LOGGER.info("STARTING WARM UP (executing "+ warmUpTime +" times)");
                            ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.WARN);
                            for(int i=0;i<warmUpTime;i++) {
                                prove(directory, contractName);
                                System.out.print((i+1)+ "...");
                            }
                            System.out.println("done");
                            ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.TRACE);
                            LOGGER.info("END WARM UP");
                        }
                        prove(directory, contractName);
                    }
                }
                if (proofGenMode == ProofGenMode.FULL) {
                    if (contractNames.size() == 1)
                        LOGGER.warn(String.format("Contract '%s' does not assume any contract. Use '-s' option instead of '-f'.", contractNames.stream().findFirst().get()));
                    LOGGER.info(String.format("Full proof for %s. %s contracts to be proven: %s ", contractName, contractNames.size(), contractNames));
                    multipleContracts(p, directory, contractNames);

                }
                if (proofGenMode == ProofGenMode.ALL) {
                    LOGGER.info(String.format("Prove all %s contracts from file %s", contractNames.size(), catsFilename));
                    multipleContracts(p, directory, contractNames);
                }
            }
        }catch(RuntimeException e){
            System.err.print(e);
            System.exit(1);
        }
    }

    private static void multipleContracts(ProofCATsBuilder p,String directory, Set<String> contractNames) throws IOException {

        Set<String> openProofs = new LinkedHashSet<>();
        for (String cName : contractNames) {
            p.generateProof(directory, cName);
        }
        if(requiredVerification) {
            int i = 0;
            for (String cName : contractNames) {
                i++;
                boolean proved = prove(directory, cName);
                if (!proved)
                    openProofs.add(cName);
            }
            System.out.printf("[Closed:%s, Open:%s, Tot:%s]%n", i, openProofs.size(), contractNames.size());
            if(!openProofs.isEmpty()){
                System.out.printf("%s contracts could not be proven%n", openProofs.size());
                openProofs.forEach(x->System.out.printf(String.format("\t - Could not prove: [%s]%n",x)));
            }
        }
    }

    private static boolean prove(String proofObligationFileName) {
        if(executionMode == KeYMode.GUI) {
            LOGGER.info("Starting Interactive Mode (continue in the newly opened window)");
            openFileWithGUI(proofObligationFileName);
            return false;
        }
        else if(executionMode == KeYMode.AUTO) {
            LOGGER.info("Starting Auto Mode");
            return openFileWithCLI(proofObligationFileName);
        }else {
            throw new RuntimeException("Execution Mode should be GUI or AUTO but found: "+ executionMode);
        }
    }

    private static boolean prove(String directory, String contractName) {
        if(executionMode == KeYMode.GUI) {
            LOGGER.info("Starting Interactive Mode (continue in the newly opened window)");
            openFileWithGUI(directory, contractName);
            return false;
        }
        else if(executionMode == KeYMode.AUTO) {
            LOGGER.info(String.format("Starting Auto Mode for %s",contractName));
            return openFileWithCLI(directory, contractName);
        }else {
            throw new RuntimeException("Execution Mode should be GUI or AUTO but found: "+ executionMode);
        }
    }

    private static void checkOptions(){
        if((proofGenMode == ProofGenMode.ALL || proofGenMode == ProofGenMode.FULL) && benchmarkRequired)
            throw  new RuntimeException("Only one proof at a time can be verified as benchmark");
        if((proofGenMode == ProofGenMode.ALL || proofGenMode == ProofGenMode.FULL) && executionMode == KeYMode.GUI)
            throw  new RuntimeException("Only one proof at a time can be loaded interactively: more interactive proofs are currently not supported");
    }

    private static void checkOptions(String[] args){
        CommandLine cl;
        CommandLineParser parser = new DefaultParser();
        Set<String> argsSet = Arrays.stream(args).collect(Collectors.toSet());

        try {
            if(argsSet.contains("-" + HELP_OPTION.getOpt()) || argsSet.contains("--" + HELP_OPTION.getLongOpt())) {
                printHelp();
            }
            else if(argsSet.contains("-" + PROOF_OBLIGATION.getOpt()) || argsSet.contains("--" + PROOF_OBLIGATION.getLongOpt())) {
                try{
                    cl = parser.parse(loadPOOptions, args);
                    loadingMode = LoadingMode.PO;
                    parseArgs(cl);
                }catch (ParseException e){
                    System.err.println("Problem with arguments, Type 'cats --help'");
                    throw new RuntimeException("Cannot parse arguments\n" + e.getMessage());
                }
            }
            else if(argsSet.contains("-" + CATSL.getOpt()) || argsSet.contains("--" + CATSL.getLongOpt())) {
                cl = parser.parse(loadCATOptions, args);
                loadingMode = LoadingMode.CAT;
                parseArgs(cl);
            }else
                throw new RuntimeException(String.format("Missing required options:\n\t" +
                        "Include '-catsl' for loading CATs (.cats) or '-po' to load proof obligations (.key, .proof)\n%s", "Type 'cats --help' for help"));
        }catch (ParseException e){
            System.err.println("Problem with arguments, Type 'cats --help'");
            throw new RuntimeException("Cannot parse arguments\n" + e.getMessage());

        }
    }

    private static void printHelp(){
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        options.addOptions(loadCATOptions);
        options.addOptions(loadPOOptions);
        options.addOptions(helpOption);
        formatter.setWidth(120);
        PrintWriter pw = new PrintWriter(System.out);
        formatter.setOptionComparator(null);
        String verificationMessage = "\n\tcats TARGET -catsl <CATSL_FILE> -java <JAVA_CLASS> [-i] [-stats] ";
        verificationMessage += "\n\tcats TARGET -catsl <CATSL_FILE> -java <JAVA_CLASS> -no-ver";
        verificationMessage += "\n\tcats --help";
        pw.println("Usage: " + verificationMessage);
        pw.println("With '-no-ver' proof obligation are generated but not verified");
        String targetOptionsMessage = "TARGET can be: '-s <CAT_ID>', '-b <CAT_ID>', '-f <CAT_ID>', or '-all'";
        pw.println(targetOptionsMessage);
        pw.println();
        formatter.printOptions(pw, formatter.getWidth(),options,formatter.getLeftPadding(),formatter.getDescPadding());
        pw.flush();
    }

    private static void parseArgs(CommandLine cl){
        requiredVerification = !cl.hasOption(NO_VER);
        benchmarkRequired = cl.hasOption(BENCHMARK);
        if(loadingMode == LoadingMode.CAT){
            if(cl.hasOption(SINGLE_CAT)) {
                contractName = cl.getOptionValue(SINGLE_CAT);
                proofGenMode = ProofGenMode.SINGLE;
            }
            if(cl.hasOption(FULL_PROOF)) {
                contractName = cl.getOptionValue(FULL_PROOF);
                proofGenMode = ProofGenMode.FULL;
            }
            if(!cl.hasOption(SINGLE_CAT) && !cl.hasOption(FULL_PROOF))
                proofGenMode = ProofGenMode.ALL;

            if(cl.hasOption(CATSL)){
                catsFilename = cl.getOptionValue(CATSL);
            }
            if(cl.hasOption(JAVA_CLASS)){
                javaClassFilename = cl.getOptionValue(JAVA_CLASS);
            }
        }
        if(loadingMode == LoadingMode.PO){
            if(cl.hasOption(PROOF_OBLIGATION)) {
                proofObligationFileName = cl.getOptionValue(PROOF_OBLIGATION);
            }
        }
        if(cl.hasOption(INTERACTIVE)) {
            executionMode = KeYMode.GUI;
        }
        stats = cl.hasOption(SHOW_STATS);
    }

    private static void openFileWithGUI(String proofObligationFileName) {
        File keyFile = new File(proofObligationFileName);
        WindowUserInterfaceControl windowUserInterfaceControl = MainWindow.getInstance().getUserInterface();
        loadCommandLineFiles(windowUserInterfaceControl, List.of(keyFile));
    }

    private static void openFileWithGUI(String directory, String contractName) {
        openFileWithGUI(String.format("%s/%s.key", directory, contractName));
    }

    private static void muteOut(){
        System.setOut(mutedOut);
    }
    private static void unmuteOut(){
        System.setOut(unmutedOut);
    }

    private static boolean openFileWithCLI(String proofObligationFileName){
        try{
            File keyFile = new File(proofObligationFileName);

            LOGGER.info(String.format("Loading %s ...", proofObligationFileName));
            KeYEnvironment<DefaultUserInterfaceControl> env = KeYEnvironment.load(keyFile);
            LOGGER.info("Started proof...");
            env.getProofControl().startAndWaitForAutoMode(env.getLoadedProof());
            Proof proof = env.getLoadedProof();
            boolean proved = proof.closed();
            saveProof(proof, proofObligationFileName);
            String proofInfo = ((proved ? String.format("%s",green("CLOSED (proven)")) : String.format("%s",red("OPEN (cannot prove)"))));
            if(stats) {
                LOGGER.info(proofInfo);
                LOGGER.info(env.getLoadedProof().getStatistics().toString());
            } else
                LOGGER.info(String.format("%s {n_nodes:%s}{time:%sms}%n",  proofInfo, env.getLoadedProof().countNodes(), env.getLoadedProof().getAutoModeTime()));
            return proved;
        }catch (ProblemLoaderException e){
            throw new RuntimeException(e);
        }
    }

    private static void saveProof(Proof proof, String proofObligationFileNameFileName) {

        String savingProofName =
                proofObligationFileNameFileName.endsWith(".proof")?
                        proofObligationFileNameFileName :
                        String.format("%s.proof", proofObligationFileNameFileName);
        LOGGER.info(String.format("Saving proof %s ... ", savingProofName));
        File proofFile = new File(savingProofName);
        ProofSaver ps = new ProofSaver(proof, proofFile, true);
        String error = ps.save();
        if(error != null)
            throw new RuntimeException(error);

    }

    private static boolean openFileWithCLI(String directory, String contractName) {
        return openFileWithCLI(String.format("%s/%s.key", directory, contractName));
    }



    static String red(String s){
        return "\033[31m " + s + "\033[0m ";
    }
    static String green(String s){
        return "\033[32m " + s + "\033[0m ";
    }

}

class MyOptionGroup extends OptionGroup {
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        Iterator<Option> iter = this.getOptions().iterator();
        buff.append("[");
        while(iter.hasNext()) {
            Option option = iter.next();
            if (option.getOpt() != null) {
                buff.append("-");
                buff.append(option.getOpt());
            } else {
                buff.append("--");
                buff.append(option.getLongOpt());
            }

            if (option.getArgName() != null) {
                buff.append(' ');
                buff.append(option.getArgName());
            }

            if (iter.hasNext()) {
                buff.append(", ");
            }
        }
        buff.append("]");
        return buff.toString();
    }
}