import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

public class MainOperate {
    public List<JOB> inputWell = new ArrayList<>();//输入井,暂存预输入数据
    public List<JOB> bufferedJobs = new ArrayList<>(); //缓冲队列,预防同一时刻有多个作业同时进入输入井
    public List<JOB> sortedJobs = new ArrayList<>();//排序好的作业调度队列,按算法顺序从头到尾尝试作业调度
    public List<JOB> readyProcesses = new ArrayList<>();//成功分配到内存和磁带的就绪队列(在内存中)
    public int tapes=4;//磁带机总数
    public List<JOB> DoneJobs = new ArrayList<>(); //最终运行结果
    public JOB nowJob;//当前的作业
    public JOB runningProcess;//当前占用单核cpu运行中的进程
    public EveryMinute everySecond = new EveryMinute();//系统时间
    public int idCount = 0;//用于输入井输入预设数据的计数器
    public boolean occurCPU = false;
    public static int totalNumber=0;
    public static int jobChoice=0;
    public static int processChoice=0;

    public MainOperate() {
        init();
    }

    private void init() {
        BlockNode.headNode=BlockNode.initPartition();
        tapes = 4;
        TestData();
        totalNumber=inputWell.size();
        for(int i=0;i< inputWell.size();i++){
            System.out.println(inputWell.get(i).printNeed());
        }
        everySecond.addPropertyChangeListener(new TimerListener());
    }

    private void TestData() {
/*        addInputWell(new JOB("JOB1", 0, 25, 15, 2));
        addInputWell(new JOB("JOB2", 20, 30, 60, 1));
        addInputWell(new JOB("JOB3", 30, 10, 50, 3));
        addInputWell(new JOB("JOB4", 35, 20, 10, 2));
        addInputWell(new JOB("JOB5", 40, 15, 30, 2));*/
        //第一组测试数据
/*        addInputWell(new JOB("JOB1", 0, 25, 15, 2,211));
        addInputWell(new JOB("JOB2", 20, 30, 60, 1,222));
        addInputWell(new JOB("JOB3", 30, 10, 50, 3,251));
        addInputWell(new JOB("JOB4", 35, 20, 10, 2,255));
        addInputWell(new JOB("JOB5", 40, 15, 30, 2,0));*/
        //第二组测试数据
        addInputWell(new JOB("JOB1", 0, 25, 15, 2,211));
        addInputWell(new JOB("JOB2", 20, 30, 60, 1,222));
        addInputWell(new JOB("JOB3", 30, 10, 10, 1,251));
        addInputWell(new JOB("JOB4", 35, 20, 30, 3,255));
        addInputWell(new JOB("JOB5", 40, 15, 30, 2,0));
    }
    public void occurCPU() {
        TimerThread timerThread = new TimerThread() ;
        timerThread.start();
    }
    /**计时器*/
    class TimerThread extends Thread {
        public void occurCPU() {
            occurCPU = true;
            while (occurCPU) {
                EveryMinute.Sleep(EveryMinute.milliOf1Minute);
                everySecond.goOneTime();
            }
        }
    }

