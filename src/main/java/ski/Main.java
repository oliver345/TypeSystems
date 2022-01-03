package ski;

public class Main {
    public static void main(String[] args) {

        // S -> S
        System.out.println(SKI.eval(SKI.parseFromString("S")));
        // K -> K
        System.out.println(SKI.eval(SKI.parseFromString("K")));
        // I -> I
        System.out.println(SKI.eval(SKI.parseFromString("I")));
        // x -> x
        System.out.println(SKI.eval(SKI.parseFromString("x")));

        // Ix -> x
        System.out.println(SKI.eval(SKI.parseFromString("Ix")));

        // Kxy -> x
        System.out.println(SKI.eval(SKI.parseFromString("Kxy")));

        // Sxyz -> xz(yz)
        System.out.println(SKI.eval(SKI.parseFromString("Sxyz")));

        // SKSK -> KK(SK)
        System.out.println(SKI.eval(SKI.parseFromString("SKSK")));
        // ((K$K)$(S$K)) -> K
        System.out.println(SKI.eval(SKI.parseFromString("((K$K)$(S$K))")));

        // SKxy -> Ky(xy)
        System.out.println(SKI.eval(SKI.parseFromString("SKxy")));
        // ((K$y)$(x$y)) -> y
        System.out.println(SKI.eval(SKI.parseFromString("((K$y)$(x$y))")));

        // SIIa -> Ia(Ia)
        System.out.println(SKI.eval(SKI.parseFromString("SIIa")));
        // ((I$a)$(I$a)) -> a(Ia)
        System.out.println(SKI.eval(SKI.parseFromString("((I$a)$(I$a))")));
        // (a$(I$a)) -> aa
        System.out.println(SKI.eval(SKI.parseFromString("(a$(I$a))")));

        // SII(SII) -> I(SII)(I(SII))
        System.out.println(SKI.eval(SKI.parseFromString("SII(SII)")));
        // ((I$((S$I)$I))$(I$((S$I)$I))) -> (SII)(I(SII))
        System.out.println(SKI.eval(SKI.parseFromString("((I$((S$I)$I))$(I$((S$I)$I)))")));
        // (((S$I)$I)$(I$((S$I)$I))) -> ((I$(I$((S$I)$I)))$(I$(I$((S$I)$I))))
        /*System.out.println(SKI.eval(SKI.parseFromString("(((S$I)$I)$(I$((S$I)$I)))")));
        System.out.println(SKI.eval(SKI.parseFromString("((I$(I$((S$I)$I)))$(I$(I$((S$I)$I))))")));
        System.out.println(SKI.eval(SKI.parseFromString("((I$((S$I)$I))$(I$(I$((S$I)$I))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(((S$I)$I)$(I$(I$((S$I)$I))))")));*/

        // (S(Ka)(SII))b -> Kab(SIIb)
        System.out.println(SKI.eval(SKI.parseFromString("(S(Ka)(SII))b")));
        // (((K$a)$b)$(((S$I)$I)$b)) -> a(SIIb)
        System.out.println(SKI.eval(SKI.parseFromString("(((K$a)$b)$(((S$I)$I)$b))")));
        // (a$(((S$I)$I)$b)) -> a(bb)
        System.out.println(SKI.eval(SKI.parseFromString("(a$(((S$I)$I)$b))")));
        System.out.println(SKI.eval(SKI.parseFromString("(a$((I$b)$(I$b)))")));
        System.out.println(SKI.eval(SKI.parseFromString("(a$(b$(I$b)))")));

        // b = s(Ka)(SII)
        // SIIb = SII(S(Ka)(SII)) -> bb => a(bb) => a(a(bb))
        System.out.println(SKI.eval(SKI.parseFromString("SII(S(Ka)(SII))")));
        /*System.out.println(SKI.eval(SKI.parseFromString("((I$((S$(K$a))$((S$I)$I)))$(I$((S$(K$a))$((S$I)$I))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(((S$(K$a))$((S$I)$I))$(I$((S$(K$a))$((S$I)$I))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(((K$a)$(I$((S$(K$a))$((S$I)$I))))$(((S$I)$I)$(I$((S$(K$a))$((S$I)$I)))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(a$(((S$I)$I)$(I$((S$(K$a))$((S$I)$I)))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(a$((I$(I$((S$(K$a))$((S$I)$I))))$(I$(I$((S$(K$a))$((S$I)$I))))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(a$((I$((S$(K$a))$((S$I)$I)))$(I$(I$((S$(K$a))$((S$I)$I))))))")));
        System.out.println(SKI.eval(SKI.parseFromString("(a$(((S$(K$a))$((S$I)$I))$(I$(I$((S$(K$a))$((S$I)$I))))))"))); ==> (a$(((S$(K$a))$((S$I)$I))$(I$(I$((S$(K$a))$((S$I)$I)))))) */

        // S(K(SI))K
        // S(K(SI))Kab -> K(SI)a(Ka)b
        System.out.println(SKI.eval(SKI.parseFromString("S(K(SI))Kab")));
        // ((((K$(S$I))$a)$(K$a))$b) -> SI(Ka)b
        System.out.println(SKI.eval(SKI.parseFromString("((((K$(S$I))$a)$(K$a))$b)")));
        // (((S$I)$(K$a))$b) -> Ib(Kab)
        System.out.println(SKI.eval(SKI.parseFromString("(((S$I)$(K$a))$b)")));
        // ((I$b)$((K$a)$b)) -> b(Kab)
        System.out.println(SKI.eval(SKI.parseFromString("((I$b)$((K$a)$b))")));
        // (b$((K$a)$b)) -> ba
        System.out.println(SKI.eval(SKI.parseFromString("(b$((K$a)$b))")));

        /*
        Boolean
        T = K, Kxy = x
        F = SK, SKxy = Ky(xy) = y
         */
        System.out.println(SKI.eval(SKI.parseFromString("Kxy")));

        System.out.println(SKI.eval(SKI.parseFromString("SKxy")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$y)$(x$y))")));

        // NOT = (F)(T) = (SK)(K)

        // (T)NOT = K(SK)(K) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("K(SK)(K)")));
        // (F)NOT = SK(SK)(K) -> KK((SK)k)
        System.out.println(SKI.eval(SKI.parseFromString("SK(SK)(K)")));
        // ((K$K)$((S$K)$K)) -> K
        System.out.println(SKI.eval(SKI.parseFromString("((K$K)$((S$K)$K))")));

        // OR = T = K

        // T OR T = KKK -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("KKK")));

        // T OR F = KK(SK) -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("KK(SK)")));

        // F OR T = (SK)KK -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("(SK)KK")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$K)$(K$K))")));

        // F OR F = (SK)K(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)K(SK)")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$(S$K))$(K$(S$K)))")));

        // AND = F = SK

        // T T AND = KK(SK) -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("KK(SK)")));

        // T F AND = K(SK)(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("K(SK)(SK)")));

        // F T AND = (SK)K(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)K(SK)")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$(S$K))$(K$(S$K)))")));

        // F F AND = (SK)(SK)(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)(SK)(SK)")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$(S$K))$((S$K)$(S$K)))")));

        // Examples of reduction
        // SKI(KIS) => ... => I
        System.out.println(SKI.eval(SKI.parseFromString("SKI(KIS)")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$((K$I)$S))$(I$((K$I)$S)))")));
        System.out.println(SKI.eval(SKI.parseFromString("((K$I)$S)")));

        // KS(I(SKSI)) => .. => S
        System.out.println(SKI.eval(SKI.parseFromString("KS(I(SKSI))")));

        // SKIK -> KK(IK)
        System.out.println(SKI.eval(SKI.parseFromString("SKIK")));
        // ((K$K)$(I$K)) -> K
        System.out.println(SKI.eval(SKI.parseFromString("((K$K)$(I$K))")));
    }
}
