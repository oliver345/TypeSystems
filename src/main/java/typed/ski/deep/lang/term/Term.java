package typed.ski.deep.lang.term;

public interface Term {

    void substituteUnknownTypes();

    default String toString(boolean prettyPrint) {
        return toString();
    }
}
