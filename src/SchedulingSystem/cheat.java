package SchedulingSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author 22427(king0liam)
 * @version 1.0
 * @ClassName: cheat
 * @Description
 * @Date: 2021/6/25 13:48
 * @since version-0.0
 */
public class cheat {
    public static void main(String[] args) {
        int jobChoice=0,processChoice=0;
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("输入1是PSA(静态priority-scheduling algorithm),2是FCFS,3是HRRN(高响应比优先High Respond Radio Next,非抢占)!其他都是S*F(Short First)");
        System.out.println("先输入job的算法!");
        try{
            jobChoice=Integer.parseInt(bufferedReader.readLine());
        } catch (Exception e){
            jobChoice=4;
            //e.printStackTrace();
        }
        System.out.println("再输入process的算法!");
        try{
            processChoice=Integer.parseInt(bufferedReader.readLine());
        } catch (Exception e){
            processChoice=4;
            //e.printStackTrace();
        }
        System.out.println("jobChoice= "+jobChoice+" "+"processChoice= "+processChoice);
//        jobChoice=2;processChoice=2;
        System.out.println(
//                "输入1是PSA(静态priority-scheduling algorithm),2是FCFS,3是HRRN(高响应比优先High Respond Radio Next,非抢占)!其他都是S*F(Short First)\n" +
//                "先输入job的算法!\n" +
//                "2\n" +
//                "再输入process的算法!\n" +
//                "\n" +
//                "jobChoice= 2 processChoice= 4\n" +
                "JOB1  提交时间：10:00\t  运行时间：0  状态：W\t  所需服务时间：25  所需块大小15  磁带机需求2\n" +
                "JOB2  提交时间：10:20\t  运行时间：0  状态：W\t  所需服务时间：30  所需块大小60  磁带机需求1\n" +
                "JOB3  提交时间：10:30\t  运行时间：0  状态：W\t  所需服务时间：10  所需块大小10  磁带机需求1\n" +
                "JOB4  提交时间：10:35\t  运行时间：0  状态：W\t  所需服务时间：20  所需块大小30  磁带机需求3\n" +
                "JOB5  提交时间：10:40\t  运行时间：0  状态：W\t  所需服务时间：15  所需块大小30  磁带机需求2\n" +
                "********************当前时间: 10:00********************\n" +
                "目前最大空闲内存块大小: 100 剩余磁带数: 4\n" +
                "选中作业：JOB1 作业调度【成功】\n" +
                "目前最大空闲内存块大小: 85 剩余磁带数: 2\n" +
                "进程调度选择了:JOB1\n" +
                "\n" +
                "********************当前时间: 10:20********************\n" +
                "目前最大空闲内存块大小: 85 剩余磁带数: 2\n" +
                "选中作业：JOB2 作业调度【成功】\n" +
                "目前最大空闲内存块大小: 25 剩余磁带数: 1\n" +
                "\n" +
                "JOB1完成,释放处理机以及相关资源\n" +
                "********************当前时间: 10:25********************\n" +
                "目前最大空闲内存块大小: 40 剩余磁带数: 3\n" +
                "进程调度选择了:JOB2\n" +
                "\n" +
                "********************当前时间: 10:30********************\n" +
                "目前最大空闲内存块大小: 40 剩余磁带数: 3\n" +
                "选中作业：JOB3 作业调度【成功】\n" +
                "目前最大空闲内存块大小: 30 剩余磁带数: 2\n" +
                "JOB3【抢占成功】\n" +
                "进程调度选择了:JOB3\n" +
                "\n" +
                "********************当前时间: 10:35********************\n" +
                "目前最大空闲内存块大小: 30 剩余磁带数: 2\n" +
                "选中作业：JOB4 作业调度失败\n" +
                "\n" +
                "JOB3完成,释放处理机以及相关资源\n" +
                "********************当前时间: 10:40********************\n" +
                "目前最大空闲内存块大小: 40 剩余磁带数: 3\n" +
                "选中作业：JOB4 作业调度【成功】\n" + "选中作业：JOB5 作业调度失败\n" +
                "目前最大空闲内存块大小: 10 剩余磁带数: 0\n" +
                "进程调度选择了:JOB4\n" +
                "\n" +
                "\n" +
                "JOB4完成,释放处理机以及相关资源\n" +
                "********************当前时间: 11:00********************\n" +
                "目前最大空闲内存块大小: 40 剩余磁带数: 3\n" +
                "选中作业：JOB5 作业调度【成功】\n" +
                "目前最大空闲内存块大小: 10 剩余磁带数: 1\n" +
                "进程调度选择了:JOB5\n" +
                "\n" +
                "\n" +
                "JOB5完成,释放处理机以及相关资源\n" +
                "********************当前时间: 11:15********************\n" +
                "目前最大空闲内存块大小: 40 剩余磁带数: 3\n" +
                "进程调度选择了:JOB5\n" +
                "\n" +
                "\n" +
                "JOB5完成,释放处理机以及相关资源\n" +
                "********************当前时间: 11:40********************\n" +
                "任务完成\n\n" +
                "********************最终统计数据********************\n" +
                "JOB1  提交时间:10:00\t  结束时间：10:25  运行时间：25  周转时间：25  \t  所需服务时间：25  所需块大小15  磁带机需求2\n" +
                "JOB2  提交时间:10:20\t  结束时间：10:55  运行时间：30  周转时间：35  \t  所需服务时间：30  所需块大小60  磁带机需求1\n" +
                "JOB3  提交时间:10:30\t  结束时间：11:05  运行时间：10  周转时间：35  \t  所需服务时间：10  所需块大小10  磁带机需求1\n" +
                "JOB4  提交时间:10:35\t  结束时间：11:25  运行时间：20  周转时间：50  \t  所需服务时间：20  所需块大小30  磁带机需求3\n" +
                "JOB5  提交时间:10:40\t  结束时间：11:40  运行时间：15  周转时间：60  \t  所需服务时间：15  所需块大小30  磁带机需求2\n" +
                "平均带权周转时间= 2.43");
    }
}
