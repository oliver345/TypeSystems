package typed.ski.shallow;

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
}
