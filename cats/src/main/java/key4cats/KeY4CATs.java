package key4cats;


import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;
import de.uka.ilkd.key.gui.MainWindow;
import de.uka.ilkd.key.gui.WindowUserInterfaceControl;
import de.uka.ilkd.key.gui.actions.ShowProofStatistics;
import de.uka.ilkd.key.proof.Proof;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;
import de.uka.ilkd.key.proof.io.ProofSaver;
import de.uka.ilkd.key.settings.ProofSettings;
import de.uka.ilkd.key.settings.StrategySettings;
import de.uka.ilkd.key.util.MiscTools;
import key4cats.parsers.CATs.CATsBaseListener;
import org.apache.commons.cli.HelpFormatter;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static de.uka.ilkd.key.core.Main.loadCommandLineFiles;

import org.apache.commons.cli.*;
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

    static Option RULE_APP_LIMIT = Option.builder("max")
            .desc("Set the number of maximum rule application to <N_RULES> (default 10K)")
            .longOpt("max-rules")
            .hasArg().argName("N_RULES")
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
            .desc("Warming up JVM <N_TIMES> before verification (N_TIMES=10 by default)")
            .longOpt("benchmark")
            .hasArg().argName("N_TIMES").optionalArg(true)
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
            .desc("Interactive mode via KeY GUI (only for single proofs)")
            .longOpt("interactive")
            .build();

    static Option INTERACTIVE_PO = Option.builder("i")
            .desc("Interactive mode via KeY GUI")
            .longOpt("interactive")
            .build();

    static Option JAVA_CLASS = Option.builder("java")
            .required(true)
            .desc("Refer to code in <JAVA_CLASS>.java")
            .longOpt("java-file")
            .hasArg().argName("JAVA_CLASS")
            .build();

    static Option PROOF_OBLIGATION = Option.builder("po")
            .desc("Load <PO> (.key/.proof file)")
            .longOpt("proof-obligation")
            .hasArg().argName("PO")
            .build();


    static final OptionGroup TARGET_OPTION = new MyOptionGroup().addOption(SINGLE_CAT).addOption(FULL_PROOF);
    static final OptionGroup PO_OR_BENCHMARK = new MyOptionGroup().addOption(NO_VER).addOption(BENCHMARK);
    static final OptionGroup MAIN_OPTION_GROUP = new MyOptionGroup().addOption(CATSL).addOption(PROOF_OBLIGATION).addOption(HELP_OPTION);


    static final Options loadPOOptions = new Options();
    static final Options mainOptions = new Options();
    static final Options loadCATOptions = new Options();
    static final Options helpOption = new Options();

    static {
        MAIN_OPTION_GROUP.setRequired(true);
        mainOptions.addOptionGroup(MAIN_OPTION_GROUP);

        loadCATOptions.addOptionGroup(TARGET_OPTION);
//        loadCATOptions.addOption(NO_VER);
//        loadCATOptions.addOption(BENCHMARK);
        loadCATOptions.addOption(CATSL);
        loadCATOptions.addOption(JAVA_CLASS);
        loadCATOptions.addOptionGroup(PO_OR_BENCHMARK);
        loadCATOptions.addOption(INTERACTIVE);
        loadCATOptions.addOption(SHOW_STATS);
        loadCATOptions.addOption(RULE_APP_LIMIT);
//        loadCATOptions.addOption(HELP_OPTION);


        loadPOOptions.addOption(PROOF_OBLIGATION);
        loadPOOptions.addOption(INTERACTIVE_PO);
        loadPOOptions.addOption(SHOW_STATS);
        loadPOOptions.addOption(BENCHMARK);
        loadPOOptions.addOption(RULE_APP_LIMIT);

        helpOption.addOption(HELP_OPTION);
    }

    static ProofGenMode proofGenMode;
    static final int DEFAULT_BENCHMARK_TIMES = 10;
    static int maxRuleAppSteps = -1;
    static int maxRuleAppStepsDEFAULT=10000;
    static boolean requiredVerification;
    static boolean benchmarkRequired;
    static boolean SAVE_CSV = true;
    static int warmupTimes;
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
        checkMaxRuleApp();
        try {
            if(loadingMode == LoadingMode.PO){
                directory = new File(proofObligationFileName).getParent();
                LOGGER.info(String.format("Loading proof obligation from %s", proofObligationFileName));
                if(benchmarkRequired){
                    int warmUpTime = warmupTimes;
                    LOGGER.info("BENCHMARK WAS REQUESTED");
                    LOGGER.info("STARTING WARM UP (executing "+ warmUpTime +" times)");
                    ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.WARN);
                    SAVE_CSV = false;
                    for(int i=0;i<warmUpTime;i++) {
                        prove(proofObligationFileName);
                        System.out.print((i+1)+ "...");
                    }
                    System.out.println("done");
                    ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.TRACE);
                    LOGGER.info("END WARM UP");
                }
                SAVE_CSV = true;
                prove(proofObligationFileName);
            }
            else {
                File catsFile = new File(catsFilename);
                directory = catsFile.getAbsoluteFile().getParent();
                ProofCATsBuilder p = new ProofCATsBuilder(catsFile, contractName, javaClassFilename, proofGenMode);
                Set<String> contractNames = p.getContractIds();
                if(!contractNames.iterator().hasNext())
                    throw new RuntimeException( "No contract to be proven (check what command you run)");
                if (proofGenMode == ProofGenMode.SINGLE) {
                    LOGGER.info(String.format("Single proof for \"%s\"", contractName));
                    p.generateProof(directory, contractName);

                    if (requiredVerification) {
                        if(benchmarkRequired){
                            int warmUpTime = warmupTimes;
                            LOGGER.info("BENCHMARK WAS REQUESTED");
                            LOGGER.info("STARTING WARM UP (executing "+ warmUpTime +" times)");
                            ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.WARN);
                            SAVE_CSV = false;
                            for(int i=0;i<warmUpTime;i++) {
                                prove(directory, contractName);
                                System.out.print((i+1)+ "...");
                            }
                            System.out.println("done");
                            ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.TRACE);
                            LOGGER.info("END WARM UP");
                        }
                        SAVE_CSV = true;
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
                if(benchmarkRequired){
                    int warmUpTime = warmupTimes;
                    LOGGER.info("BENCHMARK WAS REQUESTED");
                    LOGGER.info("STARTING WARM UP (executing "+ warmUpTime +" times)");
                    ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.WARN);
                    SAVE_CSV = false;
                    for(int j=0;j<warmUpTime;j++) {
                        prove(directory, cName);
                        System.out.print((j+1)+ "...");
                    }
                    System.out.println("done");
                    ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT")).setLevel(Level.TRACE);
                    LOGGER.info("END WARM UP");
                }

                SAVE_CSV = true;
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
        if((executionMode == KeYMode.GUI) && benchmarkRequired)
            throw  new RuntimeException("Benchmarks can be run in NON interactive mode (automode). Remove the flag -i or the flag -b, and retry.");
        if((proofGenMode == ProofGenMode.ALL || proofGenMode == ProofGenMode.FULL) && benchmarkRequired)
            LOGGER.info("Benchmark is requested for multiple CATs.");
        if((proofGenMode == ProofGenMode.ALL || proofGenMode == ProofGenMode.FULL) && executionMode == KeYMode.GUI)
            throw  new RuntimeException("Only one proof at a time can be loaded interactively: more interactive proofs are currently not supported");
    }

    private static void checkMaxRuleApp(){
        int maxRuleApp = ProofSettings.DEFAULT_SETTINGS.getStrategySettings().getMaxSteps();
        if(maxRuleAppSteps == -1) {
            if (maxRuleApp != maxRuleAppStepsDEFAULT) {
                LOGGER.info("Current setting for limit of rule application (" + maxRuleApp + ") is not the default one (" + maxRuleAppStepsDEFAULT + ")");
                LOGGER.info("Setting limit of rule application to default (" + maxRuleAppStepsDEFAULT + ")");
                ProofSettings.DEFAULT_SETTINGS.getStrategySettings().setMaxSteps(maxRuleAppStepsDEFAULT);
            }
        }else{
            if (maxRuleAppSteps == maxRuleAppStepsDEFAULT) {
                LOGGER.info("The given limit of rule applications is the default one (" + maxRuleAppStepsDEFAULT + "): no changes are needed");
            }else {
                LOGGER.info("Setting limit of rule applications to " + maxRuleAppSteps + ". It was " +
                        maxRuleApp + (maxRuleApp==maxRuleAppStepsDEFAULT ? " (default)" : " (non-default)"));
            }
            ProofSettings.DEFAULT_SETTINGS.getStrategySettings().setMaxSteps(maxRuleAppSteps);
        }

    }

    private static void checkOptions(String[] args){
        CommandLine cl;
        CommandLineParser parser = new DefaultParser();
        Set<String> argsSet = Arrays.stream(args).collect(Collectors.toSet());

        try {
            if(argsSet.contains("-" + HELP_OPTION.getOpt()) || argsSet.contains("--" + HELP_OPTION.getLongOpt())) {
                printHelp();
                System.exit(0);
            }
            else if(argsSet.contains("-" + PROOF_OBLIGATION.getOpt()) || argsSet.contains("--" + PROOF_OBLIGATION.getLongOpt())) {
                try{
                    cl = parser.parse(loadPOOptions, args);
                    loadingMode = LoadingMode.PO;
                    parseArgs(cl);
                }catch (ParseException e){
                    System.err.println("Problem with arguments, Run with '--help'");
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

        printHelpCATs();
        System.out.println();
        printHelpPOs();
        System.out.println();
        printHelpHelp();
    }

    private static void printHelpCATs(){
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        options.addOptions(loadCATOptions);
        formatter.setWidth(120);
        PrintWriter pw = new PrintWriter(System.out);
        formatter.setOptionComparator(null);
        String verificationMessage = "\n\tkey4cats TARGET -catsl <CATSL_FILE> -java <JAVA_CLASS> [-i | -b <N_TIMES>] [-stats]  [-max <N_RULES>]";
        verificationMessage += "\n\tkey4cats TARGET -catsl <CATSL_FILE> -java <JAVA_CLASS> -no-ver";
//        verificationMessage += "\n\tkey4cats --help";
        pw.println("Usage for KeY4CATs...");
        pw.println("1. LOADING CATSL FILE");
        pw.println("> Usage: " + verificationMessage);
        pw.println("With '-no-ver' proof obligation are generated but not verified");
        String targetOptionsMessage = "TARGET can be empty, '-s <CAT_ID>', or '-f <CAT_ID>' (if empty  all CATs are targetted)";
        pw.println(targetOptionsMessage);
        pw.println();
        formatter.printOptions(pw, formatter.getWidth(),options,formatter.getLeftPadding(),formatter.getDescPadding());
        pw.flush();
    }


    private static void printHelpPOs(){
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        options.addOptions(loadPOOptions);
        formatter.setWidth(120);
        PrintWriter pw = new PrintWriter(System.out);
        formatter.setOptionComparator(null);
        String verificationMessage = "\n\tkey4cats -po <KEY_FILE> [-i | -b <N_TIMES>] [-stats] [-max <N_RULES>]";
        pw.println("2. LOADING KEY FILE");
        pw.println("> Usage: " + verificationMessage);
        String targetOptionsMessage = "<KEY_FILE> can have extensions .key and .proof";
        pw.println(targetOptionsMessage);
        pw.println();
        formatter.printOptions(pw, formatter.getWidth(),options,formatter.getLeftPadding(),formatter.getDescPadding());
        pw.flush();
    }


    private static void printHelpHelp(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);
        PrintWriter pw = new PrintWriter(System.out);
        formatter.setOptionComparator(null);
        pw.println();
        String verificationMessage = "Print this message with 'key4cats --help (-h)' ";
        pw.println(verificationMessage);
        pw.flush();
    }
    private static void parseArgs(CommandLine cl){
        requiredVerification = !cl.hasOption(NO_VER);
        if(cl.hasOption(BENCHMARK)){
            benchmarkRequired = true;
            if(cl.getOptionValue(BENCHMARK) == null)
                warmupTimes = DEFAULT_BENCHMARK_TIMES;
            else
                warmupTimes= Integer.parseInt(cl.getOptionValue(BENCHMARK));
        }

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
            if(cl.hasOption(RULE_APP_LIMIT)){
                int max = Integer.parseInt(cl.getOptionValue(RULE_APP_LIMIT));
                if(max <= 0){
                    throw new RuntimeException("The limit of rule applications must be greater than zero. Given value: " + max);
                }
                maxRuleAppSteps = max;
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

            LOGGER.info(String.format("Loading %s ...", Paths.get(keyFile.getAbsolutePath()).normalize()));
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
            if(SAVE_CSV) {
                File file = new File(MiscTools.toValidFileName(proof.name().toString()) + ".csv");
                try (BufferedWriter writer =
                             new BufferedWriter(
                                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    writer.write(ShowProofStatistics.getCSVStatisticsMessage(proof));
                } catch (IOException e) {
                    LOGGER.error("Failed to write proof stats", e);
                }
            }
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
        File proofFile = new File(savingProofName);
        LOGGER.info(String.format("Saving proof %s ... ", Paths.get(proofFile.getAbsolutePath()).normalize()));
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