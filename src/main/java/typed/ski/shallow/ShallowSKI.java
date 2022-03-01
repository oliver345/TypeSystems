package typed.ski.shallow;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ShallowSKI {

    public static final int ZERO = 0;

    /*
    private final Function<Object, Function<Object, Object>> K = a -> (b -> a);

    private final Function<Object, Object> I = a -> a;
    */

    @FunctionalInterface
    private interface SType<A, B, C> {
        C apply(Function<A, Function<B, C>> f, Function<A, B> g, A a);
    }

    @FunctionalInterface
    private interface KType<A, B> extends Function<A, Function<B, A>> {
        @Override
        Function<B, A> apply(A a);
    }

    @FunctionalInterface
    private interface IType<A> extends Function<A, A> {
        @Override
        A apply(A a);
    }

    public final SType<Object, Object, Object> S = (f, g, a) -> f.apply(a).apply(g.apply(a));

    public final KType<Object, Object> K = a -> (b -> a);

    public final IType<Object> I = a -> a;


    /*public static class S implements SType<Object, Object, Object> {

        private final Object result;

        public S(Function<Object, Function<Object, Object>> f, Function<Object, Object> g, Object a) {
            result = apply(f, g, a);
        }

        @Override
        public Object apply(Function<Object, Function<Object, Object>> f, Function<Object, Object> g, Object a) {
            return f.apply(a).apply(g.apply(a));
        }

        @Override
        public String toString() {
            return result.toString();
        }
    }

    public static class K implements KType<Object, Object> {

        private final Object result;

        public K(Object a) {
            result = apply(a);
        }

        @Override
        public Function<Object, Object> apply(Object a) {
            return b -> a;
        }

        @Override
        public String toString() {
            return result.toString();
        }
    }

    public static class I implements IType<Object> {

        private final Object result;

        public I(Object a) {
            result = apply(a);
        }

        @Override
        public Object apply(Object a) {
            return a;
        }

        @Override
        public String toString() {
            return result.toString();
        }
    }*/

    public void run() {
        System.out.println(I.apply(true));
        System.out.println(K.apply(false).apply(true));  // ==> false
        System.out.println(S.apply(K, I, true));  // ==> true
        System.out.println(S.apply(K, K.apply(true), false)); // S K (K true) false ==> false

        System.out.println(ITE(isZero.test(succ.apply(ZERO)), ZERO, succ.compose(succ).apply(ZERO)));  //2
        System.out.println(ITE(isZero(succ(ZERO)), ZERO, succ(succ(ZERO))));  //2

        System.out.println(S.apply(K, succ, ZERO));

        System.out.println(S.apply(K, succ, succ.compose(succ).apply(ZERO)));

        //ystem.out.println(rec(true, K.apply(K.apply(false)), succ(ZERO)));
        //System.out.println(rec(true, K.compose(K), succ(ZERO)));
    }

    public <A> A ITE(boolean condition, A a, A b) {
        return condition ? a : b;
    }

    public final Function<Object, Object> succ = i -> (int) i + 1;

    public int succ(int i) {
        return i + 1;
    }

    public final Predicate<Object> isZero = i -> (int) i == ZERO;

    public boolean isZero(int i) {
        return ZERO == i;
    }

    /*@FunctionalInterface
    private interface RecType<A> {
        A apply(A z, Function<Integer, Function<A, A>> s, int x);
    }

    public RecType<Object> rec = (z, s, x) -> x == ZERO ? z : s.apply(--x).apply(rec.apply(z, s, x));*/

    /*public <A> A rec(A z, Function<Integer, Function<A, A>> s, int x) {
        return x == ZERO ? z : s.apply(--x).apply(rec(z, s, x));
    }*/

    public Object rec(Object z, Function<Integer, Function<Object, Object>> s, int x) {
        return x == ZERO ? z : s.apply(--x).apply(rec(z, s, x));
    }
}
