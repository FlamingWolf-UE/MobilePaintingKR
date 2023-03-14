package com.example.paintingapplab3.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapplab3.Interfaces.ToolsListener
import com.example.paintingapplab3.Interfaces.ViewOnClick
import com.example.paintingapplab3.databinding.ToolsItemBinding
import com.example.paintingapplab3.model.Operation
import com.example.paintingapplab3.model.ToolsItem

class ToolsAdapter(var listener: ToolsListener, var tools: List<ToolsItem>) :
    RecyclerView.Adapter<ToolsAdapter.ToolsViewHolder>() {

    private var selected: Int = -1

    class ToolsViewHolder(binding: ToolsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val icon: ImageView = binding.toolsIcon
        val name: TextView = binding.toolsName
        lateinit var viewOnClick: ViewOnClick

        fun setOnClickView(viewOnClick: ViewOnClick) {
            this.viewOnClick = viewOnClick
        }

        init {
            itemView.setOnClickListener {
                viewOnClick.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolsViewHolder {
        val binding =
            ToolsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToolsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToolsViewHolder, position: Int) {
        val currentTool = tools[position]
        holder.name.text = currentTool.name
        holder.icon.setImageResource(currentTool.icon)

        holder.setOnClickView(object : ViewOnClick {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(pos: Int) {
                if (currentTool.allowHighlight) {
                    selected = pos
                }
                    notifyDataSetChanged()
                    listener.onSelected(currentTool.operation)

            }
        })

        if (selected == position) {
            holder.name.setTextColor(Color.RED)
            holder.name.setTypeface(null, Typeface.BOLD)
        } else {
            holder.name.setTextColor(Color.WHITE)
            holder.name.setTypeface(null, Typeface.NORMAL)
        }
    }

    override fun getItemCount(): Int = tools.size
}