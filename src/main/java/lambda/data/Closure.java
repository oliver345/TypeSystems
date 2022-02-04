package lambda.data;

import lambda.data.term.Term;
import lambda.data.val.Val;

import java.util.Map;

public class Closure {

    private final char name;

    private final Map<Character, Val> env;

    private final Term term;

    public Closure(char name, Map<Character, Val> env, Term term) {
        this.name = name;
        this.env = env;
        this.term = term;
    }

    public char getName() {
        return name;
    }

    public Map<Character, Val> getEnv() {
        return env;
    }

    public Term getTerm() {
        return term;
    }
}

