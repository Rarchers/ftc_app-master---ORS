package org.firstinspires.ftc.robotcontroller.filters.CvHelper;


import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.firstinspires.ftc.robotcontroller.filters.NoneFilter;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

/**
 * Created by Rarcher on 2018/8/24.
 */

public class Driver {
    private static Driver driver;
    public static Driver getInstance() {
        if (driver == null) {
            driver = new Driver();
        }
        return driver;
    }

    private Driver() {

    }

    private Helper h = new Helper();
    private final String PATH = Environment.getExternalStorageDirectory().getPath()+"/FIRST/OpenCV/Driver/";
    private final String POSTFIX = ".opencv";

    //检测文件内容
    public String read(int index) {
        if (h.getfile(PATH, "driver" + index + POSTFIX) != null) {
            return h.getfile(PATH, "driver" + index + POSTFIX);//找到文件，返回读取的信息
        }else {
            h.initData("0", PATH, "driver" + index + POSTFIX);
            return "0";//没有找到文件，创建一个，内容为0
        }
    }
    public boolean check(int index) {
        switch (read(index)) {
            case "0":
                return false;//任务没有完成
            case "1":
                return true;//任务已经完成
            default:
//                h.initData("0", PATH, "driver" + index + POSTFIX);
                return false;
        }
    }
    //修改文件内容
    public void change(int index, String content) {
        read(index);
        h.initData(content, PATH, "driver" + index + POSTFIX);
        allFinish();
    }
    //检测是否全部完成
    public void allFinish() {
        boolean finish = true;
        int need = 3;
        for (int i = 1; i <= need; i++) {
            finish = finish && check(i);
            Log.i("完成"+i,check(i)+"");
        }
        Log.i("完成",""+finish);
        if (finish) {
//            String finishText = "ϲYéBțGשRְXƬI\u09BAN̙E͔TτZ˹YɾH͈IגHݛP\u098DZΥBࠥXणTǒVবOΉLІKōWؖQࠀZঁUތEݨK̓R";
            Toast.makeText(AppUtil.getInstance().getApplication().getApplicationContext(),
                    NoneFilter.Decrypt("͌BûXȳXСJڅA̲WʉZƒQ৮L۠EŧKԒM̍WŝDࠄÊTःXग़TܖM̜LۈQ٧C֨CǬZ݃O٦IةXÓBNPǒG",Short.MAX_VALUE), Toast.LENGTH_LONG).show();
        }
    }
    public int count(){
        int finish=0;
        for (int j = 1;j<=7;j++){
            if (h.getfile(PATH,"driver" + j + POSTFIX)==null){
                init();
            }
            int age=Integer.parseInt(h.getfile(PATH,"driver" + j + POSTFIX).trim());
            if (age==1){
                finish++;}
        }
        return finish;
    }
    public String completion() {
        return NoneFilter.Decrypt("ENࠐFۉRΜLۘXӫTফSŖA",Short.MAX_VALUE) + count() + "/7";
    }
    public void init() {
        for (int j=0;j<=7;j++){
            read(j);
        }
    }
}