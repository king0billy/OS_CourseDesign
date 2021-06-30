package SchedulingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author 22427(king0liam)
 * @version 1.0
 * @ClassName: newBlock
 * @Description
 * @Date: 2021/6/30 21:38
 * @since version-0.0
 */
public class partitionNode {
    final static  int MAXSIZE= 100;
          int processID;
          int status;
          int startingAddress;
          int size;
        partitionNode previous;
        partitionNode next;

      static partitionNode headNode;

    static partitionNode initPartition() {
         partitionNode pointer = new partitionNode();//( partitionNode*)malloc(sizeof( partitionNode));
        pointer.startingAddress = 0;
        pointer.size = MAXSIZE;
        pointer.status = 0;
        pointer.processID =  -9;
        pointer.previous = null;
        pointer.next = null;
        return pointer;
    }

    //在链表位置p处为编号为processID，大小为processSize的作业分配空间
    static int allocate(int processID,int processSize,  partitionNode pointer) {
        if (pointer == null){
            //没有空闲内存了,分配失败
            return 0;
        }
        if (pointer.size == processSize) {
            //分配整块的分区
            pointer.status = 1;
            pointer.processID = processID;
        } else {
            //分割空闲的内存再分配
             partitionNode newPartition = new partitionNode();//( partitionNode*)malloc(sizeof( partitionNode));
            newPartition.startingAddress = pointer.startingAddress + processSize;
            newPartition.size = pointer.size - processSize;
            newPartition.status = 0;
            newPartition.processID =  -9;

            pointer.size = processSize;
            pointer.status = 1;
            pointer.processID = processID;

            newPartition.next = pointer.next;
            pointer.next = newPartition;
            newPartition.previous = pointer;
        }
        return 1;
    }


    //首次适应算法
    static partitionNode firstFitFindFreePartition(int processSize) {
         partitionNode pointer = headNode;
        while (pointer!=null) {
            if (pointer.status == 0 && pointer.size >= processSize)
                return pointer;
            pointer = pointer.next;
        }
        return null;
    }


    static int firstFitAllocation(int processID, int processSize) {
        return allocate(processID, processSize, firstFitFindFreePartition(processSize));
    }



    //最佳适应算法
    static partitionNode bestFitFindFreePartition(  int processSize) {
         partitionNode pointer = headNode;  partitionNode minPointer = null;
         int minSize = MAXSIZE + 1;
        while (pointer!=null) {
            if (pointer.status == 0 && pointer.size >= processSize) {
                if (pointer.size < minSize) {
                    minSize = pointer.size;
                    minPointer = pointer;
                }
            }
            pointer = pointer.next;
        }

        return minPointer;
    }

    static int bestFitAllocation(int processID, int processSize) {
        return allocate(processID, processSize, bestFitFindFreePartition(processSize));
    }


    static int freeAllocation(int processID) {
        //根据输入的processID寻找进程所在的分区
         partitionNode pointer = headNode;
        while (pointer!=null) {
            if (pointer.processID == processID) {
                break;
            }
            pointer = pointer.next;
        }

        if (pointer == null){
            //找不到
            return 0;
        }


        if (pointer != headNode && pointer.previous.status == 0 &&
                pointer.previous.startingAddress + pointer.previous.size == pointer.startingAddress) {
            //前面紧接着一个size不为0的空闲分区
             partitionNode preNode = pointer.previous;
             partitionNode nextNode = pointer.next;

            preNode.size += pointer.size;
            preNode.next = pointer.next;
            nextNode.previous = preNode;


            if (pointer.next.status == 0 &&
                    pointer.startingAddress + pointer.size == pointer.next.startingAddress) {
                //后面紧接着一个size不为0的空闲分区
                preNode.size += nextNode.size;
                preNode.next = nextNode.next;
                nextNode.next.previous = preNode;
                //free(nextNode);
            }
            //free(pointer);

        } else {
            if (pointer.next != null && pointer.next.status == 0 &&
                    pointer.startingAddress + pointer.size == pointer.next.startingAddress) {
                //后面紧接着一个size不为0的空闲分区
                 partitionNode nextNode = pointer.next;
                pointer.size += nextNode.size;
                pointer.next = nextNode.next;
                if(nextNode.next!=null)nextNode.next.previous = pointer;
                //todo 解决删除唯一一个分区的bug
                pointer.status = 0;
                pointer.processID =  -9;
                //free(nextNode);
            } else {
                //不和空闲分区相连
                pointer.status = 0;
                pointer.processID =  -9;
            }
        }
        return 1;
    }

