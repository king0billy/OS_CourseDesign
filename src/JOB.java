public class JOB {//implements Comparable<JOB>, Cloneable, Serializable
    private int ID;//作业id
    private int needSize; //需要的磁带数
    private String name;//作业名字
    private int needTape;//需要的磁带数
    private boolean isOccurTape; //表示当前进程是否分配到磁带机
    private int needTime;  //服务所需要的时间
    private int submitTime;    //提交时间
    private int finishTime;   //结束服务时间
    private int runTime=0;          //已服务时间
    private int roundTime;    //周转时间
    private double averageRoundTime; //带权周转时间
    private char status;      //当前状态  W代表waiting R代表running F代表finished
    private int priority;        //优先级
    private int arriveReadyTime=99999999;//成功作业调度进入内存的时间

    public int getArriveReadyTime() {
        return arriveReadyTime;
    }

    public void setArriveReadyTime(int arriveReadyTime) {
        this.arriveReadyTime = arriveReadyTime;
    }

    public JOB() {
    }

    public JOB(String name, int arriveTime, int needTime, int needSize, int needTape) {
        setName(name);
        setServiceTime(needTime);
        setSubmitTime(arriveTime);
        setSize(needSize);
        setTapeNeeded(needTape);
        setStatus('W');
    }
    public JOB(String name, int arriveTime, int needTime, int needSize, int needTape, int priority) {
        this(name,arriveTime,needTime,needSize,needTape);
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
        return isOccurTape;
    }

    public void setTapeGet(boolean isOccurTape) {
        this.isOccurTape = isOccurTape;
    }

    public int getDegree() {
        return priority;
    }

    public void setDegree(int priority) {
        this.priority = priority;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServiceTime() {
        return needTime;
    }

    public void setServiceTime(int needTime) {
        this.needTime = needTime;
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
        return averageRoundTime;
    }

    public void setAveRoundTime(double averageRoundTime) {
        this.averageRoundTime = averageRoundTime;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

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
//    @Override
//    public String toString() {
//        return "ID：" + getID() + "  名字：" + getName() + "  提交时间：" + getSubmitTime() +
//                "\t  结束时间：" + getFinishTime() + "  运行时间：" + getTime() +
//                "  周转时间：" + getRoundTime() + "  带权周转时间：" + String.format("%.2f", getAveRoundTime()) +
//                "  状态：" + getStatus() + "  服务时间：" + getServiceTime() + //"  所在的块" + getBlockID()+
//                "  所需块大小"+getSize()+"  磁带机需求"+getTapeNeeded()+"  磁带机分配情况"+isTapeGet();
//
//    }
}
