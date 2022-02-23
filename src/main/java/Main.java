import org.apache.commons.lang3.tuple.Pair;
import typed.ski.evaluator.Evaluator;
import typed.ski.lang.preterm.Preterm;
import typed.ski.lang.term.Term;
import typed.ski.lang.type.Ty;
import typed.ski.parser.Parser;
import typed.ski.typechecker.TypeChecker;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        //Preterm parseTree = Parser.createParseTree("(K:Bool->Bool->Bool):Bool->Bool->Bool");
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        // ==>
        Preterm parseTree = Parser.createParseTree("(S K:Bool->Bool->Bool (K True):Bool->Bool False):Bool");


        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)");
        //Preterm parseTree = Parser.createParseTree("(S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool) K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)");
        //Preterm parseTree = Parser.createParseTree("ITE:Bool->Bool->Bool->Bool (K:Bool->Str->Bool True Text) False True");
        //Preterm parseTree = Parser.createParseTree("ITE:Bool->Bool->Bool->Bool (K:Bool->Str->Bool ((S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False))) Text) False True");

        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool->Bool->Bool K:(Bool->Bool)->Bool->(Bool->Bool) I:Bool->Bool True");

        //Preterm parseTree = Parser.createParseTree("(K True):Bool->Bool False");
        //Preterm parseTree = Parser.createParseTree("I:Bool->Bool True");
        //Preterm parseTree = Parser.createParseTree("True:Bool");
        Optional<Pair<Term, Ty>> wttOpt = TypeChecker.infer(parseTree);
        System.out.println(parseTree);
        System.out.println(wttOpt);
        wttOpt.ifPresent(wtt -> System.out.println(Evaluator.eval(wtt.getLeft())));
    }
}
