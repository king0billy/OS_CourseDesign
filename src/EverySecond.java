import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class EverySecond {
    public static int unitOfTime = 1;//100;//毫秒
    public static int tapesTol = 4;//磁带机总数
    private int time;
    private boolean run;
    public static void Sleep(int time) {
        if (time >= 0) {
            try {
                Thread.sleep(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public EverySecond(){
        run = true;
    }
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private PropertyChangeSupport changeSupport2 = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public void goOneTime(){
        if(run) {
            //todo 有点像配置文件
            changeSupport.firePropertyChange("time", getTime(), getTime() + 1);
            changeSupport2.firePropertyChange("time",getTime(),getTime()+1);
            setTime(getTime() + 1);
        }
    }
    static class TimeChangeListener implements PropertyChangeListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println(evt.getNewValue());
        }
    }
    public static void main(String[] args) {
        EverySecond everySecond = new EverySecond();
        everySecond.addPropertyChangeListener(new TimeChangeListener());
        while (true) {
            EverySecond.Sleep(EverySecond.unitOfTime);
            everySecond.goOneTime();
        }
    }
}

