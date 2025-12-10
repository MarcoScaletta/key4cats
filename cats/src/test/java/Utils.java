import de.uka.ilkd.key.control.DefaultUserInterfaceControl;
import de.uka.ilkd.key.control.KeYEnvironment;
import de.uka.ilkd.key.proof.io.ProblemLoaderException;

import java.nio.file.Path;

public class Utils {
    public static KeYEnvironment<DefaultUserInterfaceControl> loadPO(Path file){
        KeYEnvironment<DefaultUserInterfaceControl> env;
        try{
            env = KeYEnvironment.load(file.toFile());
        }catch(ProblemLoaderException e ){
            throw new RuntimeException("Problem loading proof:", e);
        }
        return env;
    }
}
