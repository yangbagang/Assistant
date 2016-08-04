package com.ybg.rp.assistant.utils;

import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.xutils.common.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ybg
 * @date 2014年9月4日-下午5:15:15
 */
public class SimpleUtil {

    /**
     * String 转Double
     *
     * @param dou
     * @return
     * @author ybg
     * @date 2014年10月23日-下午2:30:56
     */
    public static Double toDouble(String dou) {
        if (!StrUtil.isEmpty(dou)) {
            return Double.parseDouble(dou);
        }
        return 0.00;
    }

    /**
     * 判断手机号是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        // 181-183手机号无法通过，修改18[0,5-9] 18[^4,\\D]
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断是否是身份证
     * 定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
     *
     * @param idNum
     * @return
     * @author ybg
     */
    public static boolean isIDNum(String idNum) {
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
        //通过Pattern获得Matcher
        Matcher idNumMatcher = idNumPattern.matcher(idNum);
        return idNumMatcher.matches();
    }

    /**
     * 密码验证 数字、字母和符号，长度最小6，最大20
     *
     * @param pass
     * @return
     */
    public static boolean isPass(String pass) {
        String reg = "(?![^a-zA-Z0-9]+$)(?![^a-zA-Z/D]+$)(?![^0-9/D]+$).{6,20}$";
        // String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(pass);
        return matcher.matches();
    }

    /**
     * 暂停时间
     *
     * @param time 时间
     */
    public static void toSeelp(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtil.d("seelp-92");
        }
    }

    /**
     * 设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);
    }

    /**
     * 设置ListView的高�?+ 手动增加高度
     *
     * @param listView
     * @param size     额外增加高度
     */
    public static void setListViewHeightBasedOnChildren(GridView listView, int size) {
        try {
            // 获取listview的adapter
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
            // listAdapter.getCount()小于等于8时计算两次高度相加
            for (int i = 0; i < listAdapter.getCount(); i =i+size) {
                // 获取listview的每一个item
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                // 获取item的高度和
                totalHeight += listItem.getMeasuredHeight();
            }

            // 获取listview的布局参数
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            // 设置高度
            params.height = totalHeight;
            // 设置margin
            ((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
            // 设置参数
            listView.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址 4.0默认IPV6
     *
     * @return
     */
    public static String getPsdnIpv6() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址 IPV4
     *
     * @return
     */
    public static String getPsdnIpv4() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof InetAddress) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取sd卡路径 双sd卡时，获得的是外置sd卡
     *
     * @return
     */
    public static String getSDCardPath() {
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                LogUtil.i(lineStr);
                if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure", "");
                        return result;
                    }
                }
                // 检查命令是否执行失败。
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                    LogUtil.e("命令执行失败!");
                }
            }
        } catch (Exception e) {
            LogUtil.e(e.toString());
            // return Environment.getExternalStorageDirectory().getPath();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (inBr != null) {
                    inBr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取扩展存储路径，TF卡、U盘
     */
    public static String getExternalStorageDirectory(){
        String dir = new String();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        dir = dir.concat(columns[1] + "\n");
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        dir = dir.concat(columns[1] + "\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dir;
    }
}
