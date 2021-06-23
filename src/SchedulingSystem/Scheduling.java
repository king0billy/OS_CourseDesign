package SchedulingSystem;

import Tool.Clock;
import Tool.ToolForSch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;

public class Scheduling {
    public List<JOB> well = new ArrayList<>();//输入井
    public List<JOB> jobs = new ArrayList<>(); //后备队列
    public List<JOB> jobs1 = new ArrayList<>();    //未完成作业队列（保存着作业调度后的顺序）
    public HashMap<Integer, JOB> integerJobHashMap = new HashMap<>();//作业队列的内存块分配情况(会被取走)
    public HashMap<Integer, Block> integerBlockHashMap = new HashMap<>(); //块的情况表
    public List<JOB> jobs2 = new ArrayList<>();  //分配资源块后的就绪队列（会被取走）
    public int tapes;//磁带机
    public List<JOB> jobs3 = new ArrayList<>(); //进程调度后的结果（最终结果）
    public JOB nowJob;//当前作业
    public JOB nowProess;//当前进程
    public Clock clock = new Clock();//系统时间
    public int jobIDNum = 0;//ID号排列
    public boolean run = false;

    public Scheduling() {
        init();
    }

    private void init() {
        tapes = ToolForSch.tapesTol;
        addBlock(new Block(ToolForSch.memoryTol));
        TestData();
        clock.addPropertyChangeListener(new TimerListener());
    }

    private void TestData() {
        addWell(new JOB("JOB1", 0, 25, 15, 2));
        addWell(new JOB("JOB2", 20, 30, 60, 1));
        addWell(new JOB("JOB3", 30, 10, 50, 3));
        addWell(new JOB("JOB4", 35, 20, 10, 2));
        addWell(new JOB("JOB5", 40, 15, 30, 2));
    }

    public void run() {
        TimerThread timerThread = new TimerThread() ;
        timerThread.start();
    }


    /**
     * 计时器
     */
    class TimerThread extends Thread {
        public void run() {
            run = true;
            while (run) {
                ToolForSch.Sleep(ToolForSch.unitOfTime);
                clock.goOneTime();
            }
        }
    }

    /**
     * 时间变化一次
     */
    private class TimerListener implements PropertyChangeListener {

        /**
         * 进程执行一个时间单位
         */
        private void SPF() {
            if (nowProess == null) {
                if (jobs2.size() != 0) {
                    nowProess = jobs2.remove(0);
                    nowProess.setStatus('R');
                    System.out.println("时间：" + clock.getTime() + "|开始调用进程：" + nowProess.toString());
                } else if (integerJobHashMap.size() == 0 && well.size() == 0) {
                    System.out.println("时间：" + clock.getTime() + "|任务完成");
                    System.out.println("________________以下是结果_________");
                    jobs3.sort(((o1, o2) ->o1.getID()-o2.getID()));
                    for(int i=0;i<=jobs3.size()-1;i++){
                        System.out.println(jobs3.get(i).toString());
                    }
                    run = false;
                    return;
                } else {
                    System.out.println("时间：" + clock.getTime() + "|无进程可调度");
                    return;
                }
            }
            System.out.println("时间：" + clock.getTime() + "|正在执行：" + nowProess.toString());
            nowProess.setTime(nowProess.getTime() + 1);
            if (nowProess.getTime() == nowProess.getServiceTime()) {
                //已经执行完毕
                nowProess.setFinishTime(clock.getTime());
                nowProess.setRoundTime(nowProess.getFinishTime() - nowProess.getSubmitTime()+1);
                nowProess.setAveRoundTime((double) nowProess.getRoundTime() / nowProess.getServiceTime());
                nowProess.setStatus('F');
                System.out.println("时间：" + clock.getTime() + "|执行完毕：" + nowProess.toString());
                //释放空间的占用
                integerJobHashMap.remove(nowProess.getID());
                nowProess.setTapeGet(false);
                tapes += nowProess.getTapeNeeded();
                Block block = integerBlockHashMap.get(nowProess.getBlockID());
                Queue<Integer> queue = block.getJobIDs();
                queue.remove(nowProess.getID());
                block.setJobIDs(queue);
                block.setRemainSize(block.getRemainSize() + nowProess.getSize());
                integerBlockHashMap.put(block.getID(), block);
                for (int j = 0; j <= jobs1.size() - 1; j++) {
                    if (jobs1.get(j).getID() == nowProess.getID()) {
                        jobs1.remove(j);
                    }
                }
                jobs3.add(nowProess);
                nowProess = null;
                BF();
            }
        }

        /**
         * 作业调度
         */
        private void SJF() {
            if (jobs.size() == 0) {
                System.out.println("时间：" + clock.getTime() + ":无作业需要调度");
            }

            jobs.sort((o1, o2) -> o1.getServiceTime() - o2.getServiceTime());

            for (int i = 0; i <= jobs.size() - 1; i++) {
                nowJob = jobs.remove(0);
                jobs1.add(nowJob);
                //尝试分配
                integerJobHashMap.put(nowJob.getID(), nowJob);
                BF();
            }
            nowJob = null;
        }

        /**
         * 输入井作业到达
         */
        private void jobArrive() {
            for (int i = 0; i <= well.size() - 1; i++) {
                if (well.get(i).getSubmitTime() == clock.getTime()) {
                    jobs.add(well.remove(i));
                    i--;
                }
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            jobArrive();
            SJF();
            SPF();
        }
    }


