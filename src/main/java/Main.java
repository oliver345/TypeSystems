import org.apache.commons.lang3.tuple.Pair;
import typed.ski.deep.evaluator.Evaluator;
import typed.ski.deep.lang.preterm.Preterm;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.lang.type.PreType;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.typechecker.TypeChecker;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        /*TypeChecker.infer(Parser.createParseTree("K:Str->Bool->Str xy"))
                .ifPresent(pair -> System.out.println(pair.toString()));*/

        //Preterm pt = Parser.createParseTree("K (K (K True txt) (I False)) vvv");
        //Preterm pt = Parser.createParseTree("S K:Bool->Bool->Bool I True");
        //Preterm pt = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        //Preterm pt = Parser.createParseTree("(S K (K True) False):Bool");
        //Preterm pt = Parser.createParseTree("S{Bool}{Bool}{Bool} K I True");
        Preterm pt = Parser.createParseTree("S K I True");
        //Preterm pt = Parser.createParseTree("K{}{Bool}:Str->Bool->Str");
        //Preterm pt = Parser.createParseTree("Succ ZERO");
        //Preterm pt = Parser.createParseTree("ITE False ZERO (Succ ZERO)");
        //Preterm pt = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool (Succ (Succ ZERO))");
        //Preterm pt = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool ZERO");
        //Preterm pt = Parser.createParseTree("Rec{Bool} True (K{}{Nat} (K{}{Bool} False)) ZERO");
        //Preterm pt = Parser.createParseTree("Rec{} True (K (K False)) ZERO");
        //Preterm pt = Parser.createParseTree("Rec True (K (K False)) ZERO");
        Term resultTerm = TypeChecker.createWellTypedTree(pt);
        System.out.println(Evaluator.eval(resultTerm));

        //Preterm parseTree = Parser.createParseTree("(K:Bool->Bool->Bool):Bool->Bool->Bool");
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        // ==>
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)");
        //Preterm parseTree = Parser.createParseTree("(S K{Bool}{Bool} (K True):Bool->Bool False):Bool");
        //Preterm parseTree = Parser.createParseTree("K{}{Bool}:Str->Bool->Str");
        //Clear unknown map!!!

        //isZero
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool ZERO");
        Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K{Bool}{Bool} False)):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool (Succ ZERO):Nat");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K{}{Bool->Bool} (K{Bool}{Bool} False)) (Succ (Succ ZERO)"
               // + ":Nat):Nat");


        //Preterm parseTree = Parser.createParseTree("(S K:Bool->Bool->Bool (K True):Bool->Bool False):Bool");

        //Preterm parseTree = Parser.createParseTree("K{Bool}{Str}");
        //Preterm parseTree = Parser.createParseTree("K{}{Str} True");
        //Preterm parseTree = Parser.createParseTree("K{}{Str} True xyz");
        //Preterm parseTree = Parser.createParseTree("K{Str}{Bool} xyz");
        //Preterm parseTree = Parser.createParseTree("K{Str}{Bool} xyz False");

        //----- Preterm parseTree = Parser.createParseTree("(Succ (Succ (Rec{Nat} ZERO (K{Nat->Nat}{Nat} Succ:Nat->Nat) ZERO):Nat):Nat):Nat");


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


        Optional<Pair<Term, PreType>> wttOpt = TypeChecker.infer(parseTree);
        System.out.println(parseTree);
        System.out.println(wttOpt);
        wttOpt.ifPresent(wtt -> System.out.println(Evaluator.eval(wtt.getLeft())));
    }
}
