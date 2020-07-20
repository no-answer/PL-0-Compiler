package com.company;

import staticVar.Code;
import staticVar.synTable;
import subProgram.BLOCK;
import subProgram.ErrorException;
import subProgram.SYM;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("src/resource/demoMini" +
                ".txt");
        SYM sym = new SYM();
        try {
            sym.split(file);
        }
        catch (ErrorException error) {
            System.out.println(error);
            return ;
        }

        String symFile = "sym.txt";
        sym.printSYM(sym, symFile); // 将词法分析结果输出到文件sym.txt中

        String syntaxTreeFile = "tree.txt";
        BLOCK block = new BLOCK(sym);
        try {
            if (!block.program(syntaxTreeFile))     // 对程序进行语法分析
                System.out.println("Compile Error");
            else
                System.out.println("Compile Success");
            synTable.printSynTable("synTable.txt");
            Code.printCodes("code.txt");
        }
        catch (ErrorException error) {
            System.out.println(error);
        }

        // Implement implement = new Implement(Code.getCodes());
        // implement.implement();
    }
}
