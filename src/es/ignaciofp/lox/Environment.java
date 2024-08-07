package es.ignaciofp.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    public final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();    

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, String.format("Undefined variable '%s'.", name.lexeme));
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, String.format("Undefined variable '%s'.", name.lexeme));
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }
}
