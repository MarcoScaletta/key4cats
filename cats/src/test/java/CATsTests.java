/* This file is part of KeY - https://key-project.org
 * KeY is licensed under the GNU General Public License Version 2
 * SPDX-License-Identifier: GPL-2.0-only */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import de.uka.ilkd.key.control.DefaultProofControl;
import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;

import de.uka.ilkd.key.proof.Proof;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class CATsTests {

    private final String fileName;
    public CATsTests() throws IOException {
        String resultInfo = "results_info";
        fileName = Files.readString(new File(resultInfo).toPath());
    }



    @ParameterizedTest
    @DisplayName("Proof should succeed")
    @ValueSource(strings =
            {
                    "removeOne",
                    "removeOneWithCond",
                    "removeOneLT",
                    "removeTwo",
                    "removeThree",
                    "placeBetAbsTrSelf",
                    "placeBetInnerSimple",
                    "trivialMultipleObsCallee",
                    "callPlaceBetOnceAfterRemoveOne",
                    "callPlaceBetDecidedBet",
                    "simpleSchemTraceInclusion",
                    "callDummyProc1AssumeNoDummyProc1Before",
                    "callDummyProc2And1SafeLocalContextOr",
                    "callPlaceBetOnlyOnceAssumePreTrace",
                    "callPlaceBetOnceSetVarsToOne",
                    "casinoCaseStudySimple",
                    "casinoCaseStudySimpleCompletePlaceBet",
                    "noObservationsTrivialPrecondition",
                    "removeOneOnce",
                    "removeOneTwice",
                    "casinoCaseStudyNoDoubleDecision"
            }
            )
    public void succeedingProofs(String contract) {
        Path file = Paths.get(String.format("src/test/resources/%s.key", contract));
        long startTime = System.nanoTime();
        Proof proof = prove(file);
        long elapsedTime = System.nanoTime() - startTime;
        if(proof.closed())
            printResults(contract,proof.countNodes());
        else
            printResults(contract,-1000);
        System.out.println(proof.countNodes());System.out.printf("Time:%sms%n", TimeUnit.NANOSECONDS.toMillis(elapsedTime));
        System.out.printf("Avg time:%sμs%n", TimeUnit.NANOSECONDS.toMicros(elapsedTime / proof.countNodes()));
        assert(proof.closed());
    }



    @ParameterizedTest
    @DisplayName("Proof should fail")
    @ValueSource(strings =
            {
                    "removeOneFail",
                    "removeTwoFail",
                    "removeThreeFail",
                    "removeThreeNoCallsRemoveOneFail",
                    "removeOneWithCondFail",
                    "callBetTwiceFail", "callPlaceBetOnceInsufficientContractFail",
                    "callDummyProc1And2And3CATFail",
                    "simpleSchemTraceInclusionFail",
                    "callDummyProc1WithAssumptionsButAssumeNothingFail",
                    "callPlaceBetOnceAfterRemoveOneUnderspecifiedFail",
                    "callPlaceBetOnlyOncePreCondNotMetFail",
                    "casinoCaseStudySimpleFail",
                    "casinoCaseStudySimpleCompletePlaceBetFail",
                    "removeOneMissingStartEvFail"
            })
    public void failingProofs(String contract) {
        Path file = Paths.get(String.format("src/test/resources/%s.key", contract));
        long startTime = System.nanoTime();
        Proof proof = prove(file);
        long elapsedTime = System.nanoTime() - startTime;
        if(!proof.closed())
            printResults(contract,proof.countNodes());
        else
            printResults(contract,-1000);

        System.out.printf("Nodes: %s%n", proof.countNodes());
        System.out.printf("Time: %sms%n", TimeUnit.NANOSECONDS.toMillis(elapsedTime));
        System.out.printf("Avg time: %sμs%n", TimeUnit.NANOSECONDS.toMicros(elapsedTime / proof.countNodes()));
        assert(!proof.closed());
    }

    private void printResults(String contractName, int countNodes){
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileName, true), true);
            out.printf("%s,%s%n", contractName, countNodes);
            out.close();
        }catch(IOException exception){
            System.err.println(exception.getMessage());
        }
    }

    private Proof prove(Path file)  {
        KeYEnvironment<DefaultUserInterfaceControl> env = Utils.loadPO(file);
        env.getProofControl().startAndWaitForAutoMode(env.getLoadedProof());
        if (((DefaultProofControl) env.getProofControl()).getUncaughtException() != null) {
            throw new RuntimeException("Unexpected Exception during test: ", ((DefaultProofControl) env.getProofControl()).getUncaughtException());
        }
        return env.getLoadedProof();
    }

}
