package key4cats;

import java.util.List;


class Proof implements KeYGen{
    String include;
    String javaSource;
    Problem problem;
    Proof(String include, String javaSource, Problem problem){
        this.include = include;
        this.javaSource = javaSource;
        this.problem = problem;
    }
    @Override
    public String toKeY() {
        return String.format(
                "\\include %s;\n" +
                "\\javaSource \"%s\";\n" +
                "%s\n",
                this.include,
                this.javaSource,
                this.problem.toKeY()
        );
    }
}

record Problem(List<AssumeCAT> assumptionCATs, CATof target) implements KeYGen{

    @Override
    public String toKeY() {
        return String.format(
                "\\problem{\n%s\n==>\n%s\n}",
                Utils.listToKeY(assumptionCATs), target.toKeY());
    }
}

record AssumeCAT(CATof catof) implements KeYGen{

    @Override
    public String toKeY() {
        return String.format("assumeCATof(%s,%s)", catof.id().toKeY(), catof.cat().toKeY());
    }
}

record CATof(Identifier id, CAT cat) implements KeYGen{
    public String toKeY() {
        return String.format("CATof(%s)", Utils.listToKeY(List.of(id,cat)));
    }
}

