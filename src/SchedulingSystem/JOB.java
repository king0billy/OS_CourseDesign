package SchedulingSystem;

import java.io.Serializable;

public class JOB implements Comparable<JOB>, Cloneable, Serializable {
    private int ID;
    private int needSize;
    private int blockID;
    private String name;
    private int needTape;
    private boolean tapeGet;
    private int state;
    private int serviceTime;  //服务所需要的时间
    private int submitTime;    //提交时间
    private int finishTime;   //结束服务时间
    private int runTime=0;          //已服务时间
    private int roundTime;    //周转时间
    private double aveRoundTime; //带权周转时间
    private int source;        //所需磁带机资源
    private char status;      //当前状态  W代表就绪 R代表正在运行 F代表完成
    private int priority;        //优先级//没有用
    private double hrrfRate;   //最高响应比//没有用

    private int arriveReadyTime=99999999;
/*    private int replacedTime=9999;

    public int getReplacedTime() {
        return replacedTime;
    }

    public void setReplacedTime(int replacedTime) {
        this.replacedTime = replacedTime;
    }*/

    public int getArriveReadyTime() {
        return arriveReadyTime;
    }

    public void setArriveReadyTime(int arriveReadyTime) {
        this.arriveReadyTime = arriveReadyTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public JOB() {
    }

    public JOB(String name, int arriveTime, int serviceTime, int needSize, int needTape) {
        setName(name);
        setServiceTime(serviceTime);
        setSubmitTime(arriveTime);
        setSize(needSize);
        setTapeNeeded(needTape);
        setBlockID(-1);
        setStatus('W');
    }
    public JOB(String name, int arriveTime, int serviceTime, int needSize, int needTape, int priority) {
        this(name,arriveTime,serviceTime,needSize,needTape);
        this.setDegree(priority);
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        JOB job = null;
        job = (JOB) super.clone();
        return job;
    }

    public int compareTo(JOB o) {
        if (this.priority < o.priority) {
            return -1;
        } else if (this.priority == o.priority) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getTapeNeeded() {
        return needTape;
    }

    public void setTapeNeeded(int needTape) {
        this.needTape = needTape;
    }

    public boolean isTapeGet() {
        return tapeGet;
    }

    public void setTapeGet(boolean tapeGet) {
        this.tapeGet = tapeGet;
    }

    public int getDegree() {
        return priority;
    }

    public void setDegree(int priority) {
        this.priority = priority;
    }

    public double getHrrfRate() {
        return hrrfRate;
    }

    public void setHrrfRate(double hrrfRate) {
        this.hrrfRate = hrrfRate;
    }

    public boolean isFirestRun() {
        return firestRun;
    }

    public void setFirestRun(boolean firestRun) {
        this.firestRun = firestRun;
    }

    public boolean firestRun; //是否第一次运行

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSize() {
        return needSize;
    }

    public void setSize(int needSize) {
        this.needSize = needSize;
    }


    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(int submitTime) {
        this.submitTime = submitTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getTime() {
        return runTime;
    }

    public void setTime(int runTime) {
        this.runTime = runTime;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
    }

    public double getAveRoundTime() {
        return aveRoundTime;
    }

    public void setAveRoundTime(double aveRoundTime) {
        this.aveRoundTime = aveRoundTime;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }
/*    public String printNeed() {
        return "" + getName() + "  提交时间：" + getSubmitTime() +
                "\t  运行时间：" + getTime() +
                "  状态：" + getStatus() + "  服务时间：" + getServiceTime() +
                "  所需块大小"+getSize()+"  磁带机需求"+getTapeNeeded()+"  磁带机分配情况"+isTapeGet();

    }
    public String printResult() {
        return "" + getName() + "  提交时间:" + getSubmitTime() +
                "\t  结束时间：" + getFinishTime() + "  运行时间：" + getTime() +
                "  周转时间：" + getRoundTime() + "  带权周转时间：" + String.format("%.2f", getAveRoundTime()) +
                "  服务时间：" + getServiceTime() +
                "  所需块大小"+getSize()+"  磁带机需求"+getTapeNeeded();
    }*/
public String printNeed() {
    return "" + getName() + "  提交时间：" + timeFormat(getSubmitTime()) +
            "\t  运行时间：" + getTime() +
            "  状态：" + getStatus() + "\t  所需服务时间：" + getServiceTime() +
            "  所需块大小"+getSize()+"  磁带机需求"+getTapeNeeded();

}
    public String printResult() {
        return "" + getName() + "  提交时间:" + timeFormat(getSubmitTime()) +
                "\t  结束时间：" + timeFormat(getFinishTime()) + "  运行时间：" + getTime() +
                "  周转时间：" + getRoundTime() + "  带权周转时间：" + String.format("%.2f", getAveRoundTime()) +
                "\t  所需服务时间：" + getServiceTime() +
                "  所需块大小"+getSize()+"  磁带机需求"+getTapeNeeded();//+"  优先级数"+getDegree();
    }
    public static String timeFormat( int runTime){
        int hour=10;int min=0;
        hour+=runTime/60;min=runTime%60;
        return hour+":"+String.format("%02d",min);
    }
    @Override
    public String toString() {
        return "ID：" + getID() + "  名字：" + getName() + "  提交时间：" + getSubmitTime() +
                "\t  结束时间：" + getFinishTime() + "  运行时间：" + getTime() +
                "  周转时间：" + getRoundTime() + "  带权周转时间：" + String.format("%.2f", getAveRoundTime()) +
                "  状态：" + getStatus() + "  服务时间：" + getServiceTime() + "  所在的块" + getBlockID()+
                "  所需块大小"+getSize()+"  磁带机需求"+getTapeNeeded()+"  磁带机分配情况"+isTapeGet();

    }
    public Object[] toArray(){
        return new Object[]{
                getID(),getName(),getSubmitTime(),getServiceTime(),getTime(),getFinishTime(),getRoundTime(),
                getAveRoundTime(),getStatus(),getState(),getSize(),getBlockID(),getTapeNeeded(),isTapeGet()
        };
    }
}
