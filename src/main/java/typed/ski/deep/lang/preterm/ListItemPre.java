package typed.ski.deep.lang.preterm;

public class ListItemPre implements Preterm {

    private final Preterm head;

    private final ListItemPre tail;

    public ListItemPre() {
        this(null, null);
    }

    public ListItemPre(Preterm head, ListItemPre tail) {
        this.head = head;
        this.tail = tail;
    }

    public Preterm getHead() {
        return head;
    }

    public ListItemPre getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return "[" + head + "," + tail + "]";
    }
}
