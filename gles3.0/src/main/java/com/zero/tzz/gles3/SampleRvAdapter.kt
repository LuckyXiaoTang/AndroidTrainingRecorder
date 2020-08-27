package com.zero.tzz.gles3

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author Zero_Tzz
 * @date 2020-06-12 16:21
 * @description MyRecyclerViewAdapter
 */
public class SampleRvAdapter(
    private var mContext: Context,
    private var mTitles: List<String>
) : RecyclerView.Adapter<SampleRvAdapter.SampleViewHolder?>(), View.OnClickListener {
    private var mSelectIndex = 0
    private var mBlock: ((View?, Int) -> Unit)? = null
    fun setSelectIndex(index: Int) {
        mSelectIndex = index
    }

    fun getSelectIndex(): Int {
        return mSelectIndex
    }

    fun addOnItemClickListener(block: ((View?, Int) -> Unit)?) {
        mBlock = block
    }

    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): SampleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sample_item_layout, parent, false)
        val myViewHolder =
            SampleViewHolder(view)
        view.setOnClickListener(this)
        return myViewHolder
    }

    override fun onBindViewHolder(
        @NonNull holder: SampleViewHolder,
        position: Int
    ) {
        holder.mTitle.text = mTitles[position]
        if (position == mSelectIndex) {
            holder.mRadioButton.isChecked = true
            holder.mTitle.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        } else {
            holder.mRadioButton.isChecked = false
            holder.mTitle.text = mTitles[position]
            holder.mTitle.setTextColor(Color.GRAY)
        }
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        return mTitles.size
    }

    override fun onClick(v: View) {
        mBlock?.invoke(v, v.tag as Int)
    }

    inner class SampleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mRadioButton: RadioButton = itemView.findViewById(R.id.radio_btn)
        var mTitle: TextView = itemView.findViewById(R.id.item_title)
    }
}