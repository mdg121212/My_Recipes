package com.mattg.myrecipes.ui.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mattg.myrecipes.db.Recipe
import com.mattg.myrecipes.db.RecipeRepository
import com.mattg.myrecipes.db.RecipesDatabase
import com.mattg.myrecipes.models.recipeSearchResult.RecipeResponse
import com.mattg.myrecipes.models.responsePreset.Result
import com.mattg.myrecipes.retrofitRepository.SpoonacularRepository


//need context to show toasts in repository
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    //get instance of repository object, passing application context
    private val repository: SpoonacularRepository = SpoonacularRepository(application)
    //from repository show progress variable here as well
    //can observe this now
    val showProgress : LiveData<Boolean>
    //a list to be populated in init block from repository
    val resultList: LiveData<List<Result>> = repository.responseList
    val recipeResponse: LiveData<RecipeResponse> = repository.recipeResponse
    private val apiResponseSearch = MutableLiveData<List<Result>>()
    private val loading = MutableLiveData<Boolean>()
    val hasCalled = MutableLiveData<Boolean>()
    lateinit var searchString: String

    init {
        this.showProgress = repository.showProgress
    }

    fun progressState() {
        repository.progressState()
    }

    fun setHasCalled() {
        hasCalled.value = true
    }

    fun resetHasCalled() {
        hasCalled.value = false
    }

    fun setLoading() {
        loading.value = true
    }

    fun setSearchResults() {
        apiResponseSearch.value = repository.responseListSearch.value
    }

    fun getHomeScreenRecipes(string: String) {
        repository.getMainScreenResult(string)
    }


    fun getSingleRecipe(id: Int) {

        repository.getSingleRecipe(id)
    }

    fun fetchSearchResults(searchString: String) {

        repository.getRetrofitResultsSearch(searchString)

    }

    //saving the recipe to the local database
    fun saveApiRecipe(title: String, ingredients: String, step: String, image: String?) {
        val uri = Uri.parse("android.resource://com.mattg.myrecipes/drawable/placeholder")
        val recipeToSave = Recipe(
            title,
            ingredients,
            step,
            "",
            image ?: uri.toString(),
            null,
            null,
            null,
            null,
            false
        )

        val dao = RecipesDatabase.getInstance(getApplication()).recipeDao()
        //get repository with dao
        val repository = RecipeRepository(dao)
        repository.insertRecipe(recipeToSave)
    }
}