    /**时间每变化一个单位*/
    private class TimerListener implements PropertyChangeListener {
        /**进程执行一个时间单位*/
        private void processRunning1Second() {
            if (runningProcess == null) {
                if (readyProcesses.size() != 0) {
                    if(processChoice==3){ //进程高响应比的ReplacedTime换成ArriveReadyTime因为是非抢占 HRRN
                        System.out.println("进程调度高响应比");
                        readyProcesses.sort((o1, o2) ->  ( everySecond.getTime()-o1.getArriveReadyTime()+(o1.getServiceTime()-o1.getTime()) )/(o1.getServiceTime()-o1.getTime())
                                - (everySecond.getTime()-o2.getArriveReadyTime()+(o2.getServiceTime()-o2.getTime()) )/(o2.getServiceTime()-o2.getTime()));
                    }
                        runningProcess = readyProcesses.remove(0);
                    runningProcess.setStatus('R');
                    System.out.println("进程调度选择了:" + runningProcess.getName()+"\n");
                } else if ( inputWell.size() == 0&&totalNumber==DoneJobs.size()) {//integerJobHashMap.size() == 0 &&
                    System.out.println("********************当前时间: "+JOB.timeFormat(everySecond.getTime())+"********************");
                    System.out.println("任务完成"+"\n");
                    System.out.println("********************最终统计数据********************");
                    DoneJobs.sort(((o1, o2) ->o1.getID()-o2.getID()));
                    double averageTime=0.0f;
                    for(int i=0;i<=DoneJobs.size()-1;i++){
                        System.out.println(DoneJobs.get(i).printResult());
                        averageTime+=DoneJobs.get(i).getAveRoundTime();
                    }
                    System.out.println("平均带权周转时间= " +String.format("%.2f", averageTime/DoneJobs.size()));
                    occurCPU = false;
                    return;
                } else {
                    System.out.println("时间：" + everySecond.getTime() + "  无进程可调度");
                    return;
                }
            }
            runningProcess.setTime(runningProcess.getTime() + 1);
            //runningProcess.setTime(everySecond.getTime());
            if (runningProcess.getTime() == runningProcess.getServiceTime()) {
                //已经执行完毕
                runningProcess.setFinishTime(everySecond.getTime()+1);
                runningProcess.setRoundTime(runningProcess.getFinishTime() - runningProcess.getSubmitTime());
                runningProcess.setAveRoundTime((double) runningProcess.getRoundTime() / runningProcess.getServiceTime());
                runningProcess.setStatus('F');
                System.out.println("\n" + runningProcess.getName()+"完成,释放处理机以及相关资源");
                //释放空间的占用
                runningProcess.setTapeGet(false);
                tapes += runningProcess.getTapeNeeded();

                BlockNode.freeAllocation(runningProcess.getID());
                BlockNode.displayAllocation();
                BlockNode.allocateResult=1;

                DoneJobs.add(runningProcess);
                runningProcess = null;
                //todo 关键在这里,当前进程run完调用
                processScheduling();

            }
        }

        /**作业调度 short job fist */
        private void SJF() {
            for (int i = 0; i <= bufferedJobs.size() - 1; i++) {
                nowJob = bufferedJobs.remove(0);
                sortedJobs.add(nowJob);
                sortedJobs.sort((o1, o2) -> o1.getServiceTime() - o2.getServiceTime());
                processScheduling();
            }
            nowJob = null;
        }
        /** 作业调度 first come first serve*/
        private void FCFS() {
            for (int i = 0; i <= bufferedJobs.size() - 1; i++) {
                nowJob = bufferedJobs.remove(0);
                sortedJobs.add(nowJob);

                sortedJobs.sort((o1, o2) -> o1.getSubmitTime() - o2.getSubmitTime());

                //尝试分配
                processScheduling();
            }
            nowJob = null;
        }
        /** 作业调度 (priority-scheduling algorithm)*/
        private void PSA() {
            for (int i = 0; i <= bufferedJobs.size() - 1; i++) {
                nowJob = bufferedJobs.remove(0);
                sortedJobs.add(nowJob);
                //尝试分配
                sortedJobs.sort((o1, o2) -> o1.getDegree() - o2.getDegree());
                processScheduling();
            }
            nowJob = null;
        }
        /** 作业调度 (Highest Response Ratio Next)*/
        private void HRRN() {
            for (int i = 0; i <= bufferedJobs.size() - 1; i++) {
                nowJob = bufferedJobs.remove(0);
                sortedJobs.add(nowJob);
                //尝试分配
                sortedJobs.sort((o1, o2) -> (everySecond.getTime()-o1.getSubmitTime()+o1.getServiceTime())/o1.getServiceTime() -
                        (everySecond.getTime()-o2.getSubmitTime()+o2.getServiceTime())/o2.getServiceTime());
                processScheduling();
            }
            nowJob = null;
        }
        /** 输入井中有新作业*/
        private void jobArrive() {
            for (int i = 0; i <= inputWell.size() - 1; i++) {
                if (inputWell.get(i).getSubmitTime() == everySecond.getTime()) {
                    bufferedJobs.add(inputWell.remove(i));
                    i--;
                }
            }
        }
        /** 监视器监视时间变更*/
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


