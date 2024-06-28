package key4cats;


import java.io.*;
import java.nio.charset.StandardCharsets;

public class KeY4CATs {
    public static void main(String [] args) throws IOException {
        String keyHome = System.getenv("KEY");
        File catFile = new File(keyHome+"/cats/proof.cats");

        final InputStream targetStream = new DataInputStream(new FileInputStream(catFile));
        String s = new String(targetStream.readAllBytes(), StandardCharsets.UTF_8);
        ProofCATsBuilder p = new ProofCATsBuilder(s);
        String contractName = p.getPathProblem();
        File keyFile = new File(String.format(keyHome+"/key.ui/examples/traces/%s.key", contractName));
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
        dataOutputStream.writeBytes(p.getKeYProof());
        dataOutputStream.flush();

        File problemToSolve = new File(String.format(keyHome+"/cats/problem_to_proof"));
        DataOutputStream dt = new DataOutputStream(new FileOutputStream(problemToSolve, false));
        dt.writeBytes(contractName);
        dt.flush();
    }
}
