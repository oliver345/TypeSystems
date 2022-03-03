package typed.ski.deep.lang.type;

public interface Ty {

    enum TypeImplementationEnum {
        BOOL("Bool"), FUNCTION("Function"), STR("Str"), NAT("Nat");

        private final String typeName;

        TypeImplementationEnum(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }
}
