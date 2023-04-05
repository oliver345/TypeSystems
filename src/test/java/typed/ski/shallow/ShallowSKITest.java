package typed.ski.shallow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class ShallowSKITest {

    @Test
    void s() {
        Assertions.assertEquals(ShallowSKI.s().apply(ShallowSKI.k()).apply(ShallowSKI.i()).apply("Hello World"),
                "Hello World");
    }

    @Test
    void k() {
        Assertions.assertTrue(ShallowSKI.<Boolean, Boolean>k().apply(true) instanceof Function);
        Assertions.assertTrue(ShallowSKI.<Boolean, Boolean>k().apply(true).apply(false));
    }

    @Test
    void i() {
        Assertions.assertEquals(ShallowSKI.i().apply(5), 5);
    }

    @Test
    void succ() {
        Assertions.assertEquals(ShallowSKI.succ().apply(0), 1);
        Assertions.assertEquals(ShallowSKI.succ().apply(1), 2);
    }

    @Test
    void isZero() {
        Assertions.assertTrue(ShallowSKI.isZero().apply(0));
        Assertions.assertFalse(ShallowSKI.isZero().apply(5));
    }

    @Test
    void ITE() {
        Assertions.assertEquals(ShallowSKI.ITE().apply(true).apply("Hello").apply("world"), "Hello");
        Assertions.assertEquals(ShallowSKI.ITE().apply(false).apply("Hello").apply("world"), "world");
    }

    @Test
    void rec() {
        //Implements isZero
        Assertions.assertEquals(ShallowSKI.<Boolean>rec().apply(true).apply(ShallowSKI.<Function<Boolean, Boolean>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k().apply(false))).apply(0),
                true);
        Assertions.assertEquals(ShallowSKI.<Boolean>rec().apply(true).apply(ShallowSKI.<Function<Boolean, Boolean>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k().apply(false))).apply(5),
                false);
    }

    @Test
    void b() {
        //B Succ I ZERO ==> 1
        Assertions.assertEquals(ShallowSKI.<Integer, Integer, Integer>nativeB().apply(ShallowSKI.<Integer, Integer>succ()).apply(ShallowSKI.<Integer>i()).apply(0), 1);
        Assertions.assertEquals(ShallowSKI.<Integer, Integer, Integer>b().apply(ShallowSKI.<Integer, Integer>succ()).apply(ShallowSKI.<Integer>i()).apply(0), 1);
    }

    @Test
    void c() {
        //C I 0 Succ ==> 1
        System.out.println(ShallowSKI.<Function<Integer, Integer>, Integer, Integer>nativeC().apply(ShallowSKI.<Function<Integer, Integer>>i()).apply(0).apply(ShallowSKI.succ()));
    }

    @Test
    void LE() {
        System.out.println("Testing LE");
        System.out.println("35, 25");
        boolean result = ShallowSKI.LE.apply(35).apply(25);
        System.out.println(result);
    }

}
