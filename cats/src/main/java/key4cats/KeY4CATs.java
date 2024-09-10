package key4cats;


import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;
import de.uka.ilkd.key.gui.MainWindow;
import de.uka.ilkd.key.gui.WindowUserInterfaceControl;
import de.uka.ilkd.key.proof.Proof;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static de.uka.ilkd.key.core.Main.loadCommandLineFiles;

public class KeY4CATs {
    public static void main(String [] args) throws IOException, ProblemLoaderException {
        String keyHome = System.getenv("KEY");
        File catFile = new File(keyHome+"/key.ui/examples/traces/test/test.cats");

        final InputStream targetStream = new DataInputStream(new FileInputStream(catFile));
        String s = new String(targetStream.readAllBytes(), StandardCharsets.UTF_8);
        ProofCATsBuilder p = new ProofCATsBuilder(s);
        Set<Identifier> contractNames = p.getContractIds();
        for (Identifier contractName : contractNames){
            File keyFile = new File(String.format(keyHome + "/key.ui/examples/traces/test/%s.key", contractName.toKeY()));
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
            dataOutputStream.writeBytes(p.assembleProof(contractName).toKeY());
            dataOutputStream.flush();
        }
    }
//
    private static void openFileWithGUI(File file)
            throws ProblemLoaderException {
        WindowUserInterfaceControl windowUserInterfaceControl = MainWindow.getInstance().getUserInterface();
        loadCommandLineFiles(windowUserInterfaceControl, List.of(file));

    }
}
