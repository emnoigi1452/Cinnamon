package me.stella.reflection;

public class MethodWrapper {

    private final String name;
    private final Parameters parameters;

    public static MethodWrapper of(String name, Class<?>... parameters) {
        return new MethodWrapper(name, parameters);
    }

    private MethodWrapper(String name, Class<?>... parameters) {
        this.name = name;
        this.parameters = new Parameters(parameters);
    }

    public String getMethodName() {
        return this.name;
    }

    public Parameters getParameters() {
        return this.parameters;
    }

    public static class Parameters {

        private final Class<?>[] parameters;

        public Parameters(Class<?>... types) {
            this.parameters = types;
        }

        public Class<?>[] getParameters() {
            return this.parameters;
        }

    }

}
