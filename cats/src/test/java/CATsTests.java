/* This file is part of KeY - https://key-project.org
 * KeY is licensed under the GNU General Public License Version 2
 * SPDX-License-Identifier: GPL-2.0-only */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;

import de.uka.ilkd.key.proof.io.ProblemLoaderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CATsTests {

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
                    "placeBetInnerSimple"
            })
    public void succeedingProofs(String contract) throws Exception{
        Path file = Paths.get(String.format("src/test/resources/%s.key", contract));
        assert(prove(file));
    }

    @ParameterizedTest
    @DisplayName("Proof should fail")
    @ValueSource(strings =
            {
                    "removeOneFail",
                    "removeTwoFail",
                    "removeThreeFail",
                    "removeThreeNoCallsRemoveOneFail",
            })
    public void failingProofs(String contract) throws Exception{
        Path file = Paths.get(String.format("src/test/resources/%s.key", contract));
        assert(!prove(file));
    }

    private boolean prove(Path file)
            throws ProblemLoaderException, IOException {
        KeYEnvironment<DefaultUserInterfaceControl> env = KeYEnvironment.load(file.toFile());
        env.getProofControl().startAndWaitForAutoMode(env.getLoadedProof());
        return env.getLoadedProof().closed();
    }

}
