package staticVar;

import java.util.HashMap;
import java.util.Map;

// 判断是否是用户自定义的变量名及相应的值，使用静态Map的形式保证程序执行过程中能够一直保存所有的变量名
public class Variable {
    public static Map<String, Integer> variable = new HashMap<String, Integer>();

    public static boolean isVariable(String name) {
        return variable.containsKey(name);
    }

    public static int valueOfKey(String name) {
        return variable.get(name);
    }
}
