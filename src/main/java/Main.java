import java.util.HashMap;

import typed.ski.deep.SKI;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.typechecker.TypeChecker;
import typed.ski.shallow.ShallowSKI;

public class Main {

    private static void convertDeepToShallow(String input) {
        try {
            Term term = TypeChecker.createWellTypedTree(Parser.createParseTree(input, new HashMap<>()));
            Object result = ShallowSKI.termToShallow(term);
            System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        convertDeepToShallow("K False ZERO");
        convertDeepToShallow("K I False True");
        convertDeepToShallow("S K I ZERO");
        convertDeepToShallow("S (K S) K K Succ ZERO False");
        convertDeepToShallow("Cons 8 [5, 10, 7]");

        SKI.executeCode("B=S (K S) K;" +
                "B K Succ ZERO False");

        //LE=Rec (K True) (K (S (K (Rec False)) (S (K K))));
        /*ShallowSKI.<Function<Integer, Boolean>>rec()  //Rec
                .apply(ShallowSKI.<Boolean, Integer>k().apply(true))  //(K True)
                .apply(ShallowSKI.<Function<Function<Integer, Boolean>, Function<Integer, Boolean>>, Integer>k().apply(ShallowSKI.<Function<Integer, Boolean>, Function<Integer, Function<Boolean, Boolean>>, Function<Integer, Boolean>>s()  //(K (S
                        .apply(ShallowSKI.<Function<Function<Integer, Function<Boolean, Boolean>>, Function<Integer, Boolean>>, Function<Integer, Boolean>>k().apply(ShallowSKI.<Boolean>rec().apply(false)))  //(K (Rec False))
                        .apply(ShallowSKI.<Integer, Boolean, Function<Boolean, Boolean>>s().apply(ShallowSKI.<Function<Boolean, Function<Boolean, Boolean>>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k())))))  //(S (K K))  ))
                .apply(2)
                .apply(5);*/

        /*Function<Integer, Function<Integer, Boolean>> shallowLE = ShallowSKI.<Function<Integer, Boolean>>rec().apply(ShallowSKI.<Boolean, Integer>k().apply(true)).apply(ShallowSKI.<Function<Function<Integer, Boolean>, Function<Integer, Boolean>>, Integer>k().apply(ShallowSKI.<Function<Integer, Boolean>, Function<Integer, Function<Boolean, Boolean>>, Function<Integer, Boolean>>s().apply(ShallowSKI.<Function<Function<Integer, Function<Boolean, Boolean>>, Function<Integer, Boolean>>, Function<Integer, Boolean>>k().apply(ShallowSKI.<Boolean>rec().apply(false))).apply(ShallowSKI.<Integer, Boolean, Function<Boolean, Boolean>>s().apply(ShallowSKI.<Function<Boolean, Function<Boolean, Boolean>>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k())))));
        System.out.println(shallowLE.apply(35).apply(25));
        //System.out.println(ShallowSKI.LE.apply(2).apply(5));
        //5-6 mins
        System.out.println(shallowLE.apply(25).apply(35));*/

        SKI.executeCode("L=[ZERO,Succ ZERO];" +
                "S K I True;" +
                "isZero=Rec True (K (K False));" +
                "isZero ZERO;" +
                "B=isZero ZERO;" +
                "isZero (Succ ZERO);" +
                "ski=S K I;" +
                "ski False;" +
                "L;" +
                "B;" +
                "List1=[True,B,False];" +
                "List1");

        SKI.executeCode("" +
                "S K I True;" +
                "isZero=Rec True (K (K False));" +
                "isZero ZERO;" +
                "isZero (Succ ZERO);" +
                "ski=S K I;" +
                "ski False");

        /*TypeChecker.infer(Parser.createParseTree("K:Str->Bool->Str xy"))
                .ifPresent(pair -> System.out.println(pair.toString()));*/

        //Preterm pt = Parser.createParseTree("K (K (K True txt) (I False)) vvv");
        //Preterm pt = Parser.createParseTree("S K:Bool->Bool->Bool I True");
        //Preterm pt = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        //Preterm pt = Parser.createParseTree("(S K (K True) False):Bool");
        //Preterm pt = Parser.createParseTree("S{Bool}{Bool}{Bool} K I True");
        //                        Preterm pt = Parser.createParseTree("S K I True");
        //Preterm pt = Parser.createParseTree("K{}{Bool}:Str->Bool->Str");
        //Preterm pt = Parser.createParseTree("Succ ZERO");
        //Preterm pt = Parser.createParseTree("ITE False ZERO (Succ ZERO)");
        //Preterm pt = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool (Succ (Succ ZERO))");
        //Preterm pt = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool ZERO");
        //Preterm pt = Parser.createParseTree("Rec{Bool} True (K{}{Nat} (K{}{Bool} False)) ZERO");
        //Preterm pt = Parser.createParseTree("Rec{} True (K (K False)) ZERO");
        //Preterm pt = Parser.createParseTree("Rec True (K (K False)) ZERO");
        //         Term resultTerm = TypeChecker.createWellTypedTree(pt);
        //          System.out.println(Evaluator.eval(resultTerm));

        //Preterm parseTree = Parser.createParseTree("(K:Bool->Bool->Bool):Bool->Bool->Bool");
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool True");
        // ==>
        //Preterm parseTree = Parser.createParseTree("S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)");
        //Preterm parseTree = Parser.createParseTree("(S K{Bool}{Bool} (K True):Bool->Bool False):Bool");
        //Preterm parseTree = Parser.createParseTree("K{}{Bool}:Str->Bool->Str");
        //Clear unknown map!!!

        //isZero
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool ZERO");
        //           parseTree = Parser.createParseTree("Rec{Bool} True (K (K False):Bool->Bool):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K{Bool}{Bool} False)):Nat->Bool->Bool ZERO");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool (Succ ZERO):Nat");
        //Preterm parseTree = Parser.createParseTree("Rec{Bool} True (K{}{Bool->Bool} (K{Bool}{Bool} False)) (Succ (Succ ZERO):Nat):Nat");


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


        /*

        System.out.println("teszt");
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("((Succ ZERO):Nat):Nat", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(SKI.executeCodeLine("Rec True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool ZERO", Collections.emptyMap()));

        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("Rec True (K (K:Bool->Bool->Bool False)):Nat->Bool->Bool ZERO", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("K{}{Str} True", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("S{}{Bool}{Bool} K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("S{}{Bool}{Bool} K{Bool}{Bool} I:Bool->Bool (K:Bool->Bool->Bool True False)", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("(K{}{}):Bool->Bool->Bool", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("(I{}):Bool->Bool", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("I{}:Bool->Bool", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("S{Bool}{Bool}{Bool}:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("S{}{Bool}{}:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("S K I True", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("K (K False) ZERO False", null)).map(Pair::getLeft).orElseThrow()));

        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("[]", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("[ZERO]", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("[ZERO,Succ ZERO,Succ (Succ ZERO)]", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("[[],[]]", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("[ZERO]", null))));

        System.out.println("with infer");
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("[]", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("[ZERO]", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("[ZERO,Succ ZERO,Succ (Succ ZERO)]", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("[[],[]]", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("[ZERO]", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("Cons True [False,True,False,True,False,True,False,True,False,True,False,True,False,True,False,True]", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("Cons True (Cons False (Cons True [True,False,True,False,True]))", null)).map(Pair::getLeft).orElseThrow()));


        //System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("RecList", null)).map(Pair::getLeft).orElseThrow()));
        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("RecList{Nat}{Bool}", null)).map(Pair::getLeft).orElseThrow()));

        System.out.println(Evaluator.eval(TypeChecker.infer(Parser.createParseTree("Rec True (K (K False)):Nat->Bool->Bool ZERO", null)).map(Pair::getLeft).orElseThrow()));

        //S:(Bool->Bool->Bool)->(Bool->Bool)->Bool->Bool K:Bool->Bool->Bool I:Bool->Bool (K:Bool->Bool->Bool True False)

        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("Rec (K True) (K (S (K (Rec False))(S (K K)))) (Succ (Succ ZERO)) ZERO", null))));


        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("Cons True (Cons False [True])", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("Cons True []", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("[True]", null))));
        System.out.println(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree("Cons True (Cons False (Cons True [True,False,True,False,True]))", null))));

        System.out.println("---- ----");


        */

        // List items are not evaluated also!
        SKI.executeCode("[[ZERO,ZERO],[ZERO,ZERO]]");
        SKI.executeCode("[[ZERO],[ZERO, ZERO]]");

        SKI.executeCode("Cons ZERO []");
        SKI.executeCode("[Cons ZERO [],[ZERO,ZERO]]");

        SKI.executeCode("[[Succ ZERO],[ZERO, ZERO]]");
        SKI.executeCode("[[I True],[False, True]]");

        SKI.executeCode("" +
                "LE=Rec (K True) (K (S (K (Rec False)) (S (K K))));" +
                "B=S (K S) K;" +
                "C=S (S (K B) S) (K K);" +
                "Insert=S (B RecList (C Cons [])) (C (B S (B (B C) (B (B (B B)) (S (B S (B (B B) (B (B ITE) LE))) (C (B B (B B Cons)) Cons))))) Cons);" +
                "InsertionSort=RecList [] (S (K K) Insert);" +
                "InsertionSort [];" +
                "InsertionSort [ZERO];" +
                "InsertionSort [ZERO, ZERO];" +
                "InsertionSort [Succ ZERO];" +
                "InsertionSort [ZERO, Succ ZERO];" +
                "InsertionSort [Succ ZERO, ZERO];" +
                "InsertionSort [Succ (Succ ZERO), ZERO, Succ ZERO];" +
                "InsertionSort [Succ (Succ ZERO), ZERO, Succ (Succ (Succ (Succ ZERO))), Succ ZERO];" +
                "InsertionSort [Succ (Succ ZERO), ZERO, Succ ZERO, Succ (Succ (Succ (Succ ZERO))), ZERO, Succ ZERO];" +
                "InsertionSort [2, 0, 1, 4, 0, 1];" +
                "");
    }
}
