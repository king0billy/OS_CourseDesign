package SchedulingSystem;

import java.util.LinkedList;
import java.util.Queue;

public class Block {
    private int ID;
    private int size;
    private int startAddress;
    private int EndAddress;
    private Queue<Integer> JobIDs = new LinkedList<>();
    private int remainSize;//似乎是附近的空闲空间//不是,就是可动内存而已

    public Block() {

    }

    public Block(int size) {
        setSize(size);
        setRemainSize(size);
    }

    /**
     * 加入一个作业的ID，表示占用
     *
     * @param jobID
     */
    public void addaJobID(Integer jobID) {
        Queue<Integer> queue = getJobIDs();
        queue.add(jobID);
        setJobIDs(queue);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getEndAddress() {
        return EndAddress;
    }

    public void setEndAddress(int endAddress) {
        EndAddress = endAddress;
    }

    public Queue<Integer> getJobIDs() {
        return JobIDs;
    }

    public void setJobIDs(Queue<Integer> jobIDs) {
        JobIDs = jobIDs;
    }

    public int getRemainSize() {
        return remainSize;
    }

    public void setRemainSize(int remainSize) {
        this.remainSize = remainSize;
    }
    //todo 难用上
    @Override
    public String toString() {
        return "BlockID:" + getID() + "  Size:" + getSize() + "  开始地址:" + getStartAddress() +
                "  结束地址:" + getEndAddress() + "  剩余大小:" + getRemainSize() + "  作业:" + getJobIDs().toString();
    }
    public String printList() {
        return "BlockID:" + getID() + "  Size:" + getSize() + "  开始地址:" + getStartAddress() +
                "  结束地址:" + getEndAddress() + "  剩余大小:" + getRemainSize() + "  作业:" + getJobIDs().toString();
    }

    public Object[] toArray() {
        Object[] array = new Object[6];
        array[0] = getID();
        array[1] = getSize();
        array[2] = getStartAddress();
        array[3] = getEndAddress();
        array[4] = getRemainSize();
        array[5] = getJobIDs();
        return array;
    }
}
