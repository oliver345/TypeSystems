package typed.ski.shallow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShallowSKITest {

    @Test
    void s() {
        assertEquals("Hello World",
                ShallowSKI.s().apply(ShallowSKI.k()).apply(ShallowSKI.i()).apply("Hello World"));
    }

    @Test
    void k() {
        Assertions.assertInstanceOf(Function.class, ShallowSKI.<Boolean, Boolean>k().apply(true));
        Assertions.assertTrue(ShallowSKI.<Boolean, Boolean>k().apply(true).apply(false));
    }

    @Test
    void i() {
        assertEquals(5, ShallowSKI.i().apply(5));
    }

    @Test
    void succ() {
        assertEquals(1, ShallowSKI.succ().apply(0));
        assertEquals(2, ShallowSKI.succ().apply(1));
    }

    @Test
    void isZero() {
        Assertions.assertTrue(ShallowSKI.isZero().apply(0));
        Assertions.assertFalse(ShallowSKI.isZero().apply(5));
    }

    @Test
    void ITE() {
        assertEquals("Hello", ShallowSKI.ITE().apply(true).apply("Hello").apply("world"));
        assertEquals("world", ShallowSKI.ITE().apply(false).apply("Hello").apply("world"));
    }

    @Test
    void rec() {
        //Implements isZero
        assertEquals(true,
                ShallowSKI.<Boolean>rec().apply(true).apply(ShallowSKI.<Function<Boolean, Boolean>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k().apply(false))).apply(0));
        assertEquals(false,
                ShallowSKI.<Boolean>rec().apply(true).apply(ShallowSKI.<Function<Boolean, Boolean>, Integer>k().apply(ShallowSKI.<Boolean, Boolean>k().apply(false))).apply(5));
    }

    @Test
    void b() {
        //B Succ I ZERO ==> 1
        assertEquals(1, ShallowSKI.<Integer, Integer, Integer>nativeB().apply(ShallowSKI.<Integer, Integer>succ()).apply(ShallowSKI.<Integer>i()).apply(0));
        assertEquals(1, ShallowSKI.<Integer, Integer, Integer>b().apply(ShallowSKI.<Integer, Integer>succ()).apply(ShallowSKI.<Integer>i()).apply(0));
    }

    @Test
    void c() {
        //C I 0 Succ ==> 1
        assertEquals(1, ShallowSKI.<Function<Integer, Integer>, Integer, Integer>nativeC().apply(ShallowSKI.<Function<Integer, Integer>>i()).apply(0).apply(ShallowSKI.succ()));
    }

    @Test
    void LE() {
        assertThat(ShallowSKI.LE.apply(35).apply(25)).isFalse();
    }
}
