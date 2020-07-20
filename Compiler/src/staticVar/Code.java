package staticVar;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Code {
    private String function;
    private int level;
    private int offset;

    private static ArrayList<Code> codes = new ArrayList<>();
    private static HashMap<String, Integer> Index = new HashMap<String, Integer>();

    static {
        Index.put("returnOpr", 0);
        Index.put("addOpr", 2);
        Index.put("subOpr", 3);
        Index.put("mulOpr", 4);
        Index.put("divOpr", 5);
        Index.put("oddOpr", 6);
        Index.put("modOpr", 7);
        Index.put("equOpr", 8);
        Index.put("neqOpr", 9);
        Index.put("lessOpr", 10);
        Index.put("egreOpr", 11);
        Index.put("greOpr", 12);
        Index.put("elessOpr", 13);
        Index.put("writeOpr", 14);
        Index.put("chaLineOpr", 15);
        Index.put("readOpr", 16);
    }

    public Code() {
        function = null;
        level = -1;
        offset = -1;
    }

    public Code(String func, int lev, int off) {
        function = func;
        level = lev;
        offset = off;
    }

    public String getFunction() {
        return function;
    }

    public int getLevel() {
        return level;
    }

    public int getOffset() {
        return offset;
    }

    public String toString() {
        return function+" "+level+" "+offset;
    }

    public static ArrayList<Code> getCodes() {
        return codes;
    }

    public static int getCodeIndex(String x) {
        return Index.get(x);
    }

    public static int getOffset(int index) {
        return codes.get(index).offset;
    }

    public static void addCode(String func, int lev, int off) {
        codes.add(new Code(func, lev, off));
    }

    public static void addCode(Code c) {
        codes.add(c);
    }

    public static void setCode(int index, Code c) {
        codes.set(index, c);
    }

    public static void printCodes(String file) throws IOException {
        PrintWriter codesOutFile = new PrintWriter("src/resource/"+file, "UTF-8");
        for (int i = 0; i < codes.size(); i++)
            codesOutFile.println(i+": "+codes.get(i).toString());
        codesOutFile.close();;
        System.out.println("The codes has been output to file src/resource/"+file);
    }
}
