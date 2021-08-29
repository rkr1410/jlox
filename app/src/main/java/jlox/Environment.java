package jlox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap<>();

  void define(String name, Object value) {
    values.put(name, value);
//    var oldVar = values.putIfAbsent(name, value);
//    if (oldVar != null) {
//      TODO throw new RuntimeError
//    }
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    throw new RuntimeError(name, "Variable " + name.lexeme + " is undefined");
  }

  Object get(Token name) {
    var value = values.get(name.lexeme);
    //TODO can we try and make it a syntax error? (see 8.3 Environments in 'crafting interpreters')
    if (value == null) {
      throw new RuntimeError(name, "Variable " + name.lexeme + " is undefined");
    }
    return value;
  }
}
