package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

public class ZERO implements Term {

    @Override
    public String toString() {
        return "ZERO";
    }

    @Override
    public String toString(boolean prettyPrint) {
        return prettyPrint ? "0" : toString();
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {}
}
