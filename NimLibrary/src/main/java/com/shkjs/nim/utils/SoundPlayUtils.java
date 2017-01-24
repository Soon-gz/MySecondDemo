package com.shkjs.nim.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.shkjs.nim.R;


/**
 * Created by xiaohu on 2016/12/6.
 * <p>
 * 消息提示音播放工具类
 */

public class SoundPlayUtils {

    private static SoundPlayUtils soundPlayUtils;
    // SoundPool对象
    private SoundPool mSoundPlayer;
    // 上下文
    private static Context mContext;

    private SoundPlayUtils() {
    }

    public static SoundPlayUtils getInstance() {
        if (null == soundPlayUtils) {
            synchronized (SoundPlayUtils.class) {
                if (null == soundPlayUtils) {
                    soundPlayUtils = new SoundPlayUtils();
                }
            }
        }
        return soundPlayUtils;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public SoundPlayUtils init(Context context) {
        // 初始化声音
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPlayer = new SoundPool.Builder().build();
        } else {
            // 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            mSoundPlayer = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        }

        mSoundPlayer.load(mContext, SoundSource.MSG.getIndex(), 1);// 1

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID 资源ID
     */
    public void play(int soundID) {
        play(soundID, false);
    }

    /**
     * 播放声音
     *
     * @param soundID 资源ID
     * @param loop    是否循环
     */
    public void play(final int soundID, final boolean loop) {
        if (null != mSoundPlayer) {
            new Thread() {
                @Override
                public void run() {
                    if (loop) {
                        mSoundPlayer.play(soundID, 1, 1, 0, -1, 1);
                    } else {
                        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
                    }
                }
            }.start();
        }
    }

    public void stop(int soundID) {
        if (null != mSoundPlayer) {
            mSoundPlayer.stop(soundID);
        }
    }

    /**
     * 资源枚举
     */
    public enum SoundSource {
        MSG(1, R.raw.msg);

        private int id;
        private int index;

        SoundSource(int id, int index) {
            this.id = id;
            this.index = index;
        }

        public int getId() {
            return id;
        }

        public int getIndex() {
            return index;
        }
    }
}
