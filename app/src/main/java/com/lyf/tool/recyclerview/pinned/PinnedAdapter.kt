package com.lyf.tool.recyclerview.pinned

import android.support.v7.widget.RecyclerView

/**
 * 固定item的适配器
 * Created by LYF on 2018/9/10.
 */
abstract class PinnedAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {
    abstract fun isPinnedPosition(position: Int): Boolean
}