    /**预设作业*/
    public void addInputWell(JOB job) {
        job.setID(idCount++);
        inputWell.add(job);
    }
    /** 进程调度*/
    public void processScheduling() {
        if ( sortedJobs.size()>=1 && BlockNode.allocateResult >= 1) {
            //按作业调度顺序来分配
            if(runningProcess==null &&everySecond.getTime()!=0){
                System.out.println("********************当前时间: "+JOB.timeFormat(everySecond.getTime()+1)+"********************");
            }
            else{
                System.out.println("********************当前时间: "+JOB.timeFormat(everySecond.getTime())+"********************");
            }

            int biggestRemainSize=0;
            BlockNode temp;
            for (temp=BlockNode.headNode;temp!=null;temp=temp.next) {
                if(temp.processID<=0){
                biggestRemainSize=biggestRemainSize> temp.size?biggestRemainSize:temp.size;
                }
            }
            System.out.println("目前最大空闲内存块大小: "+biggestRemainSize+" 剩余磁带数: "+tapes);


            for (int i = 0; i <= sortedJobs.size() - 1; i++) {
                if (! sortedJobs.get(i).isTapeGet()) {

                    JOB job = sortedJobs.get(i);
                    System.out.print("选中作业：" + job.getName()+" ");
                    if ( job.getTapeNeeded() <= tapes){
                        BlockNode.allocateResult=BlockNode.firstFitAllocation(job.getID(),job.getSize());
                    }
                    if ( BlockNode.allocateResult>=1 && job.getTapeNeeded() <= tapes) {
                        //todo 成功分配到全部资源
                        sortedJobs.remove(i);

                        job.setArriveReadyTime(everySecond.getTime());
                        job.setTapeGet(true);
                        tapes -= job.getTapeNeeded();
                        System.out.println("作业调度【成功】");

                        BlockNode.displayAllocation();

                        biggestRemainSize=0;//原来bug在这里
                        for (temp=BlockNode.headNode;temp!=null;temp=temp.next) {
                            if(temp.processID<=0){
                                biggestRemainSize=biggestRemainSize> temp.size?biggestRemainSize:temp.size;
                            }
                        }
                        System.out.println("目前最大空闲内存块大小: "+biggestRemainSize+" 剩余磁带数: "+tapes);

                        //抢占(排好队等待调度)//todo 改这里看看 job抢占了nowProcess readyProcesses.add
                        if(processChoice==1){//PSA
                            if (runningProcess != null && job.getDegree() < runningProcess.getDegree()) {
                                runningProcess.setStatus('W');
                                readyProcesses.add(0, runningProcess);
                                readyProcesses.add(0, job);
                                runningProcess = null;
                                System.out.println(job.getName()+"【抢占成功】");
                            } else {
                                readyProcesses.add(job);
                                readyProcesses.sort((o1, o2) -> o1.getDegree() - o2.getDegree());
                            }
                        }
                        else if(processChoice==2){//FCFS
                            if (runningProcess != null && job.getArriveReadyTime() < runningProcess.getArriveReadyTime()) {
                                runningProcess.setStatus('W');
                                readyProcesses.add(0, runningProcess);
                                readyProcesses.add(0, job);
                                runningProcess = null;
                                System.out.println(job.getName()+"【抢占成功】");
                            } else {
                                readyProcesses.add(job);
                                readyProcesses.sort((o1, o2) -> o1.getArriveReadyTime() - o2.getArriveReadyTime());
                            }
                        }
                        else if(processChoice==3){//HRRN
                                readyProcesses.add(job);
                        }
                        else {//S*F
                            if (runningProcess != null && job.getServiceTime() -job.getTime()< runningProcess.getServiceTime() - runningProcess.getTime()) {
                                runningProcess.setStatus('W');
                                readyProcesses.add(0, runningProcess);
                                readyProcesses.add(0, job);
                                runningProcess = null;
                                System.out.println(job.getName()+"【抢占成功】");
                            } else {
                                readyProcesses.add(job);
                                readyProcesses.sort((o1, o2) -> o1.getServiceTime()-o1.getTime() - o2.getServiceTime()-o2.getTime());
                            }
                        }
                    } else {
                        System.out.println("作业调度失败");
                    }
                }
            }
        }
    }
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
        new MainOperate().occurCPU();
    }
}
