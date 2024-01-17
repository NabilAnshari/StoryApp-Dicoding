package com.nabil.submission1_appstory.Adapt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nabil.submission1_appstory.Data.ListStory
import com.nabil.submission1_appstory.databinding.StoryCardBinding

class ShowAdapter :
    PagingDataAdapter<ListStory, ShowAdapter.MyViewHolder>(DIFF_CALLBACK){

    private var onItemClick: ((ListStory)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @Suppress("DEPRECATION")
    inner class MyViewHolder(val binding: StoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStory) {
            binding.apply {
                username.text = data.name
                description.text = data.description
            }
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.photo)
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    onItemClick?.invoke(data)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    fun setOnItemClickListener(listener: (ListStory) -> Unit){
        onItemClick = listener
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}