package typed.ski.deep;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import typed.ski.deep.lang.term.Term;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class SKITest {

    static Method skiExecuteCodeLine;

    @BeforeAll
    static void init() throws NoSuchMethodException {
        skiExecuteCodeLine = SKI.class.getDeclaredMethod("executeCodeLine", String.class, Map.class, Map.class);
        skiExecuteCodeLine.setAccessible(true);
    }

    @ParameterizedTest
    @MethodSource("terms")
    void testTerms(String input, String expectedResult) throws InvocationTargetException, IllegalAccessException {
        Term result = ((Optional<Term>) skiExecuteCodeLine.invoke(null, input, null, null)).orElseThrow();
        assertThat(result.toString()).isEqualTo(expectedResult);
    }

    static Stream<Arguments> terms() {
        return Stream.of(
                Arguments.of("True", "True"),
                Arguments.of("False", "False"),
                Arguments.of("ZERO", "ZERO"),
                Arguments.of("0", "ZERO"),
                Arguments.of("Succ 0", "(Succ ZERO)"),
                Arguments.of("I True", "True"),
                Arguments.of("I{Nat} ZERO", "ZERO"),
                Arguments.of("K False True", "False"),
                Arguments.of("K{Str}{Str} hello world", "\"hello\""),
                Arguments.of("S K I True", "True"),
                Arguments.of("S{}{}{} K I False", "False"),
                Arguments.of("HelloWorld", "\"HelloWorld\""),
                Arguments.of("[]", "[]"),
                Arguments.of("[1,0]", "((Cons (Succ ZERO)) ((Cons ZERO) []))"),
                Arguments.of("Cons 3 [2,1]", "((Cons (Succ (Succ (Succ ZERO)))) ((Cons (Succ (Succ ZERO))) ((Cons (Succ ZERO)) [])))"),
                Arguments.of("ITE True [1] [2]", "((Cons (Succ ZERO)) [])"),
                Arguments.of("ITE False [1] [2]", "((Cons (Succ (Succ ZERO))) [])"),
                Arguments.of("Rec ZERO K 5", "(Succ (Succ (Succ (Succ ZERO))))"),
                Arguments.of("RecList ZERO ((S (K S) K) (K (K Succ)) I) [1,0]", "(Succ (Succ ZERO))")
        );
    }
}