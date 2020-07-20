package staticVar;

import java.util.HashMap;
import java.util.Map;

// 判断是否是操作符
public class Operator {
    public static Map<String, Boolean> operator = new HashMap<String, Boolean>();

    static {
        operator.clear();
        operator.put("+", true);
        operator.put("-", true);
        operator.put("*", true);
        operator.put("/", true);
        operator.put("<", true);
        operator.put("<=", true);
        operator.put("=", true);
        operator.put(">=", true);
        operator.put(">", true);
        operator.put("<>", true);
        operator.put(":=", true);
        operator.put("#", true);
    }

    public static boolean isOperator(String name) {
        return operator.containsKey(name);
    }
}
