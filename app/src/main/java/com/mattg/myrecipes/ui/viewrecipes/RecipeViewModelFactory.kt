package com.mattg.myrecipes.ui.viewrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mattg.myrecipes.db.RecipeRepository
//boilerplate code used for all view model factories
@Suppress("UNCHECKED_CAST")
class RecipeViewModelFactory(private val repository: RecipeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecipesViewModel::class.java)){
            return RecipesViewModel(repository) as T
        }
        throw IllegalArgumentException("unknown view model class")
    }
}