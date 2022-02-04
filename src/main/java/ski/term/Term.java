package ski.term;

import ski.SKI;

public abstract class Term {

    public Term add(Term term) {
        return SKI.add(this, term);
    }
}
