package subProgram;

import staticVar.Code;
import staticVar.Variable;
import staticVar.synTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class BLOCK {
    private int pos;
    private int depth;
    private List<Table> sym;
    private List<Integer> num;
    PrintWriter syntaxTreeFile;
    private static int cx = 0;
    private static int dx = 3;
    private static int proLevel = 0;
    private static int varCount = 0;

    public BLOCK(SYM s) {
        pos = 0;
        depth = 0;
        sym = s.getSym();
        num = s.getNum();   // 获取词法分析结果
    }

    public boolean program(String treeFile) throws IOException {
        syntaxTreeFile = new PrintWriter("src/resource/"+treeFile, "UTF-8");
        syntaxTreeFile.println(deep()+"程序");
        if(!subProgram()) { // 对分程序进行语法分析
            throw new ErrorException("error in subProgram");
        }
        if (!kind().equals("._bd"))
            throw new ErrorException("error: need a '.' at the end of program at line "+line());
        Code.addCode("opr", 0, 0);
        cx++;
        syntaxTreeFile.println(deep()+name());
        syntaxTreeFile.close();
        System.out.println("The syntax tree has been output  to file src/resource/"+treeFile);
        return true;
    }

    private boolean subProgram() {
        depth++;
        Code.addCode("jmp", 0, -1);
        cx++;
        int tempCodeAddr = cx-1;
        syntaxTreeFile.println(deep()+"分程序");
        if(!constantDescription())          // 对常量说明部分进行语法分析
            throw new ErrorException("error in constant description at line "+line());
        if (!variableDescription())    // 对变量说明部分进行语法分析
            throw new ErrorException("error in variable description at line "+line());
        if (!processDescription())     // 对过程说明部分进行语法分析
            throw new ErrorException("error in process description at line "+line());
        Code tempCode = new Code("jmp", 0, cx-1);
        Code.setCode(tempCodeAddr, tempCode);
        Code.addCode("int", 0, varCount);
        cx++;
        varCount = 0;
        if (!sentence()) {             // 对主程序语句进行语法分析
            throw new ErrorException("error in sentence of main at line " + line());
        }
        depth--;
        return true;
    }

    private boolean constantDescription() { // 对常量说明部分进行语法分析
        if (!kind().equals("const_Re"))     // 未定义常量，无需对常量说明部分进行语法分析
            return true;
        depth++;
        syntaxTreeFile.println(deep()+"常量说明部分");
        depth++;
        syntaxTreeFile.println(deep()+name());
        pos++;
        constantDefinition();               // 对常量定义语句进行语法分析
        while (kind().equals(",_bd")) {     // 逗号隔开，定义了多个常量
            syntaxTreeFile.println(deep()+name());
            pos++;
            constantDefinition();
        }
        if (!kind().equals(";_bd"))         // 语句应当以 ; 结尾
            throw new ErrorException("error: need a ';' after constant description at line "+line());
        syntaxTreeFile.println(deep()+name());
        pos++;
        depth -= 2;
        return true;
    }

    private boolean variableDescription() { // 对变量说明部分进行语法分析
        if (!kind().equals("var_Re"))       // 未定义变量，无需对变量说明部分进行语法分析
            return true;
        depth++;
        syntaxTreeFile.println(deep()+"变量说明部分");
        depth++;
        syntaxTreeFile.println(deep()+name());
        pos++;
        synTable syn = new synTable();
        syn.setName(name());
        syn.setKind("var");
        syn.setAddr(dx++);
        syn.setLevel(proLevel);
        if (!isIdentifier())                // 判断当前词是否是标识符
            return false;
        varCount++;
        synTable.synTables.add(syn);
        while (kind().equals(",_bd")) {    // 逗号隔开，定义了多个变量
            syntaxTreeFile.println(deep()+name());
            pos++;
            synTable syn1 = new synTable();
            syn1.setName(name());
            syn1.setKind("var");
            syn1.setAddr(dx++);
            syn1.setLevel(proLevel);
            if (!isIdentifier())                // 判断当前词是否是标识符
                return false;
            synTable.synTables.add(syn1);
            varCount++;
        }
        if (!kind().equals(";_bd"))         // 语句应当以 ; 结尾
            throw new ErrorException("error: need a ';' after constant description at line "+line());
        syntaxTreeFile.println(deep()+name());
        depth -= 2;
        pos++;
        return true;
    }

    private boolean processDescription() {
        if (!kind().equals("procedure_Re")) // 未定义过程说明部分，无需分析
            return true;
        dx++;
        syntaxTreeFile.println(deep(1)+"过程说明部分");
        syntaxTreeFile.println(deep(2)+"过程首部");
        syntaxTreeFile.println(deep(3)+name());
        depth += 3;
        pos++;
        synTable syn = new synTable();
        syn.setName(name());
        syn.setKind("procedure");
        syn.setAddr(cx);
        syn.setLevel(proLevel);
        if (!isIdentifier())
            throw new ErrorException("error: need a identifier in the head of procedure at line "+line());
        synTable.synTables.add(syn);
        if (!kind().equals(";_bd"))
            throw new ErrorException("error: need a ';' after the head of procedure at line "+line());
        syntaxTreeFile.println(deep()+name());
        Code.addCode("opr", 0, 0);
        cx++;
        depth -= 2;
        pos++;
        if (!subProgram())
            throw new ErrorException("error in the subprogram of procedure at line "+line());
        depth--;
        while (kind().equals("procedure_Re"))  // 有多个过程说明部分
            processDescription();
        return true;
    }

    private boolean sentence() {
        depth++;
        syntaxTreeFile.println(deep()+"语句");
        if (kind().equals(";_bd")) {            // 空语句
            syntaxTreeFile.println(deep()+"空语句");
            syntaxTreeFile.println(deep(1)+name());
            return true;
        }
        else if (kind().equals("id")) {         // 赋值语句
            depth++;
            syntaxTreeFile.println(deep()+"赋值语句");
            depth++;
            if (!isIdentifier())                // 不是标识符则抛出异常
                throw new ErrorException("error: '"+name()+"' is not a identifier at line "+line());
            if (!kind().equals(":=_op"))           // := 符号不存在则抛出异常
                throw new ErrorException("error: need a ':=' at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            int t = expression();                       // 不是表达式则抛出异常
            if (!kind().equals(";_bd"))         // 句末无 ; 则抛出异常
                throw new ErrorException("error: need a ';' after the expression at line "+line());
            depth -= 2;
            pos++;
            Code.addCode("sto", 0, 0);
            cx++;
        }
        else if (kind().equals("if_Re")) {      // 条件语句
            depth++;
            syntaxTreeFile.println(deep()+"条件语句");
            depth++;
            syntaxTreeFile.println(deep()+name());
            pos++;
            condition();
            if (!kind().equals("then_Re"))
                throw new ErrorException("error: need a 'then' after if at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            depth--;
            Code.addCode("jpc", 0, -1);
            int tempAddr = cx;
            cx++;
            if (!sentence())
                throw new ErrorException("error in the sentence of if-then at line "+line());
            Code.setCode(tempAddr, new Code("jpc", 0, cx));
            depth--;
        }
        else if (kind().equals("while_Re")) {   // 当型循环语句
            syntaxTreeFile.println(deep(1)+"当型循环语句");
            syntaxTreeFile.println(deep(2)+name());
            depth += 2;
            pos++;
            condition();
            if (!kind().equals("do_Re"))
                throw new ErrorException("error: need a 'do' after while a line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            depth--;
            Code.addCode("jpc", 0, -1);
            int tempAddr = cx;
            cx++;
            sentence();
            Code.setCode(tempAddr, new Code("jpc", 0, cx));
            depth--;
        }
        else if (kind().equals("call_Re")) {    // 过程调用语句
            depth++;
            syntaxTreeFile.println(deep()+"过程调用语句");
            depth++;
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!isIdentifier())
                throw new ErrorException("error: '"+name()+"' is not a identifier at line "+line());
            if (!kind().equals(";_bd"))
                throw new ErrorException("error: need a ';' after call at line "+line());
            pos++;
            depth -= 2;
        }
        else if (kind().equals("read_Re")) {    // 读语句
            depth++;
            syntaxTreeFile.println(deep()+"读语句");
            depth++;
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!kind().equals("(_bd"))
                throw new ErrorException("error: need a '(' after call at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!isIdentifier())
                throw new ErrorException("error: '"+name()+"' is not a identifier at line "+line());
            while (kind().equals(",_bd")) {
                syntaxTreeFile.println(deep()+name());
                pos++;
                if (!isIdentifier())
                    throw new ErrorException("error: '"+name()+"' is not a identifier at line "+line());
            }
            if (!kind().equals(")_bd"))
                throw new ErrorException("error: need a ')' after '(' at in read at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!kind().equals(";_bd"))
                throw new ErrorException("error: need a ';' after read at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            depth -= 2;
        }
        else if (kind().equals("write_Re")) {   // 写语句
            depth++;
            syntaxTreeFile.println(deep()+"写语句");
            depth++;
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!kind().equals("(_bd"))
                throw new ErrorException("error: need a '(' after call at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            expression();
            while (kind().equals(",_bd")) {
                syntaxTreeFile.println(deep()+name());
                pos++;
                expression();
            }
            if (!kind().equals(")_bd"))
                throw new ErrorException("error: need a ')' after '(' at in write at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!kind().equals(";_bd"))
                throw new ErrorException("error: need a ';' after call at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            depth -= 2;
        }
        else if (kind().equals("begin_Re")) {   // 复合语句
            depth++;
            syntaxTreeFile.println(deep()+"复合语句");
            depth++;
            syntaxTreeFile.println(deep()+name());
            pos++;
            while (!kind().equals("end_Re")) {
                if (!sentence())
                    throw new ErrorException("error in the sentence of begin-end at line "+line());
            }
            if (!kind().equals("end_Re"))
                throw new ErrorException("error: need a 'end' after begin at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            if (!kind().equals(";_bd"))
                throw new ErrorException("error: need a ';' after begin-end sentence at line "+line());
            syntaxTreeFile.println(deep()+name());
            pos++;
            depth -= 2;
        }
        else
            throw new ErrorException("error: unknown sentence type at line "+line());
        depth--;
        return true;
    }

    private String kind() { // 获取当前词的种类
        return sym.get(pos).getKind();
    }
    private int line() {    // 获取当前词所在行数
        return sym.get(pos).getLine();
    }
    private String name() { // 获取当前词
        return sym.get(pos).getName();
    }
    private int level() {
        return sym.get(pos).getLevel();
    }

    private boolean constantDefinition() {  // 对常量定义语句进行语法分析
        synTable syn = new synTable();
        syn.setName(name());
        syn.setKind("const");
        if (!isIdentifier())                // 不是用户自定义变量名则抛出异常
            throw new ErrorException("error: there is not a identifier at line "+line());
        if (!kind().equals("=_op"))         // 不含有 = 则抛出异常
            throw new ErrorException("error: need a '=' in constant definition at line "+line());
        syntaxTreeFile.println(deep()+name());
        pos++;
        if (!isUnsignedInt())               // 不是无符号整数则抛出异常
            throw new ErrorException("error: there is not a unsigned integer at line "+line());
        pos--;
        syn.setValue(Integer.parseInt(name()));
        pos++;
        synTable.synTables.add(syn);
        return true;
    }

    private boolean isIdentifier() {        // 判断是否是用户自定义变量名
        if (!kind().equals("id"))           // 单词类别不是用户自定义标识符则抛出异常
            throw new ErrorException("'"+name()+"' is not a identifier at line "+line());
        if (!isLetter(name().charAt(0)))    // 变量名首字母不是英文字母则抛出异常
            throw new ErrorException("error: variable names cannot start without letter like '"+name()+"' at line "+line());
        for (int i = 1; i < name().length(); i++) { // 变量名中有英文字母或数字以外的字符则抛出异常
            if (!isLetter(name().charAt(i)) && (name().charAt(i) < '0' || name().charAt(i) > '9'))
                throw new ErrorException("error: '"+name()+"' is not a legal variable name at line "+line());
        }
        syntaxTreeFile.println(deep()+"标识符");
        syntaxTreeFile.println(deep(1)+name());
        pos++;
        return true;
    }

    private boolean isUnsignedInt() {        // 判断是否是无符号整数
        for (int i = 0; i < name().length(); i++) {
            if (name().charAt(i) < '0' || name().charAt(i) > '9')
                return false;
        }
        syntaxTreeFile.println(deep()+"无符号整数");
        syntaxTreeFile.println(deep(1)+name());
        pos++;
        return true;
    }

    private boolean isLetter(char x) {       // 判断是否是英文字母
        if (x >= 'a' && x <= 'z')
            return true;
        else if (x >= 'A' && x <= 'Z')
            return true;
        return false;
    }

    private int expression() {
        syntaxTreeFile.println(deep()+"表达式");
        depth++;
        int exp = 0;
        if (kind().equals("-_op")) {
            syntaxTreeFile.println(deep()+name());
            Code.addCode("opr", 0, 2);
            cx++;
            pos++;
            exp = -term();
        }
        else if (kind().equals("+_op")) {
            syntaxTreeFile.println(deep()+name());
            Code.addCode("opr", 0, 3);
            cx++;
            pos++;
            exp = term();
        }
        else exp = term();
        while (kind().equals("+_op") || kind().equals("-_op")) {
            if (kind().equals("-_op")) {
                syntaxTreeFile.println(deep()+name());
                Code.addCode("opr", 0, 2);
                cx++;
                pos++;
                exp -= term();
            }
            else {
                syntaxTreeFile.println(deep()+name());
                Code.addCode("opr", 0, 3);
                cx++;
                pos++;
                exp += term();
            }
        }
        depth--;
        return exp;
    }

    private int term() {
        syntaxTreeFile.println(deep()+"项");
        depth++;
        int ter = factor();
        while (kind().equals("*_op") || kind().equals("/_op")) {
            if (kind().equals("*_op")) {
                syntaxTreeFile.println(deep()+name());
                Code.addCode("opr", 0, 4);
                cx++;
                pos++;
                ter *= factor();
            }
            else {
                syntaxTreeFile.println(deep()+name());
                Code.addCode("opr", 0, 5);
                cx++;
                pos++;
                ter /= factor();
            }
        }
        depth--;
        return ter;
    }

    private int factor() {
        syntaxTreeFile.println(deep()+"因子");
        int n;
        if (Variable.isVariable(name())) {// 因子是标识符
            syntaxTreeFile.println(deep(1)+"标识符");
            syntaxTreeFile.println
                    (deep(2)+name());
            n = Variable.valueOfKey(name());
            pos++;
        }
        else if (kind().equals("number")) {// 因子是无符号整数
            n = Table.isNumber(name());
            syntaxTreeFile.println(deep(1)+"无符号整数");
            syntaxTreeFile.println(deep(2)+name());
            pos++;
        }
        else if (kind().equals("(_bd")) {// 括号包裹的表达式
            n = expression();
        }
        else throw new ErrorException("error in factor at line "+line());
        return n;
    }

    private boolean condition() {
        syntaxTreeFile.println(deep()+"条件");
        depth++;
        if (kind().equals("odd_Re")) {
            syntaxTreeFile.println(deep()+name());
            pos++;
            int con = expression();
            depth--;
            return con == 0;
        }
        else if (Variable.isVariable(name())) {
            int con1 = expression();
            if (!kind().equals("=_op") && !kind().equals("#_op") && !kind().equals("<_op")
                    && !kind().equals("<=_op") && !kind().equals(">_op") && !kind().equals(">=_op"))
                throw new ErrorException("error: need a relational operator at line "+line());
            String op = kind();
            syntaxTreeFile.println(deep()+name());
            pos++;
            int con2 = expression();
            depth--;
            if (op.equals("=_op"))
                return con1 == con2;
            else if (op.equals("#_op"))
                return con1 != con2;
            else if (op.equals("<_op"))
                return con1 < con2;
            else if (op.equals("<=_op"))
                return con1 <= con2;
            else if (op.equals(">_op"))
                return con1 > con2;
            else if (op.equals(">= op"))
                return con1 >= con2;
        }
        throw new ErrorException("error: wrong return in condition at line "+line());
    }

    private String deep() {
        String d = "";
        for (int i = 0; i < depth; i++)
            d += "  ";
        return d;
    }

    private String deep(int offset) {
        String d = "";
        for (int i = 0; i < depth+offset; i++)
            d += "  ";
        return d;
    }
}
