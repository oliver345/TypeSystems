package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

public interface Term {
    void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes);
}
