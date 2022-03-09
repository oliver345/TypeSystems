package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

public class Literal implements Term {

    private final String name;

    public Literal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {}
}
