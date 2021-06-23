package Tool;

public class ToolForSch {
    public static final int timeNeededMax = 60;
    public static int unitOfTime = 1;//100;//毫秒
    public static final int renewTime  = 200;//毫秒
    public static final int jobToPro = 100;//作业转化为进程
    public static int tapesTol = 4;//磁带机总数
    public static int memoryTol = 100;//总的块大小
    public static void Sleep(int time) {
        if (time >= 0) {
            try {
                Thread.sleep(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
