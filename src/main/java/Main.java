import lambda.Lambda;
import lambda.term.Application;
import lambda.term.Lam;
import lambda.term.Term;
import lambda.term.Var;
import ski.SKI;

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

        // SKSK -> K
        System.out.println(SKI.eval(SKI.parseFromString("SKSK")));

        // SKxy -> y
        System.out.println(SKI.eval(SKI.parseFromString("SKxy")));

        // SIIa -> aa
        System.out.println(SKI.eval(SKI.parseFromString("SIIa")));

        // SII(SII) -> I(SII)(I(SII))
        //Infinite loop!
        //System.out.println(SKI.eval(SKI.parseFromString("SII(SII)")));
        // ((I$((S$I)$I))$(I$((S$I)$I))) -> (SII)(I(SII))

        // (S(Ka)(SII))b -> a(bb)
        System.out.println(SKI.eval(SKI.parseFromString("(S(Ka)(SII))b")));

        // b = s(Ka)(SII)
        // SIIb = SII(S(Ka)(SII)) -> bb => a(bb) => a(a(bb))
        //Infinite loop!
        //System.out.println(SKI.eval(SKI.parseFromString("SII(S(Ka)(SII))")));

        // S(K(SI))K
        // S(K(SI))Kab -> ba
        System.out.println(SKI.eval(SKI.parseFromString("S(K(SI))Kab")));

        /*
        Boolean
        T = K, Kxy = x
        F = SK, SKxy = Ky(xy) = y
         */
        System.out.println(SKI.eval(SKI.parseFromString("Kxy")));

        System.out.println(SKI.eval(SKI.parseFromString("SKxy")));

        // NOT = (F)(T) = (SK)(K)
        // (T)NOT = K(SK)(K) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("K(SK)(K)")));
        // (F)NOT = SK(SK)(K) -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("SK(SK)(K)")));

        // OR = T = K
        // T OR T = KKK -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("KKK")));

        // T OR F = KK(SK) -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("KK(SK)")));

        // F OR T = (SK)KK -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("(SK)KK")));

        // F OR F = (SK)K(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)K(SK)")));

        // AND = F = SK
        // T T AND = KK(SK) -> K = T
        System.out.println(SKI.eval(SKI.parseFromString("KK(SK)")));

        // T F AND = K(SK)(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("K(SK)(SK)")));

        // F T AND = (SK)K(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)K(SK)")));

        // F F AND = (SK)(SK)(SK) -> SK = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)(SK)(SK)")));

        // Examples of reduction
        // SKI(KIS) -> I
        System.out.println(SKI.eval(SKI.parseFromString("SKI(KIS)")));

        // KS(I(SKSI)) -> S
        System.out.println(SKI.eval(SKI.parseFromString("KS(I(SKSI))")));

        // SKIK -> KK(IK)
        System.out.println(SKI.eval(SKI.parseFromString("SKIK")));

        //yx
        System.out.println(SKI.eval(SKI.parseFromString("(S(K(SI))(S(KK)I)xy)")));

        System.out.println("*** Lambda ***");
        System.out.println(Lambda.eval(new Var('x')));
        System.out.println(Lambda.eval(new Lam(new Var('x'), new Application(new Var('y'), new Var('x')))));
        System.out.println(Lambda.eval(new Lam(new Var('x'), new Lam(new Var('y'), new Application(new Var('y'), new Var('x'))))));

        System.out.println("FromSKI");
        System.out.println(Lambda.fromSKI(SKI.parseFromString("x")));
        System.out.println(Lambda.fromSKI(SKI.parseFromString("S")));
        System.out.println(Lambda.fromSKI(SKI.parseFromString("K")));
        System.out.println(Lambda.fromSKI(SKI.parseFromString("I")));
        System.out.println(Lambda.fromSKI(SKI.parseFromString("zI")));

        System.out.println(SKI.fromLambda(new Lam(new Var('x'), new Var('x'))));
        System.out.println(SKI.fromLambda(new Lam(new Var('x'), new Var('y'))));

        // (Lx.yx)u -> yu
        System.out.println(Lambda.evalExpression(new Application(new Lam(new Var('x'), new Application(new Var('y'), new Var('x'))), new Var('u'))));

        // (Lx.(Ly.yx)z)v -> (Ly.yv)z -> zv
        System.out.println(Lambda.evalExpression(new Application(new Lam(new Var('x'), new Application(new Lam(new Var('y'), new Application(new Var('y'), new Var('x'))), new Var('z'))), new Var('v'))));

        // (Lx.x(xy))N -> N(Ny)
        System.out.println(Lambda.evalExpression(new Application(new Lam(new Var('x'), new Application(new Var('x'), new Application(new Var('x'), new Var('y')))), new Var('N'))));

        // (Lx.x(Lx.x))(Lx.x) => ((Lx.x):(La.a))    ???  Lx.x
        Term exp = Lambda.evalExpression(new Application(new Lam(new Var('x'), new Application(new Var('x'), new Lam(new Var('x'), new Var('x')))), new Lam(new Var('x'), new Var('x'))));
        System.out.println(exp);
        // ((Lx.x):(La.a)) => La.a
        System.out.println(Lambda.evalExpression(exp));

        // (Lx.(x:(Lx.x))):Lx.x
        System.out.println(SKI.fromLambda(new Application(new Lam(new Var('x'), new Application(new Var('x'), new Lam(new Var('x'), new Var('x')))), new Lam(new Var('x'), new Var('x')))));
        Term ttt = Lambda.fromSKI(SKI.parseFromString("(((S$I)$I)$I)"));
        System.out.println(ttt);
        System.out.println(Lambda.evalExpression(ttt));

        System.out.println(SKI.eval(SKI.parseFromString("((S$I)$I)$I$t")));
        System.out.println(Lambda.evalExpression(new Application(ttt, new Var('t'))));
        System.out.println(Lambda.evalExpression(Lambda.eval(new Application(ttt, new Var('t')))));


        // (SK)(SK)(SK) => SK
        System.out.println(SKI.eval(SKI.parseFromString("(SK)(SK)(SK)")));
        Term l = Lambda.fromSKI(SKI.eval(SKI.parseFromString("(SK)(SK)(SK)")));
        System.out.println(l);
        Term L = Lambda.evalExpression(l);
        System.out.println(L);
        ski.term.Term ski = SKI.fromLambda(L);
        System.out.println(ski);
        System.out.println(SKI.eval(ski));
    }

    //SKI.fromLambda
    //Var.binder
    //Lambda: subst, rename
}
