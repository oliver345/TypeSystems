import org.apache.commons.lang3.tuple.Pair;
import typed.ski.deep.evaluator.Evaluator;
import typed.ski.deep.lang.preterm.Preterm;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.lang.type.Ty;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.typechecker.TypeChecker;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        /*
        ShallowSKI ski = new ShallowSKI() {
            @Override
            public void run() {
                super.run();
            }
        };
        ski.run();*/

        //Preterm parseTree = Parser.createParseTree("(K:Bool->Bool->Bool):Bool->Bool->Bool");
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        // ==>
        //Preterm parseTree = Parser.createParseTree("(S K:Bool->Bool->Bool (K True):Bool->Bool False):Bool");
        //Preterm parseTree = Parser.createParseTree("(S K{Bool}{Bool} (K True):Bool->Bool False):Bool");

        //isZero
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K{Bool}{Bool} False)):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K{}{Bool->Bool} (K{Bool}{Bool} False)) ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool (Succ ZERO):Nat");
        Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K{}{Bool->Bool} (K{Bool}{Bool} False)) (Succ (Succ ZERO):Nat):Nat");


        //Preterm parseTree = Parser.createParseTree("K{Bool}{Str}");
        //Preterm parseTree = Parser.createParseTree("K{}{Str} True");
        //Preterm parseTree = Parser.createParseTree("K{}{Str} True xyz");
        //Preterm parseTree = Parser.createParseTree("K{Str}{Bool} xyz");
        //Preterm parseTree = Parser.createParseTree("K{Str}{Bool} xyz False");


        //Preterm parseTree = Parser.createParseTree("K{Bool}{Str} True xyz");
        //Preterm parseTree = Parser.createParseTree("K{Bool}{Str} True xyz");


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
