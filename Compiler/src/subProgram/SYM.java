package subProgram;

import staticVar.Operator;
import staticVar.Reserved;
import staticVar.Variable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import static subProgram.Table.isNumber;

public class SYM {
    private int state;
    private int level;
    private List<Table> sym;
    private List<Integer> num;

    public SYM() {
        state = 0;  // 当前状态
        level = 0;  // 嵌套层次
        sym = new LinkedList<Table>();      // 关键字表
        num = new LinkedList<Integer>();    // 数字表
    }

    public void split(File file) throws FileNotFoundException {
        Scanner fileIn = new Scanner(file);
        String temp = fileIn.nextLine();
        String now = null;
        int index = 0, lines = 1;
        while (temp != null) {
            Table tempTable = new Table();
            if (temp.charAt(index) == ':') {
                if (index+1 >= temp.length() || temp.charAt(index+1) != '=')
                    throw new ErrorException("There is no Operator named ',' at line "+lines);
                index++;
                if (now.length() != 0) {
                    Table tableNow = new Table();
                    tableNow.set(now, lines, level, state);
                    sym.add(tableNow);
                }
                now = null;
                tempTable.set(":=", lines, level, state);
                sym.add(tempTable);
            }
            else if (temp.charAt(index) == '<') {
                if (now.length() != 0) {
                    Table tableNow = new Table();
                    tableNow.set(now, lines, level, state);
                    sym.add(tableNow);
                }
                if (index < temp.length() && temp.charAt(index+1) == '=') {
                    tempTable.set("<=", lines, level, state);
                    index++;
                }
                else if (index < temp.length() && temp.charAt(index+1) == '>') {
                    tempTable.set("<>", lines, level, state);
                    index++;
                }
                else tempTable.set("<", lines, level, state);
                sym.add(tempTable);
                now = null;
            }
            else if (temp.charAt(index) == '>') {
                if (index < temp.length() && temp.charAt(index+1) == '='){
                    tempTable.set(">=", lines, level, state);
                    index++;
                }
                else tempTable.set(">", lines, level, state);
                now = null;
            }
            else if (temp.charAt(index) == ',' || temp.charAt(index) == ';' || temp.charAt(index) == '('
                    || temp.charAt(index) == ')' || temp.charAt(index) == '+' || temp.charAt(index) == '-'
                    || temp.charAt(index) == '*' || temp.charAt(index) == '/' || temp.charAt(index) == '='
                    || temp.charAt(index) == ' ' || temp.charAt(index) == '#' || temp.charAt(index) == '\n'
                    || temp.charAt(index) == '.') {
                if (now != null && now.length() > 0) {
                    if (now.charAt(0) >= '0' && now.charAt(0) <= '9') {
                        if (isNumber(now) == -1)
                            throw new ErrorException("There is a illegal variable name '"+now+"' at line "+lines);
                        else num.add(isNumber(now));
                    }
                    else if (state == 1 && Reserved.isReserved(now)) {
                        throw new ErrorException("Cannot use reserved word as variable name '"+now+"' at line "+lines);
                    }
                    else if (state != 1 && !Reserved.isReserved(now) && !Operator.isOperator(now) && !Variable.isVariable(now)) {
                        throw new ErrorException("There is no sym like '"+now+"' at line "+lines);
                    }

                    tempTable.set(now, lines, level, state);
                    if (now.equals("var") || now.equals("const") || now.equals("procedure"))
                        state = 1;
                    if (now.equals("begin"))
                        level++;
                    else if (now.equals("end"))
                        level--;

                    if (now.equals("begin"))
                        tempTable.set(now, lines-1, level, state);
                    sym.add(tempTable);
                    now = null;
                }

                now = now;
                if (temp.charAt(index) != ' ' && temp.charAt(index) != '\n') {
                    tempTable = new Table();
                    tempTable.set(String.valueOf(temp.charAt(index)), lines, level, state);
                    if (temp.charAt(index) == ';')
                        state = 0;
                    sym.add(tempTable);
                }
            }
            else {
                if (now != null) now += temp.charAt(index);
                else now = String.valueOf(temp.charAt(index));
            }

            index++;
            if (index >= temp.length()) {
                if (fileIn.hasNext())
                    temp = fileIn.nextLine();
                else temp = null;
                lines++;
                index = 0;
            }
        }

        fileIn.close();
    }

    public List<Table> getSym() { return sym; }
    public List<Integer> getNum() { return num; }

    public void printSYM(SYM sym, String symFIle) throws IOException {
        PrintWriter symOutFile = new PrintWriter("src/resource/"+symFIle, "UTF-8");
        ListIterator iterSym = sym.getSym().listIterator(); // 迭代器迭代两个List并输出
        while (iterSym.hasNext()) {
            Table temp = (Table)iterSym.next();
            symOutFile.println(temp.toString());
        }
        ListIterator iterNum = sym.getNum().listIterator();
        while (iterNum.hasNext()) {
            int temp = (int)iterNum.next();
            symOutFile.println("num "+temp);
        }
        symOutFile.close();
        System.out.println("The table of SYM has been output to file src/resource/"+symFIle);
    }
}
