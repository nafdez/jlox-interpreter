package es.ignaciofp.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();    

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name, String.format("Undefined variable '%s'.", name.lexeme));
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        throw new RuntimeError(name, String.format("Undefined variable '%s'.", name.lexeme));
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }
}
