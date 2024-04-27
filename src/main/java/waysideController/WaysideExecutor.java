package waysideController;

import java.util.HashMap;
import java.util.Map;

public class WaysideExecutor {

  private Map<String, Integer> variables = new HashMap<>();

  public int getVariable(String variableName) {
    return variables.get(variableName);
  }

  public void addVariable(String variableName, int value) {
    variables.put(variableName, value);
  }
}