    /**
     * 添加作业
     *
     * @param job 新添加的作业
     */
    public void addWell(JOB job) {
        job.setID(jobIDNum++);
        well.add(job);
    }

    /**
     * 最佳适应
     */
    public void BF() {
        if (integerJobHashMap.size() >= 1 && integerBlockHashMap.size() >= 1) {
            //按作业调度顺序来分配
            int jobID;
            for (int i = 0; i <= jobs1.size() - 1; i++) {
                jobID = jobs1.get(i).getID();
                if (integerJobHashMap.get(jobID) != null &&
                        integerJobHashMap.get(jobID).getState() == 0 &&
                        !integerJobHashMap.get(jobID).isTapeGet()) {
                    JOB job = integerJobHashMap.get(jobID);
                    System.out.println("时间：" + clock.getTime()+"选中作业：" + job.toString());
                    //找到最佳适应的块
                    int bestFitBlockID = -1;
                    for (int blockID = 0; blockID <= integerBlockHashMap.size() - 1; blockID++) {
                        Block block = integerBlockHashMap.get(blockID);
                        if (block.getRemainSize() - job.getSize() >= 0) {
                            if (bestFitBlockID == -1) bestFitBlockID = blockID;
                            else if (block.getRemainSize() <= integerBlockHashMap.get(bestFitBlockID).getRemainSize()) {
                                bestFitBlockID = blockID;
                            }
                        }
                    }
                    //操作部分
                    if (bestFitBlockID != -1 && job.getTapeNeeded() <= tapes) {
                        Block block;
                        block = integerBlockHashMap.get(bestFitBlockID);
                        block.setRemainSize(block.getRemainSize() - job.getSize());
                        block.addaJobID(jobID);
                        job.setState(1);
                        job.setBlockID(bestFitBlockID);
                        job.setTapeGet(true);
                        tapes -= job.getTapeNeeded();
                        integerBlockHashMap.put(bestFitBlockID, block);
                        integerJobHashMap.put(jobID, job);
                        System.out.println("时间：" + clock.getTime()+"作业分配成功：" + job.toString());
                        //抢占(排好队等待调度)
                        if(nowProess!=null && job.getServiceTime()< nowProess.getServiceTime()){
                            nowProess.setStatus('W');
                            jobs2.add(0,nowProess);
                            jobs2.add(0,job);
                            nowProess = null;
                            System.out.println("时间：" + clock.getTime()+"抢占成功"+job);
                        }else {
                            jobs2.add(job);
                            jobs2.sort((o1, o2) -> o1.getServiceTime()-o2.getServiceTime());
                        }
                    } else {
                        System.out.println("时间：" + clock.getTime()+"作业分配失败：" + job.toString());
                        System.out.println("时间：" + clock.getTime()+"原因：空间不够或磁带不够");
                    }
                }
            }
        }
    }

    /**
     * 合并空间
     */
    public void spaceToMerge() {
        if (integerBlockHashMap.size() > 0) {
            int size1 = integerBlockHashMap.size();
            for (int blockID = 0; blockID <= size1 - 1; blockID++) {
                if (integerBlockHashMap.get(blockID).getRemainSize() > 0) {
                    int finalBlockID = blockID;
                    while (finalBlockID < size1 - 1
                            && integerBlockHashMap.get(finalBlockID + 1).getRemainSize() == integerBlockHashMap.get(finalBlockID + 1).getSize()) {
                        finalBlockID++;
                    }
                    if (finalBlockID != blockID) {
                        int size = 0;
                        Block newBlock = new Block();
                        newBlock.setStartAddress(integerBlockHashMap.get(blockID).getEndAddress() - integerBlockHashMap.get(blockID).getRemainSize() + 1);
                        for (int startID = blockID; startID <= finalBlockID; startID++) {
                            Block block = integerBlockHashMap.get(startID);
                            size += block.getRemainSize();
                            block.setSize(block.getSize() - block.getRemainSize());
                            block.setRemainSize(0);
                            integerBlockHashMap.put(block.getID(), block);
                            if (startID == finalBlockID) {
                                newBlock.setSize(size);
                                newBlock.setRemainSize(size);
                            }
                        }
                        newBlock.setID(integerBlockHashMap.size());
                        newBlock.setEndAddress(newBlock.getStartAddress() + newBlock.getSize() - 1);
                        integerBlockHashMap.put(integerBlockHashMap.size(), newBlock);
                    }
                    blockID = finalBlockID;
                }
            }
        }
    }

    /**
     * 加入块
     *
     * @param block
     * @return
     */
    public boolean addBlock(Block block) {
        if (block == null) return false;
        if (integerBlockHashMap.size() == 0) {
            block.setStartAddress(0);
            block.setEndAddress(block.getSize());
        } else {
            block.setStartAddress(integerBlockHashMap.get(integerBlockHashMap.size() - 1).getEndAddress() + 1);
            block.setEndAddress(block.getStartAddress() + block.getSize() - 1);
        }
        block.setID(integerBlockHashMap.size());
        integerBlockHashMap.put(block.getID(), block);
        return true;
    }
    public static void main(String[] args) {
        new Scheduling().run();
    }
}
