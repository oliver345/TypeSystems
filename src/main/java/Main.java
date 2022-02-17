import typed.ski.evaluator.Evaluator;
import typed.ski.lang.preterm.Preterm;
import typed.ski.parser.Parser;
import typed.ski.typechecker.TypeChecker;
import typed.ski.typechecker.maybe.Maybe;
import typed.ski.typechecker.maybe.WellTypedTree;

public class Main {
    public static void main(String[] args) {
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)");
        //Preterm parseTree = Parser.createParseTree("(S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool) K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)");
        //Preterm parseTree = Parser.createParseTree("ITE:Bool->Bool->Bool->Bool (K:Bool->Str->Bool True Text) False True");
        Preterm parseTree = Parser.createParseTree("ITE:Bool->Bool->Bool->Bool (K:Bool->Str->Bool ((S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False))) Text) False True");

        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool->Bool->Bool K:(Bool->Bool)->Bool->(Bool->Bool) I:Bool->Bool True");
        Maybe maybeWtt = TypeChecker.infer(parseTree);
        System.out.println(parseTree);
        System.out.println(maybeWtt);
        typed.ski.lang.term.Term evaluated = Evaluator.eval(((WellTypedTree) maybeWtt).getWellTypedTree());
        System.out.println(evaluated);
    }
}
