package untyped.ski.term;

import untyped.ski.SKI;

public abstract class Term {

    public Term add(Term term) {
        return SKI.add(this, term);
    }
}
