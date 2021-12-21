package ski;

import ski.term.*;

public class Main {
    public static void main(String[] args) {

        System.out.println(SKI.eval(new S()));
        System.out.println(SKI.eval(new K()));
        System.out.println(SKI.eval(new I()));

        Application expression = new Application(new Application(new Application(new S(), new K()), new S()), new K());
        Term result = SKI.eval(expression);
        System.out.println(result);

        expression = new Application(new Application(new K(), new K()), new Application(new S(), new K()));
        System.out.println(SKI.eval(expression));

        //Boolean - NOT
        //not True = False = SK
        expression = new Application(new Application(new K(), new Application(new S(), new K())), new K());
        System.out.println(SKI.eval(expression));

        //not False = True = K
        expression = new Application(new Application(new Application(new S(), new K()), new Application(new S(), new K())), new K());
        System.out.println(SKI.eval(expression));
        expression = new Application(new Application(new K(), new K()), new Application(new Application(new S(), new K()), new K()));
        System.out.println(SKI.eval(expression));

        //----------------------------------------------------------

        System.out.println("---------------------------");
        /*SKI.parseFromString("((K$K)$(S$K))");
        SKI.parseFromString("((K$K)$((S$K)$K))");

        SKI.parseFromString("KK(SK)");
        SKI.parseFromString("KK((SK)K)");*/
        System.out.println("---------------------------");
        //System.out.println(SKI.parseFromString("S((KI)S)"));
        //System.out.println(SKI.parseFromString("S$((K$I)$S)"));

        //S-nek egy parametere van, megall a vegrehajtas
        System.out.println(SKI.eval(SKI.parseFromString("S((KI)S)")));


        System.out.println(SKI.eval(SKI.parseFromString("SK(SK)K")));
        //((K$K)$((S$K)$K))
        System.out.println(SKI.eval(SKI.parseFromString("((K$K)$((S$K)$K))")));
        //K
        System.out.println(SKI.eval(SKI.parseFromString("K")));
        //K

        //(F)(T)AND = F(T)(F) = F
        System.out.println(SKI.eval(SKI.parseFromString("(SK)K(SK)")));
        //((K$(S$K))$(K$(S$K)))
        System.out.println(SKI.eval(SKI.parseFromString("((K$(S$K))$(K$(S$K)))")));
        //(S$K) ==> SK ==> F

        //(T)(T)AND = T(T)(F) = T
        System.out.println(SKI.eval(SKI.parseFromString("K(K)(SK")));

        System.out.println("SI(SK) -> " + SKI.eval(SKI.parseFromString("SI(SK)")));
        System.out.println("SI(SK)I -> " + SKI.eval(SKI.parseFromString("SI(SK)I")));
        System.out.println("Expressions");
        System.out.println(SKI.eval(SKI.parseFromString("SIIS")));
        System.out.println(SKI.eval(SKI.parseFromString("((I$S)$(I$S))")));
        System.out.println(SKI.eval(SKI.parseFromString("(S$(I$S))")));


    }
}
