package staticVar;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class synTable {
    private String name;    // 词的内容
    private String kind;    // 词的类别
    private int level;      // 词所在的层次
    private int addr;       // 地址
    private int value;      // 值

    public static ArrayList<synTable> synTables = new ArrayList<synTable>();

    public synTable() {
        name = null;
        kind = null;
        level = -1;
        value = -1;
    }

    public synTable(String n, String k, int lev, int lin, int v) {
        name = n;
        kind = k;
        level = lev;
        value = v;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getAddr() {
        return addr;
    }

    public String getKind() {
        return kind;
    }

    public int getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (kind.equals("const"))
            return name+" "+kind+" "+value;
        else return name+" "+kind+" "+level+" "+addr;
    }

    public static void printSynTable(String file) throws IOException {
        PrintWriter synOutFile = new PrintWriter("src/resource/"+file, "UTF-8");
        for (int i = 0; i < synTables.size(); i++)
            synOutFile.println(i+": "+synTables.get(i).toString());
        synOutFile.close();;
        System.out.println("The table of SYN has been output to file src/resource/"+file);
    }
}
