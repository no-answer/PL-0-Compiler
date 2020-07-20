package staticVar;

import java.util.HashMap;
import java.util.Map;

// 判断是否是界符
public class Bounder {
    public static Map<String, Boolean> bounder = new HashMap<String, Boolean>();

    static {
        bounder.put("(", true);
        bounder.put(")", true);
        bounder.put(".", true);
        bounder.put(",", true);
        bounder.put(";", true);
    }

    public static boolean isBounder(String name) {
        return bounder.containsKey(name);
    }
}
