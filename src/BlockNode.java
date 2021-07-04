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
public class BlockNode {
        final static  int MAXSIZE= 100;//内存空间最大值
        int processID;//占用内存块的作业id
        int status;//作业运行状态
        int startingAddress;//起始地址
        int size;//进程占用大小
        BlockNode previous;//指向前一个节点的引用
        BlockNode next;//指向都一个节点的引用
        static BlockNode headNode;
        static int allocateResult=1;

    static BlockNode initPartition() {
         BlockNode pointer = new BlockNode();//( BlockNode*)malloc(sizeof( BlockNode));
        pointer.startingAddress = 0;
        pointer.size = MAXSIZE;
        pointer.status = 0;
        pointer.processID =  -9;
        pointer.previous = null;
        pointer.next = null;
        return pointer;
    }

    //在链表位置p处为编号为processID，大小为processSize的作业分配空间
    static int allocate(int processID,int processSize,  BlockNode pointer) {
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
             BlockNode newPartition = new BlockNode();//( BlockNode*)malloc(sizeof( BlockNode));
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
    static BlockNode firstFitFindFreePartition(int processSize) {
         BlockNode pointer = headNode;
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
    static BlockNode bestFitFindFreePartition(  int processSize) {
         BlockNode pointer = headNode;  BlockNode minPointer = null;
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
         BlockNode pointer = headNode;
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
             BlockNode preNode = pointer.previous;
             BlockNode nextNode = pointer.next;

            preNode.size += pointer.size;
            preNode.next = pointer.next;
            nextNode.previous = preNode;


            if (pointer.next.status == 0 &&
                    pointer.startingAddress + pointer.size == pointer.next.startingAddress) {
                //后面紧接着一个size不为0的空闲分区
                preNode.size += nextNode.size;
                preNode.next = nextNode.next;
                if(nextNode.next!=null)nextNode.next.previous = preNode;//todo 为什么这里会null
                //free(nextNode);
            }
            //free(pointer);

        } else {
            if (pointer.next != null && pointer.next.status == 0 &&
                    pointer.startingAddress + pointer.size == pointer.next.startingAddress) {
                //后面紧接着一个size不为0的空闲分区
                 BlockNode nextNode = pointer.next;
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
         BlockNode pointer = headNode;

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
}
