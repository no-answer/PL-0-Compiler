package subProgram;

import staticVar.Operator;
import staticVar.Reserved;
import staticVar.Variable;
import staticVar.Bounder;

import java.util.Objects;

public class Table {        // 用于词法分析的Table类
    private String name;    // 词的内容
    private String kind;    // 词的类别
    private int level;      // 词所在的层次
    private int line;       // 词所在的行

    public Table() {        // 构造函数
        name = null;
        kind = null;
        level = 0;
        line = 0;
    }

    public Table(String n, String k, int l, int a) {    // 构造函数重载
        name = n;
        kind = k;
        level = l;
        line = a;
    }

    public void set(String nm, int pos, int nowLevel, int state) { // 设置Table的属性并判断类别
        name = nm;
        if (Reserved.isReserved(nm))        // 当前字符串是保留字
            kind = nm+"_Re";
        else if (Operator.isOperator(nm))   // 当前字符串是操作符
            kind = nm+"_op";
            else if (Bounder.isBounder(nm)) // 当前字符是界符
            kind = nm+"_bd";
        else if (isNumber(nm) != -1)        // 当前字符是无符号整数
            kind = "number";
        else {                              // 均不是则认定为用户自定义的变量名
            kind = "id";
            if (state != 1 && !Variable.isVariable(nm))
                throw new ErrorException("SYM ERROR: this variable '"+nm+"' is not declared at line "+pos);
            else if (state == 1 && Variable.isVariable(nm))
                throw new ErrorException("SYM ERROR: this variable '"+nm+"' has been declared at line "+pos);
            Variable.variable.put(nm, -1);
        }
        level = nowLevel;
        line = pos;
    }

    public static int isNumber(String s) {
        int n = 0;
        for(int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9')
                return -1;
            n = n *10+(s.charAt(i)-'0');
        }
        return n;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        Table other = (Table) obj;
        return Objects.equals(this.name, other.name) &&
                Objects.equals(this.kind, other.kind) &&
                Objects.equals(this.level, other.level) &&
                Objects.equals(this.line, other.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kind, level, line);
    }

    @Override
    public String toString() {
        return "name "+name+" kind "+kind+" level "+level+" line "+line;
    }
}
