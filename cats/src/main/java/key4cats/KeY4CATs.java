package key4cats;


import de.uka.ilkd.key.gui.MainWindow;
import de.uka.ilkd.key.gui.WindowUserInterfaceControl;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static de.uka.ilkd.key.core.Main.loadCommandLineFiles;

public class KeY4CATs {



    public static void main(String [] args) throws IOException, ProblemLoaderException {
        String keyHome = System.getenv("KEY");
        File catFile;
        if(args.length == 0)
            catFile = new File(keyHome+"/key.ui/examples/traces/test/test.cats");
        else
            catFile = new File(keyHome + args[0]);
        String directory = catFile.getParent();
        final InputStream targetStream = new DataInputStream(new FileInputStream(catFile));
        String s = new String(targetStream.readAllBytes(), StandardCharsets.UTF_8);
        ProofCATsBuilder p = new ProofCATsBuilder(s);

        Set<Identifier> contractNames = p.getContractIds();


        if(p.getMode() == ProofCATsBuilder.Mode.SINGLE){
            if(!contractNames.iterator().hasNext())
                throw new RuntimeException("No Contract Provided for Single Mode");
            Identifier idContract = contractNames.iterator().next();
            File keyFile = new File(String.format("%s/%s.key", directory, idContract.toKeY()));
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
            dataOutputStream.writeBytes(p.assembleProof(idContract).toKeY());
            dataOutputStream.flush();
            openFileWithGUI(keyFile);
        }else {
            for (Identifier contractName : contractNames) {
                File keyFile = new File(String.format("%s/%s.key", directory, contractName.toKeY()));
                DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
                dataOutputStream.writeBytes(p.assembleProof(contractName).toKeY());
                dataOutputStream.flush();
            }
        }
    }

    private static void openFileWithGUI(File file) {
        WindowUserInterfaceControl windowUserInterfaceControl = MainWindow.getInstance().getUserInterface();
        loadCommandLineFiles(windowUserInterfaceControl, List.of(file));
    }
}
