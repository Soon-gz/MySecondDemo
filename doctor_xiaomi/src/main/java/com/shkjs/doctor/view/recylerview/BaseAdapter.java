/**
 * created by jiang, 12/3/15
 * Copyright (c) 2015, jyuesong@gmail.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG            #
 * #                                                   #
 */

package com.shkjs.doctor.view.recylerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * 项目中使用的recyclerview版本是23.2.+的，所以它的item的根布局请使用wrap_content
 * Created by jiang on 2/19/16.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private OnItemClickListener onItemClickListener;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {

        if (onItemClickListener != null) {
            holder.getmConvertView().setClickable(true);
            holder.getmConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            });
        }
        onBindView(holder, holder.getAdapterPosition());

    }

    protected abstract void onBindView(BaseViewHolder holder, int position);

    /**
     * 这里通过使用layoutID作为item的viewtype
     *
     * @param position 位置
     * @return type
     */
    @Override
    public int getItemViewType(int position) {
        return getLayoutID(position);
    }


    protected abstract int getLayoutID(int position);


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
