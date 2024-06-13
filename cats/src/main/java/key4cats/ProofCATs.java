package key4cats;

import java.util.List;


class Proof implements KeYGen{
    List<String> include;
    String javaSource;
    Problem problem;
    Proof(List<String> include, String javaSource, Problem problem){
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
        String.join(", ", this.include),
                this.javaSource,
                this.problem.toKeY()
        );
    }
}

record Problem(List<CATof> assumptionCATs, CATof target) implements KeYGen{

    @Override
    public String toKeY() {
        return String.format(
                "\\problem{\n%s\n==>\n%s\n}",
                Utils.listToKeY(assumptionCATs), target.toKeY());
    }
}

record CATof(Identifier id, CAT cat) implements KeYGen{
    public String toKeY() {
        return String.format("CATof(%s)", Utils.listToKeY(List.of(id,cat)));
    }
}

