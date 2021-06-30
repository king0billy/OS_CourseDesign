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
    public List<JOB> jobs = new ArrayList<>(); //后备队列//没能进入内存(就绪队列的)
    public List<JOB> jobs1 = new ArrayList<>();    //未完成作业队列（保存着作业调度后的顺序）
    public HashMap<Integer, JOB> integerJobHashMap = new HashMap<>();//作业队列的内存块分配情况(会被取走)
    //public HashMap<Integer, Block> integerBlockHashMap = new HashMap<>(); //块的情况表
    public List<JOB> jobs2 = new ArrayList<>();  //分配资源块后的就绪队列（会被取走）
    public int tapes;//磁带机
    public List<JOB> jobs3 = new ArrayList<>(); //进程调度后的结果（最终结果）
    public JOB nowJob;//当前作业
    public JOB nowProcess;//当前进程
    public Clock clock = new Clock();//系统时间
    public int jobIDNum = 0;//ID号排列
    public boolean run = false;
    public static int jobChoice=0;
    public static int processChoice=0;

    public Scheduling() {
        init();
    }

    private void init() {
        partitionNode.headNode=partitionNode.initPartition();

        tapes = ToolForSch.tapesTol;
        //addBlock(new Block(ToolForSch.memoryTol));
        TestData();
        for(int i=0;i< well.size();i++){
            System.out.println(well.get(i).printNeed());
        }
        clock.addPropertyChangeListener(new TimerListener());
    }

    private void TestData() {
/*        addWell(new JOB("JOB1", 0, 25, 15, 2));
        addWell(new JOB("JOB2", 20, 30, 60, 1));
        addWell(new JOB("JOB3", 30, 10, 50, 3));
        addWell(new JOB("JOB4", 35, 20, 10, 2));
        addWell(new JOB("JOB5", 40, 15, 30, 2));*/
        //第一组测试数据
/*        addWell(new JOB("JOB1", 0, 25, 15, 2,211));
        addWell(new JOB("JOB2", 20, 30, 60, 1,222));
        addWell(new JOB("JOB3", 30, 10, 50, 3,251));
        addWell(new JOB("JOB4", 35, 20, 10, 2,255));
        addWell(new JOB("JOB5", 40, 15, 30, 2,0));*/
        //第二组测试数据
        addWell(new JOB("JOB1", 0, 25, 15, 2,211));
        addWell(new JOB("JOB2", 20, 30, 60, 1,222));
        addWell(new JOB("JOB3", 30, 10, 10, 1,251));
        addWell(new JOB("JOB4", 35, 20, 30, 3,255));
        addWell(new JOB("JOB5", 40, 15, 30, 2,0));
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
         * 进程执行一个时间单位 short process fist
         */
        private void processRunning1Second() {
            if (nowProcess == null) {
                if (jobs2.size() != 0) {
                    if(processChoice==3){ //进程高响应比的ReplacedTime换成ArriveReadyTime因为是非抢占 HRRN
                        System.out.println("进程调度高响应比");
                        jobs2.sort((o1, o2) ->  ( clock.getTime()-o1.getArriveReadyTime()+(o1.getServiceTime()-o1.getTime()) )/(o1.getServiceTime()-o1.getTime())
                                - (clock.getTime()-o2.getArriveReadyTime()+(o2.getServiceTime()-o2.getTime()) )/(o2.getServiceTime()-o2.getTime()));
                    }
                        nowProcess = jobs2.remove(0);
                    nowProcess.setStatus('R');
                    System.out.println("进程调度选择了:" + nowProcess.getName()+"\n");
                    //System.out.println("进程调度选择了:" + nowProcess.getName());
                    //System.out.println("进程调度选择了:" + nowProcess.printNeed());
                } else if (integerJobHashMap.size() == 0 && well.size() == 0) {
                    System.out.println("********************当前时间: "+JOB.timeFormat(clock.getTime())+"********************");
                    System.out.println("任务完成"+"\n");
                    System.out.println("********************最终统计数据********************");
                    jobs3.sort(((o1, o2) ->o1.getID()-o2.getID()));
                    double averageTime=0.0f;
                    //System.out.println("提交时间\t结束时间\t运行时间\t周转时间\t带权周转时间\t");
                    for(int i=0;i<=jobs3.size()-1;i++){
                        System.out.println(jobs3.get(i).printResult());
                        averageTime+=jobs3.get(i).getAveRoundTime();
                    }
                    System.out.println("平均带权周转时间= " +String.format("%.2f", averageTime/jobs3.size()));
                    run = false;
                    return;
                } else {
                    System.out.println("时间：" + clock.getTime() + "  无进程可调度");
                    return;
                }
            }
            //todo System.out.println("时间：" + clock.getTime() + "  正在执行：" + nowProcess.toString());
            nowProcess.setTime(nowProcess.getTime() + 1);
            //nowProcess.setTime(clock.getTime());
            if (nowProcess.getTime() == nowProcess.getServiceTime()) {
                //已经执行完毕
                nowProcess.setFinishTime(clock.getTime()+1);
                nowProcess.setRoundTime(nowProcess.getFinishTime() - nowProcess.getSubmitTime());
                nowProcess.setAveRoundTime((double) nowProcess.getRoundTime() / nowProcess.getServiceTime());
                nowProcess.setStatus('F');
                //System.out.println("\n执行完毕：" + nowProcess.printResult());//所在块总是0
                System.out.println("\n" + nowProcess.getName()+"完成,释放处理机以及相关资源");
                //释放空间的占用
                integerJobHashMap.remove(nowProcess.getID());
                nowProcess.setTapeGet(false);
                tapes += nowProcess.getTapeNeeded();

                partitionNode.freeAllocation(nowProcess.getID());
                partitionNode.displayAllocation();
                partitionNode.allocateResult=1;

//                Block block = integerBlockHashMap.get(nowProcess.getBlockID());
//                Queue<Integer> queue = block.getJobIDs();
//                queue.remove(nowProcess.getID());
//                block.setJobIDs(queue);
//                //todo 这里移动了作业!
//                block.setRemainSize(block.getRemainSize() + nowProcess.getSize());
//                integerBlockHashMap.put(block.getID(), block);
                for (int j = 0; j <= jobs1.size() - 1; j++) {
                    if (jobs1.get(j).getID() == nowProcess.getID()) {
                        jobs1.remove(j);//jobs2已经remove过了?
                    }
                }
                jobs3.add(nowProcess);
                nowProcess = null;
                //todo 关键在这里,当前进程run完调用
                processScheduling();

            }
        }

        /**
         * 作业调度 short job fist
         */
        private void SJF() {
for(int j=0;j<jobs.size();j++){
    System.out.println("jobs before sort!!!!!"+jobs.get(j).getName());
}
            jobs.sort((o1, o2) -> o1.getServiceTime() - o2.getServiceTime());

for(int j=0;j<jobs.size();j++){
    System.out.println("jobs after sort!!!!!"+jobs.get(j).getName());
}
            for (int i = 0; i <= jobs.size() - 1; i++) {
                nowJob = jobs.remove(0);
                jobs1.add(nowJob);

for(int j=0;j<jobs1.size();j++){
    System.out.println("jobs1 before sort!!!!!"+jobs1.get(j).getName());
}
                jobs1.sort((o1, o2) -> o1.getServiceTime() - o2.getServiceTime());

for(int j=0;j<jobs1.size();j++){
    System.out.println("jobs1 after sort!!!!!"+jobs1.get(j).getName());
}
                //尝试分配
                integerJobHashMap.put(nowJob.getID(), nowJob);
                processScheduling();
            }
            nowJob = null;
        }
        /**
         * 作业调度 first come first serve
         */
        private void FCFS() {
            jobs.sort((o1, o2) -> o1.getSubmitTime() - o2.getSubmitTime());
            for (int i = 0; i <= jobs.size() - 1; i++) {
                nowJob = jobs.remove(0);
                jobs1.add(nowJob);
                //尝试分配
                integerJobHashMap.put(nowJob.getID(), nowJob);
                processScheduling();
            }
            nowJob = null;
        }
        /**
         * 作业调度 (priority-scheduling algorithm)
         */
        private void PSA() {
            jobs.sort((o1, o2) -> o1.getDegree() - o2.getDegree());
            for (int i = 0; i <= jobs.size() - 1; i++) {
                nowJob = jobs.remove(0);
                jobs1.add(nowJob);
                //尝试分配
                integerJobHashMap.put(nowJob.getID(), nowJob);
                processScheduling();
            }
            nowJob = null;
        }
        /**
         * 作业调度 (Highest Response Ratio Next)
         */
        private void HRRN() {
            jobs.sort((o1, o2) -> (clock.getTime()-o1.getSubmitTime()+o1.getServiceTime())/o1.getServiceTime() - 
                    (clock.getTime()-o2.getSubmitTime()+o2.getServiceTime())/o2.getServiceTime());
            for (int i = 0; i <= jobs.size() - 1; i++) {
                nowJob = jobs.remove(0);
                jobs1.add(nowJob);
                //尝试分配
                integerJobHashMap.put(nowJob.getID(), nowJob);
                processScheduling();
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
            if(jobChoice==1){
                PSA();
            } else if(jobChoice==2){
                FCFS();
            } else if(jobChoice==3){
                HRRN();
            }
            else {
                SJF();
            }

            processRunning1Second();
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
    public void processScheduling() {
        //processRunning1Second当前jobs(后备)还有作业入才能了jobHashMap  &&  integerBlockHashMap是只有当前run进程结束才入的

        if (integerJobHashMap.size() >= 1 && partitionNode.allocateResult >= 1) {
            //按作业调度顺序来分配
            int jobID;
            if(nowProcess==null &&clock.getTime()!=0){
                System.out.println("********************当前时间: "+JOB.timeFormat(clock.getTime()+1)+"********************");
            }
            else{
                System.out.println("********************当前时间: "+JOB.timeFormat(clock.getTime())+"********************");
            }

            int biggestRemainSize=0;
            partitionNode temp;
            for (temp=partitionNode.headNode;temp!=null;temp=temp.next) {
                if(temp.processID<=0){
                biggestRemainSize=biggestRemainSize> temp.size?biggestRemainSize:temp.size;
                }
            }
            System.out.println("目前最大空闲内存块大小: "+biggestRemainSize+" 剩余磁带数: "+tapes);


            for (int i = 0; i <= jobs1.size() - 1; i++) {
                jobID = jobs1.get(i).getID();
                if (integerJobHashMap.get(jobID) != null &&
                        integerJobHashMap.get(jobID).getState() == 0 &&
                        !integerJobHashMap.get(jobID).isTapeGet()) {

                    JOB job = integerJobHashMap.get(jobID);
                    System.out.print("选中作业：" + job.getName()+" ");
/*                    //找到最佳适应的块
                    int bestFitBlockID = -1;
                    for (int blockID = 0; blockID <= integerBlockHashMap.size() - 1; blockID++) {
                        Block block = integerBlockHashMap.get(blockID);
                        if (block.getRemainSize() - job.getSize() >= 0) {
                            if (bestFitBlockID == -1) {
                                bestFitBlockID = blockID;
                            }
                            else if (block.getRemainSize() <= integerBlockHashMap.get(bestFitBlockID).getRemainSize()) {
                                bestFitBlockID = blockID;
                            }
                        }
                    }*/
                    //找到首次适应的块
                    int bestFitBlockID = -1;
//                    for (int blockID = 0; blockID <= integerBlockHashMap.size() - 1; blockID++) {
//                        block = integerBlockHashMap.get(blockID);
//                        if (block.getRemainSize() - job.getSize() >= 0) {
//                                bestFitBlockID = blockID;break;
//                        }
//                    }
                    //操作部分
                    if ( job.getTapeNeeded() <= tapes){
                        partitionNode.allocateResult=partitionNode.firstFitAllocation(job.getID(),job.getSize());
                    }
                    if ( partitionNode.allocateResult>=1 && job.getTapeNeeded() <= tapes) {
//                        block = integerBlockHashMap.get(bestFitBlockID);
//                        block.setRemainSize(block.getRemainSize() - job.getSize());
//                        block.addaJobID(jobID);
                        //todo 成功分配到全部资源



                        job.setArriveReadyTime(clock.getTime());
                        //job.setReplacedTime(job.getArriveReadyTime());

                        job.setState(1);
                        //todo bestFitBlockID有问题?
                        job.setBlockID(bestFitBlockID);
                        job.setTapeGet(true);
                        tapes -= job.getTapeNeeded();
//                        integerBlockHashMap.put(bestFitBlockID, block);
                        integerJobHashMap.put(jobID, job);
                        System.out.println("作业调度【成功】");

                        partitionNode.displayAllocation();
                        //System.out.println("作业调度【成功】：" + job.printNeed());

                        biggestRemainSize=0;//原来bug在这里
                        for (temp=partitionNode.headNode;temp!=null;temp=temp.next) {
                            if(temp.processID<=0){
                                biggestRemainSize=biggestRemainSize> temp.size?biggestRemainSize:temp.size;
                            }
                        }
                        System.out.println("目前最大空闲内存块大小: "+biggestRemainSize+" 剩余磁带数: "+tapes);

                        //抢占(排好队等待调度)//todo 改这里看看 job抢占了nowProcess jobs2.add
                        if(processChoice==1){//PSA
                            if (nowProcess != null && job.getDegree() < nowProcess.getDegree()) {
                                nowProcess.setStatus('W');
                                //nowProcess.setReplacedTime(clock.getTime());
                                jobs2.add(0, nowProcess);
                                jobs2.add(0, job);
                                nowProcess = null;
                                System.out.println(job.getName()+"【抢占成功】");
                                //System.out.println("抢占成功" + job.printNeed());
                            } else {
                                jobs2.add(job);
                                jobs2.sort((o1, o2) -> o1.getDegree() - o2.getDegree());
                            }
                        }
                        else if(processChoice==2){//FCFS
                            if (nowProcess != null && job.getArriveReadyTime() < nowProcess.getArriveReadyTime()) {
                                nowProcess.setStatus('W');
                                //nowProcess.setReplacedTime(clock.getTime());
                                jobs2.add(0, nowProcess);
                                jobs2.add(0, job);
                                nowProcess = null;
                                System.out.println(job.getName()+"【抢占成功】");
                            } else {
                                jobs2.add(job);
                                jobs2.sort((o1, o2) -> o1.getArriveReadyTime() - o2.getArriveReadyTime());
                            }
                        }
/*                        else if(processChoice==3){//HRRN
                            if (nowProcess != null && ( clock.getTime()-job.getReplacedTime()+(job.getServiceTime()-job.getTime()) )/(job.getServiceTime()-job.getTime())
                                    < ( clock.getTime()-nowProcess.getReplacedTime()+(nowProcess.getServiceTime()-nowProcess.getTime()) )/(nowProcess.getServiceTime()-nowProcess.getTime())) {
//                                nowProcess.setStatus('W');
//                                nowProcess.setReplacedTime(clock.getTime());
//                                jobs2.add(0, nowProcess);
//                                jobs2.add(0, job);
//                                nowProcess = null;
                                System.out.println("时间：" + clock.getTime() + "此方法是非抢占的!" + job);
                            }
//                            else {
                                job.setReplacedTime(clock.getTime());
                                jobs2.add(job);
                                jobs2.sort((o1, o2) ->  ( clock.getTime()-o1.getReplacedTime()+(o1.getServiceTime()-o1.getTime()) )/(o1.getServiceTime()-o1.getTime())
                                        - (clock.getTime()-o2.getReplacedTime()+(o2.getServiceTime()-o2.getTime()) )/(o2.getServiceTime()-o2.getTime()));
                            //}
                        }*/
                        else {//S*F
                            if (nowProcess != null && job.getServiceTime() -job.getTime()< nowProcess.getServiceTime() - nowProcess.getTime()) {
                                nowProcess.setStatus('W');
                                //nowProcess.setReplacedTime(clock.getTime());
                                jobs2.add(0, nowProcess);
                                jobs2.add(0, job);
                                nowProcess = null;
                                System.out.println(job.getName()+"【抢占成功】");
                                //System.out.println("抢占成功" + job.printNeed());
                            } else {
                                //job.setReplacedTime(clock.getTime());
                                jobs2.add(job);
                                jobs2.sort((o1, o2) -> o1.getServiceTime()-o1.getTime() - o2.getServiceTime()-o2.getTime());
                            }
                        }
                    } else {
                        System.out.println("作业调度失败");
                    }
                }
            }
        }
    }
    /**
     * 加入块
     *
     * @param //block
     * @return
     */
//    public boolean addBlock(Block block) {
//        if (block == null) return false;
//        if (integerBlockHashMap.size() == 0) {
//            block.setStartAddress(0);
//            block.setEndAddress(block.getSize());
//        } else {
//            block.setStartAddress(integerBlockHashMap.get(integerBlockHashMap.size() - 1).getEndAddress() + 1);
//            block.setEndAddress(block.getStartAddress() + block.getSize() - 1);
//        }
//        block.setID(integerBlockHashMap.size());
//        integerBlockHashMap.put(block.getID(), block);
//        return true;
//    }
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("输入1是PSA(静态priority-scheduling algorithm),2是FCFS,3是HRRN(高响应比优先High Respond Radio Next,非抢占)!其他都是S*F(Short First)");
        System.out.println("先输入job的算法!");
        try{
            jobChoice=Integer.parseInt(bufferedReader.readLine());
        } catch (Exception e){
            jobChoice=4;
        }
        System.out.println("再输入process的算法!");
        try{
            processChoice=Integer.parseInt(bufferedReader.readLine());
        } catch (Exception e){
            processChoice=4;
        }
        System.out.println("jobChoice= "+jobChoice+" "+"processChoice= "+processChoice);
        new Scheduling().run();
    }
}
