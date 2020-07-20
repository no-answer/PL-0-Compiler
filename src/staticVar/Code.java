package staticVar;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Code {
    private String function;
    private int level;
    private int offset;

    private static ArrayList<Code> codes = new ArrayList<>();

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

    public String toString() {
        return function+" "+level+" "+offset;
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
