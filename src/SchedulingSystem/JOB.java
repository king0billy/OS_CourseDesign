package SchedulingSystem;

import java.io.Serializable;

public class JOB implements Comparable<JOB>, Cloneable, Serializable {
    private int ID;
    private int size;
    private int blockID;
    private String name;
    private int tapeNeeded;
    private boolean tapeGet;
    private int state;
    private int serviceTime;  //服务所需要的时间
    private int submitTime;    //提交时间
    private int finishTime;   //结束服务时间
    private int time;          //已服务时间
    private int roundTime;    //周转时间
    private double aveRoundTime; //带权周转时间
    private int source;        //所需资源
    private char status;      //当前状态  W代表就绪 R代表正在运行 F代表完成
    private int degree;        //优先级
    private double hrrfRate;   //最高响应比


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public JOB() {
    }

    public JOB(String name, int arrive, int serviceTime, int size, int tape) {
        setName(name);
        setServiceTime(serviceTime);
        setSubmitTime(arrive);
        setSize(size);
        setTapeNeeded(tape);
        setBlockID(-1);
        setStatus('W');
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        JOB job = null;
        job = (JOB) super.clone();
        return job;
    }

    public int compareTo(JOB o) {
        if (this.degree < o.degree) {
            return -1;
        } else if (this.degree == o.degree) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getTapeNeeded() {
        return tapeNeeded;
    }

    public void setTapeNeeded(int tapeNeeded) {
        this.tapeNeeded = tapeNeeded;
    }

    public boolean isTapeGet() {
        return tapeGet;
    }

    public void setTapeGet(boolean tapeGet) {
        this.tapeGet = tapeGet;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
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
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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

    @Override
    public String toString() {
        return "ID：" + getID() + "|名字：" + getName() + "|提交时间：" + getSubmitTime() +
                "|结束时间：" + getFinishTime() + "|运行时间：" + getTime() +
                "|周转时间：" + getRoundTime() + "|带权周转时间：" + getAveRoundTime() +
                "|状态：" + getStatus() + "|服务时间：" + getServiceTime() + "|所在的块" + getBlockID()+
                "|所需块大小"+getSize()+"|磁带机需求"+getTapeNeeded()+"|磁带机分配情况"+isTapeGet();

    }
    public Object[] toArray(){
        return new Object[]{
                getID(),getName(),getSubmitTime(),getServiceTime(),getTime(),getFinishTime(),getRoundTime(),
                getAveRoundTime(),getStatus(),getState(),getSize(),getBlockID(),getTapeNeeded(),isTapeGet()
        };
    }
}
