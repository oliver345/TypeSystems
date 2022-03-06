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
}
