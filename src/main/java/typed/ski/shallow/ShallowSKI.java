package typed.ski.shallow;

import typed.ski.deep.lang.term.*;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.typechecker.TypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public abstract class ShallowSKI {

    public static <A, B, C> Function<Function<A, Function<B, C>>, Function<Function<A, B>, Function<A, C>>> s() {
        return f -> (g -> (a -> f.apply(a).apply(g.apply(a))));
    }

    public static <A, B> Function<A, Function<B, A>> k() {
        return a -> (b-> a);
    }

    public static <A> Function<A, A> i() {
        return a -> a;
    }

    public static Function<Integer, Integer> succ() {
        return i -> i + 1;
    }

    public static Function<Integer, Boolean> isZero() {
        return i -> i == 0;
    }

    public static <A> Function<Boolean, Function<A, Function<A, A>>> ITE() {
        return b -> (x -> (y -> b ? x : y));
    }

    public static <A> Function<A, Function<Function<Integer, Function<A, A>>, Function<Integer, A>>> rec() {
        return z -> (s -> (i -> i == 0 ? z : s.apply(i - 1).apply(ShallowSKI.<A>rec().apply(z).apply(s).apply(i - 1))));

    }

    public static <A, B> Function<B, Function<Function<A, Function<List<A>, Function<B, B>>>, Function<List<A>, B>>> recList() {
        return z -> (c -> (l ->
                l.isEmpty() ? z : c.apply(l.get(0)).apply(l.subList(1, l.size())).apply(ShallowSKI.<A, B>recList().apply(z).apply(c).apply(l.subList(1, l.size())))));
    }

    public static <A> Function<A, Function<List<A>, List<A>>> cons() {
        return a -> (l -> {
            l.add(0, a);
            return l;
        });
    }

    public static Function<Integer, Function<Integer, Boolean>> LE =
            ShallowSKI.<Function<Integer, Boolean>>rec()  //Rec
                    .apply(ShallowSKI.<Boolean, Integer>k().apply(true))  //(K True)
                    .apply(ShallowSKI.<Function<Function<Integer, Boolean>, Function<Integer, Boolean>>, Integer>k().apply(ShallowSKI.<Function<Integer, Boolean>, Function<Integer, Function<Boolean, Boolean>>, Function<Integer, Boolean>>s()  //(K (S
                            .apply(ShallowSKI.<Function<Function<Integer, Function<Boolean, Boolean>>, Function<Integer, Boolean>>, Function<Integer, Boolean>>k().apply(ShallowSKI.<Boolean>rec().apply(false)))  //(K (Rec False))
                            .apply(ShallowSKI.<Integer, Boolean, Function<Boolean, Boolean>>s().apply(ShallowSKI.<Function<Boolean, Function<Boolean, Boolean>>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k())))));  //(S (K K))  ))

    public static <A, B, C> Function<Function<B, C>, Function<Function<A, B>, Function<A, C>>> nativeB() {
        return f -> (g -> (a -> f.apply(g.apply(a))));
    }

    public static <A, B, C> Function<Function<B, C>, Function<Function<A, B>, Function<A, C>>> b() {
        //S (K S) K
       return ShallowSKI.<Function<B, C>, Function<A, Function<B, C>>, Function<Function<A, B>, Function<A,C>>>s()
               .apply(ShallowSKI.<Function<Function<A, Function<B, C>>, Function<Function<A, B>, Function<A, C>>>, Function<B, C>>k().apply(ShallowSKI.<A, B, C>s()))
               .apply(ShallowSKI.<Function<B, C>, A>k());
    }

    public static <A, B, C> Function<Function<A, Function<B, C>>, Function<B, Function<A, C>>> nativeC() {
        return f -> (g -> (a -> f.apply(a).apply(g)));
    }

    public static <A, B, C> Function<Function<A, Function<B, C>>, Function<B, Function<A, C>>> c() {
        //S (S (K B) S) (K K)
        //1.
        //return ShallowSKI.<Function<A, A>, Function<B, Function<A, B>>, Function<B, Function<A, C>>>s().apply(ShallowSKI.<Function<A, A>, Function<Function<A, B>, Function<A, C>>, Function<Function<B, Function<A, B>>, Function<B, Function<A, C>>>>s().apply(ShallowSKI.<Function<Function<Function<A, B>, Function<A, C>>, Function<Function<B, Function<A, B>>, Function<B, Function<A, C>>>>, Function<A, A>>k().apply(ShallowSKI.nativeB())).apply(ShallowSKI.<A, B, C>s())).apply(ShallowSKI.<B, Function<A, B>>k().apply(ShallowSKI.<B, A>k()));

        //2.
        //return ShallowSKI.<Function<A, Function<B, C>>, Function<B, Function<A, B>>, Function<B, Function<A, C>>>s().apply(ShallowSKI.<Function<A, A>, Function<Function<A, B>, Function<A, C>>, Function<Function<B, Function<A, B>>, Function<B, Function<A, C>>>>s().apply(ShallowSKI.<Function<Function<Function<A, B>, Function<A, C>>, Function<Function<B, Function<A, B>>, Function<B, Function<A, C>>>>, Function<A, A>>k().apply(ShallowSKI.<B, Function<A, B>, Function<A, C>>nativeB())).apply(ShallowSKI.<A, B, C>s())).apply(ShallowSKI.<B, Function<A, B>>k().apply(ShallowSKI.<B, A>k()));
        return null;
    }


    //Evaluates Deep implemented Term by Shallow implementation
    public static Object termToShallow(Term term) {
        if (term instanceof Application app) {
            return ((Function) termToShallow(app.getLeftTerm())).apply(termToShallow(app.getRightTerm()));
        }
        if (term instanceof Cons) {
            return ShallowSKI.cons();
        }
        if (term instanceof EmptyList) {
            return new ArrayList<>();
        }
        if (term instanceof False) {
            return false;
        }if (term instanceof I) {
            return ShallowSKI.i();
        }
        if (term instanceof ITE) {
            return ShallowSKI.ITE();
        }
        if (term instanceof K) {
            return ShallowSKI.k();
        }
        if (term instanceof Literal) {
            return term.toString();
        }
        if (term instanceof Rec) {
            return ShallowSKI.rec();
        }
        if (term instanceof RecList) {
            return ShallowSKI.recList();
        }
        if (term instanceof S) {
            return ShallowSKI.s();
        }
        if (term instanceof Succ) {
            return ShallowSKI.succ();
        }
        if (term instanceof True) {
            return true;
        }
        if (term instanceof ZERO) {
            return 0;
        }
        throw new IllegalArgumentException();
    }

    public static void stringToShallow(String input) {
        try {
            Term term = TypeChecker.createWellTypedTree(Parser.createParseTree(input, new HashMap<>()));
            System.out.println(ShallowSKI.termToShallow(term));
        }
        catch (Exception exception) {
            System.out.println("Something went wrong for input: " + input);
            exception.printStackTrace();
        }
    }
}
