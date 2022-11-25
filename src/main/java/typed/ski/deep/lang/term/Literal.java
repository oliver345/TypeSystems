package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

public class Literal implements Term {

    private final String text;

    public Literal(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "\"" + text + "\"";
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {}
}