    static void displayAllocation() {
         partitionNode pointer = headNode;

        System.out.print("\n|分区号\t|起始地址\t|分区大小\t|分区状态\n");

        while (pointer!=null) {
            System.out.print("|"+pointer.processID+"\t\t|"+pointer.startingAddress+"\t\t|"+pointer.size+"\t\t|");
            if(pointer.status==1)System.out.print("已分配");
            else System.out.print("空闲");
            System.out.print("\n");
            pointer = pointer.next;
        }
        System.out.print("\n");
    }
    static void inputControl() throws IOException {
        //传入的参数是一个函数
        //char T[5];
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        String T;
        do{
            System.out.print("申请按 1 释放按 2 退出按 9\n");
            T=bufferedReader.readLine();//scanf("%s", T);
            if (T.equals("1")) {
                int processID, size;
                System.out.print("输入要申请空间的作业id: ");
                processID=Integer.parseInt(bufferedReader.readLine());
                //scanf("%d", &processID );
                System.out.print("输入大小: ");
                size=Integer.parseInt(bufferedReader.readLine());
                //scanf("%d", &size);
                int allocateResult = firstFitAllocation(processID, size);
                if (allocateResult>=1) {
                    System.out.print("申请成功");
                    displayAllocation();
                }
                else {
                    System.out.print("\n内存不足 申请失败\n\n");
                }
            } else{
                if (T.equals("2")) {
                    int processID;
                    System.out.print("输入要释放空间的作业id: ");
                    processID=Integer.parseInt(bufferedReader.readLine());
                    //scanf("%d", &processID);
                    int allocateResult = freeAllocation(processID);
                    if (allocateResult>=1) {
                        System.out.print("释放成功");
                        displayAllocation();
                    } else {
                        System.out.print("未找到相关作业，释放失败\n\n");
                    }
                }
            }
        }while(T.equals("9")==false);
    }

//    void inputControl(int (*allocateAlgorithm)(int,int)) {
//        //传入的参数是一个函数
//        char T[5];
//        do{
//            System.out.print("申请按 1 释放按 2 退出按 9\n");
//            scanf("%s", T);
//            if (T[0] == '1') {
//                int processID, size;
//                System.out.print("输入要申请空间的作业id: ");
//                scanf("%d", &processID );
//                System.out.print("输入大小: ");
//                scanf("%d", &size);
//                int allocateResult = allocateAlgorithm(processID, size);
//                if (allocateResult) {
//                    System.out.print("申请成功");
//                    displayAllocation();
//                }
//                else {
//                    System.out.print("\n内存不足 申请失败\n\n");
//                }
//            } else{
//                if (T[0] == '2') {
//                    int processID;
//                    System.out.print("输入要释放空间的作业id: ");scanf("%d", &processID);
//                    int allocateResult = freeAllocation(processID);
//                    if (allocateResult) {
//                        System.out.print("释放成功");
//                        displayAllocation();
//                    } else {
//                        System.out.print("未找到相关作业，释放失败\n\n");
//                    }
//                }
//            }
//        }while(T[0]!='9');
//    }


/*    void firstFitAllocationControl() {
        inputControl(firstFitAllocation);
    }

    void bestFitAllocationControl() {
        inputControl(bestFitAllocation);
    }



    void selectAlgorithm() {
        //system("cls");
        System.out.print("按1_首次适应算法                ");
        System.out.print("按2_最佳适应算法             \n");
        char op[20];
        scanf("%s", op);
        if (!strcmp(op, "1"))
            firstFitAllocationControl();
        else if (!strcmp(op, "2"))
            bestFitAllocationControl();
    }

      int main()
    {
        headNode = initPartition();
        selectAlgorithm();
        return 0;
    }*/
    public static void main(String[] args) throws IOException {
        headNode = initPartition();
        inputControl();
    }
}
