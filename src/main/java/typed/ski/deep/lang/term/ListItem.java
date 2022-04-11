package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

public class ListItem implements Term {

    private final Term head;

    private final ListItem tail;

    public ListItem() {
        this(null, null);
    }

    public ListItem(Term head, ListItem tail) {
        this.head = head;
        this.tail = tail;
    }

    public Term getHead() {
        return head;
    }

    public Term getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return "[" + head + "," + tail + "]";
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        head.substituteUnknownTypes(resolvedTypes);
        tail.substituteUnknownTypes(resolvedTypes);
    }
}
