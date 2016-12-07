package cn.clickwise.utils.other;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.clickwise.config.Constants;

/**
 * Created by T420s on 2016/11/10.
 */
public class CalculateUtil {
    private static final double EARTH_RADIUS = 6378137.0;

    // 返回单位是米
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 为路由位置详情排序
     * 备注：都是血和泪呀
     * @param data
     * @return
     */
    public static List<Map<String, String>> sortMap(List<Map<String, String>> data) {
        List<Map<String, String>> sortList = new ArrayList<>();
        int rowLen = data.size();
        ArrayList<Integer> distances = new ArrayList<>();
        //提取距离数据
        for (int i = 0; i < rowLen; i++) {
            Map<String, String> stringMap = data.get(i);
            String disStr = stringMap.get(Constants.ROUTERLOCAL_DISTANCE);
            int distance = Integer.parseInt(disStr.substring(0, disStr.length() - 1));
            //按原始数据顺序将距离单独取出来
            distances.add(distance);
        }
        /**
         * 注意没有实例化直接赋值和实例化后添加的区别，前者是两个索引共用一块内容，后者是重新开辟了内存只是与前者的值相同
         */
        //List<Integer> distancesSort=distances;
        List<Integer> distancesSort = new ArrayList<>();
        distancesSort.addAll(distances);
        List<Integer> sortPoi = new ArrayList<>();
        //数据排序
        Collections.sort(distancesSort);
        for (int i = 0; i < rowLen; i++) {
            int dis = distancesSort.get(i);
            //依次得到从小到大数在distances的位置
            /*for (int j = 0; j < rowLen; j++) {
                if (distances.get(j) == dis) {
                    sortPoi.add(j);
                    break;
                }
            }*/
            int poi = distances.indexOf(dis);
            sortPoi.add(poi);
        }
        for (int i = 0; i < rowLen; i++) {
            int location = sortPoi.get(i);
            sortList.add(data.get(location));
        }
        return sortList;
    }
}
