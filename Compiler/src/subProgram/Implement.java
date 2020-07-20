package subProgram;

import staticVar.Code;
import subProgram.SYM;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Implement {
    private int PC;     // 下一条目标指令的地址
    private int ST;     // 栈顶寄存器
    private int BP;     // 基地址寄存器
    private Stack<Integer> runningStack = new Stack<Integer>();     // 运行栈
    ArrayList<Code> codes;

    public Implement(ArrayList<Code> c) {
        runningStack.push(0);
        runningStack.push(0);
        runningStack.push(0);
        PC = 0;
        ST = 0;
        BP = 0;
        codes = c;
    }

    public void implement() {
        int index = 0;
        while (index <= codes.size()) {
            PC++;
            if (codes.get(index).getFunction().equals("lit")) {
                ST++;
                runningStack.push(codes.get(index).getOffset());
            }
            else if (codes.get(index).getFunction().equals("lod")) {
                ST++;
                int tempBP = BP;
                for (int i = 0; i < codes.get(index).getLevel(); i++)
                    tempBP = runningStack.get(tempBP);
                if (tempBP+codes.get(index).getOffset() < runningStack.size())
                    runningStack.push(runningStack.get(tempBP+codes.get(index).getOffset()));
            }
            else if (codes.get(index).getFunction().equals("sto")) {
                int tempBP = BP;
                for (int i = 0; i < codes.get(index).getLevel(); i++)
                    tempBP = runningStack.get(tempBP);
                int ttt = tempBP+codes.get(index).getOffset();
                if (ttt < runningStack.size())
                    runningStack.set(ttt, runningStack.peek());
                runningStack.pop();
            }
            else if (codes.get(index).getFunction().equals("cal")) {
                int tempBP = BP;
                for (int i = 0; i < codes.get(index).getLevel(); i++)
                    tempBP = runningStack.get(tempBP);
                runningStack.push(tempBP);
                runningStack.push(BP);
                runningStack.push(PC);
                BP = ST+1;
                PC = codes.get(index).getOffset();
            }
            else if (codes.get(index).getFunction().equals("int")) {
                ST += codes.get(index).getOffset();
            }
            else if (codes.get(index).getFunction().equals("jmp")) {
                PC = codes.get(index).getOffset();
            }
            else if (codes.get(index).getFunction().equals("jpc")) {
                if (ST < runningStack.size() && runningStack.get(ST) == 0)
                    PC = codes.get(index).getOffset();
                ST--;
                runningStack.pop();
            }
            else if (codes.get(index).getFunction().equals("opr")) {
                switch (codes.get(index).getOffset()) {
                    case 0: {
                        if (PC == codes.size()) {
                            System.out.println("end");
                            return ;
                        }
                        int count = runningStack.size()-BP;
                        ST = BP-1;
                        BP = runningStack.get(ST+2);
                        PC = runningStack.get(ST+3);
                        for (int i = 0; i < count; i++)
                            runningStack.pop();
                        break;
                    }
                    case 2: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        runningStack.push(temp1+temp2);
                        break;
                    }
                    case 3: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        runningStack.push(temp1-temp2);
                        break;
                    }
                    case 4: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        runningStack.push(temp1*temp2);
                        break;
                    }
                    case 5: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        runningStack.push(temp1/temp2);
                        break;
                    }
                    case 6: {
                        int temp = runningStack.peek();
                        runningStack.pop();
                        runningStack.push(temp%2);
                        break;
                    }
                    case 7: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        runningStack.push(temp1%temp2);
                        break;
                    }
                    case 8: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        if (temp1 == temp2)
                            runningStack.push(1);
                        else runningStack.push(0);
                        break;
                    }
                    case 9: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        if (temp1 != temp2)
                            runningStack.push(1);
                        else runningStack.push(0);
                        break;
                    }
                    case 10: {
                        int temp1 = runningStack.get(runningStack.size()-2);
                        int temp2 = runningStack.get(runningStack.size()-1);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        if (temp1 < temp2)
                            runningStack.push(1);
                        else runningStack.push(0);
                        break;
                    }
                    case 11: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        if (temp1 >= temp2)
                            runningStack.push(1);
                        else runningStack.push(0);
                        break;
                    }
                    case 12: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        if (temp1 > temp2)
                            runningStack.push(1);
                        else runningStack.push(0);
                        break;
                    }
                    case 13: {
                        int temp1 = runningStack.get(ST-1);
                        int temp2 = runningStack.get(ST);
                        ST--;
                        runningStack.pop();
                        runningStack.pop();
                        if (temp1 <= temp2)
                            runningStack.push(1);
                        else runningStack.push(0);
                        break;
                    }
                    case 14: {
                        System.out.println("输出"+runningStack.peek());
                        ST--;
                        runningStack.pop();
                        break;
                    }
                    case 15: {
                        System.out.println();
                        break;
                    }
                    case 16: {
                        ST++;
                        int temp;
                        System.out.println("请输入");
                        Scanner javaIn = new Scanner(System.in);
                        temp = javaIn.nextInt();
                        runningStack.push(temp);
                        break;
                    }
                }
            }
            index = PC;
        }
    }
}
