/* This file is part of KeY - https://key-project.org
 * KeY is licensed under the GNU General Public License Version 2
 * SPDX-License-Identifier: GPL-2.0-only */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;

import de.uka.ilkd.key.proof.Proof;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CATsTests {

    String fileName= "results_"
            + new SimpleDateFormat("ddMMyy_HHmm").format(new Date())
            + ".csv";;

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
                    "placeBet",
                    "placeBetInner",
                    "placeBetInnerSimple",
                    "trivialMultipleObsCallee",
                    "callPlaceBetOnceSufficientContract",
                    "callPlaceBetDecidedBet"
            })
    public void succeedingProofs(String contract) throws Exception{
        Path file = Paths.get(String.format("src/test/resources/%s.key", contract));
        Proof proof = prove(file);
        printResults(contract,proof.countNodes());
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
                    "callBetTwiceFail",
                    "callPlaceBetOnceInsufficientContractFail"
            })
    public void failingProofs(String contract) throws Exception{
        Path file = Paths.get(String.format("src/test/resources/%s.key", contract));
        Proof proof = prove(file);
        printResults(contract,proof.countNodes());
        assert(!proof.closed());
    }

    private void printResults(String contractName, int countNodes){
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileName, true), true);
            out.printf("%s,%s%n", contractName, countNodes);
            out.close();
        }catch(IOException exception){
            System.err.println(exception);
        }
    }


    private Proof prove(Path file)
            throws ProblemLoaderException {
        KeYEnvironment<DefaultUserInterfaceControl> env = KeYEnvironment.load(file.toFile());
        env.getProofControl().startAndWaitForAutoMode(env.getLoadedProof());
        return env.getLoadedProof();
    }

}
