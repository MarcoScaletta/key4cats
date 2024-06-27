package key4cats;

import key4cats.parsers.CATs.CATsLexer;
import key4cats.parsers.CATs.CATsParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class KeY4CATs {
    public static void main(String [] args) throws IOException {
        File f = new File("cats/proof.cats");
        final InputStream targetStream = new DataInputStream(new FileInputStream(f));
        String s = new String(targetStream.readAllBytes(), StandardCharsets.UTF_8);
        ProofCATsBuilder p = new ProofCATsBuilder(s);

    }
}
