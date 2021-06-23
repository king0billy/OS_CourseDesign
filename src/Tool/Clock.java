package Tool;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Clock {
    private int time;
    private boolean run;

    public Clock(){
        run = true;
    }

    public void stop(){
        run = false;
    }

    public void start(){
        run = true;
    }

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private PropertyChangeSupport changeSupport2 = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener2(PropertyChangeListener listener) {
        changeSupport2.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void goOneTime(){
        if(run) {
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
        Clock clock = new Clock();
        clock.addPropertyChangeListener(new TimeChangeListener());
        while (true) {
            ToolForSch.Sleep(ToolForSch.unitOfTime);
            clock.goOneTime();
        }
    }
}

