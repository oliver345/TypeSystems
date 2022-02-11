package typed.ski.lang.type;

public interface Ty {

    enum TypeImplementationEnum {
        BOOL("Bool"), FUNCTION("Function"), STR("Str");

        private final String typeName;

        TypeImplementationEnum(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }
}
