package com.mattg.myrecipes.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.mattg.myrecipes.databinding.ApirecipeItemBinding
import com.mattg.myrecipes.models.responsePreset.Result
import com.mattg.myrecipes.ui.viewrecipes.ApiClickListener


class ApiRecipeAdapter(private val clickListener: ApiClickListener) :
    RecyclerView.Adapter<ApiRecipeAdapter.ApiViewHolder>() {
    //moved from constructor
    private var results: List<Result> = ArrayList()
    //function to update its value
    fun setList(results: List<Result>) {
        this.results = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiViewHolder {
        return ApiViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onBindViewHolder(holder: ApiViewHolder, position: Int) {
        val currentItem = results[position]
        holder.bind(currentItem, clickListener)
    }

    class ApiViewHolder private constructor( private val binding: ApirecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ApiViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ApirecipeItemBinding.inflate(layoutInflater, parent, false)
                return ApiViewHolder(binding)
            }
        }
        fun bind(result: Result, clickListener: ApiClickListener){
            binding.item = result
            binding.root.setOnClickListener {
                clickListener.onClick(result, adapterPosition)
            }
            binding.root.apply {
                animation = AlphaAnimation(0.0f, 1.0f)
                animation.duration = 2000
            }
            binding.executePendingBindings()
        }

    }
}