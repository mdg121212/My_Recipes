package com.mattg.myrecipes.ui.viewrecipes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mattg.myrecipes.databinding.ViewRecipesItemBinding
import com.mattg.myrecipes.db.Recipe
import com.mattg.myrecipes.db.RecipeRepository
import com.mattg.myrecipes.db.RecipesDatabase

class ViewRecipesAdapter(val context: Context,
                         recipes: List<Recipe>,
                         private val parentFragment: Fragment,
                         private val clickListener: ClickListener) :
    RecyclerView.Adapter<ViewRecipesAdapter.RecipeViewHolder>() {

    private var listToUse: List<Recipe> = recipes
    private lateinit var recipesViewModel: RecipesViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        //get way to delete and edit items
        val dao = RecipesDatabase.getInstance(parentFragment.requireContext()).recipeDao()
        //get repository with dao
        val repository = RecipeRepository(dao)
        recipesViewModel = RecipesViewModel(repository)
        return RecipeViewHolder.from(parent)
    }

    override fun getItemCount() = listToUse.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = listToUse[position]
        holder.bind(recipe, clickListener)
    }


    class RecipeViewHolder private constructor(private val binding: ViewRecipesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): RecipeViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewRecipesItemBinding.inflate(layoutInflater, parent, false)
                return RecipeViewHolder(binding)
            }

        }
            fun bind(recipe: Recipe, clickListener: ClickListener) {
                binding.recipe = recipe
                binding.imageButtonItemdelete.setOnClickListener {
                    clickListener.onClickDelete(recipe, adapterPosition, 1)
                }
                binding.imageButtonItemedit.setOnClickListener {
                    clickListener.onClickEdit(recipe, adapterPosition,2)
                }
                binding.imageButtonItemview.setOnClickListener {
                    clickListener.onClickView(recipe, adapterPosition,3)
                }
                binding.executePendingBindings()

                binding.root.animation = AlphaAnimation(0.0f, 1.0f).apply {
                duration = 1000
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}



