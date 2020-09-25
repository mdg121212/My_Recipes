package com.mattg.myrecipes.ui.viewrecipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mattg.myrecipes.db.Recipe
import com.mattg.myrecipes.db.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class RecipesViewModel(private val repository: RecipeRepository) : ViewModel() {

    val recipesList = repository.getAllRecipes()


    private val _positionFromAdapter = MutableLiveData<Int>()
    val positionFromAdapter: MutableLiveData<Int> get() = _positionFromAdapter


    private val _recipesListFromSearch = MutableLiveData<List<Recipe>>()
    val recipesListFromSearch: LiveData<List<Recipe>> = _recipesListFromSearch

    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe

    private val testDialogRecipe = MutableLiveData<Int>()
    val dialogInt = MutableLiveData<Int>()


    fun setDialogRecipe(id: Int) {
        testDialogRecipe.value = id

    }

    lateinit var searchString: String

    private val savedId = MutableLiveData<String>()


    fun setPositionFromAdapter(position: Int) {
        _positionFromAdapter.value = position
    }

    fun setId(id: String) {
        savedId.value = id
    }

    private fun insertRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertRecipe(recipe)
        }
    }

    fun getRecipesFromQuery(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = repository.getRecipesSearch(query)
            MainScope().launch { _recipesListFromSearch.value = list }
        }
    }

    fun saveUserRecipeToDb(
        title: String?,
        ingredients: String?,
        directions: String?,
        notes: String?,
        uriToSave: String?,
        rating: Float?,
        date: String?,
        isLeftover: Boolean,
        loadedId: String
    ): Boolean {
        val recipeToSave = Recipe(
            title,
            ingredients,
            directions,
            notes,
            uriToSave,
            null,
            null,
            rating,
            date,
            isLeftover
        )
        if (loadedId != "notloaded") {
            recipeToSave.id = loadedId.toInt()
        }
        insertRecipe(recipeToSave)
        return false
    }

    fun getRecipeById2(id: String) = repository.getRecipeByIdLive(id)

    fun deleteRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteRecipe(recipe)

        }


    }

}


