package avalidations.jgsu.com.avalidations.com.jgsu.avalidations;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bibi on 2016/10/14.
 */

public class ThemeManager {
    private static ThemeMode mThemeMode=ThemeMode.DAY;
    //默认日间模式
    //主题模式监听器
    private static List<OnThemeChangeListener> mThemeChangeListenerList =new LinkedList<>();
    //夜间资源缓存 key：资源类型,值 <key：资源名称 value：int值>
    private static HashMap<String,HashMap<String,Integer>> sCachedNightResrouces=
            new HashMap<>();
    private static final String RESOURCE_SUFFIX="_night";
    public enum ThemeMode{
        DAY,NIGHT
    }
    /*
    设置主题：
     */
    public static void setThemeMode(ThemeMode themeMode){
        if (mThemeMode!=themeMode){
            mThemeMode=themeMode;
            if (mThemeChangeListenerList.size() > 0) {
                for (OnThemeChangeListener listener:mThemeChangeListenerList){
                    listener.onThemeChanged();
                }
            }
        }
    }
    /*
    根据传入的日间模式的resId 得到相应主题的resId
    dayResId 日间模式resId  夜间 nightResId
     */
    public static int getCurrentThemeRes(Context context,int dayResId){
        if (getThemeMode()==ThemeMode.DAY){
            return dayResId;
        }
        //资源名
        String entryName =context.getResources().getResourceEntryName(dayResId);
        //资源类型
        String typeName =context.getResources().getResourceTypeName(dayResId);
        HashMap<String ,Integer> cachedRes = sCachedNightResrouces.get(typeName);
        //从缓存中取 若有 返回id
        if (cachedRes ==null){
            cachedRes=new HashMap<>();
        }
        Integer resId=cachedRes.get(entryName+RESOURCE_SUFFIX);
        if (resId!=null&&resId!=0){
            return resId;
        }else{
            //如果缓存中没有再根据资源ID取动态获取
            try {
                int nightResId = context.getResources().getIdentifier(entryName + RESOURCE_SUFFIX,
                        typeName, context.getPackageName());
                //放入缓存中
                cachedRes.put(entryName + RESOURCE_SUFFIX, nightResId);
                sCachedNightResrouces.put(typeName, cachedRes);
                return nightResId;
            }catch (Resources.NotFoundException e){
                e.printStackTrace();
            }
        }
        return 0;
    }
    /*
    注册ThemeChangeListener
     */
    public static void registerThemeChangeListener(OnThemeChangeListener listener){
        if (!mThemeChangeListenerList.contains(listener)){
            mThemeChangeListenerList.add(listener);
        }
    }
    /*
    反注册ThemeChangeListener
     */
    public static void unregisterThemeChangeListener(OnThemeChangeListener listener){
        if (mThemeChangeListenerList.contains(listener)){
            mThemeChangeListenerList.remove(listener);
        }
    }

    public static ThemeMode getThemeMode(){
        return mThemeMode;
    }
    /*
    主题模式切换监听器
    */
    public interface OnThemeChangeListener{
        //主题切换时回调
        void onThemeChanged();
    }
}
