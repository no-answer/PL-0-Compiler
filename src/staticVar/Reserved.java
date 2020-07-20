package staticVar;

import java.util.HashMap;
import java.util.Map;

// 判断是否是保留字
public class Reserved {
    public static Map<String, Boolean> reserved = new HashMap<String, Boolean>();

    static {
        reserved.clear();
        reserved.put("const", true);
        reserved.put("var", true);
        reserved.put("procedure", true);
        reserved.put("begin", true);
        reserved.put("end", true);
        reserved.put("if", true);
        reserved.put("then", true);
        reserved.put("call", true);
        reserved.put("while", true);
        reserved.put("do", true);
        reserved.put("read", true);
        reserved.put("write", true);
        reserved.put("odd", true);
    }

    public static boolean isReserved(String name) {
        return reserved.containsKey(name);
    }
}
