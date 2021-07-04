import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class EveryMinute {
    public static int milliOf1Minute = 1;//毫秒
    private int relativeTime;//当前时间
    private boolean occurCPU;//是否有进程在运行
    public static void Sleep(int relativeTime) {
        if (relativeTime >= 0) {
            try {
                Thread.sleep(relativeTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public EveryMinute(){
        occurCPU = true;
    }
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private PropertyChangeSupport changeSupport2 = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }
    public int getTime() {
        return relativeTime;
    }
    public void setTime(int relativeTime) {
        this.relativeTime = relativeTime;
    }
    public void goOneTime(){
        if(occurCPU) {
            //todo 有点像配置文件
            changeSupport.firePropertyChange("relativeTime", getTime(), getTime() + 1);
            changeSupport2.firePropertyChange("relativeTime",getTime(),getTime()+1);
            setTime(getTime() + 1);
        }
    }
}

