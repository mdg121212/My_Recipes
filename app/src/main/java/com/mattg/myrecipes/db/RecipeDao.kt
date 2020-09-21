package com.mattg.myrecipes.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: String): Recipe

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById2(id: String): LiveData<Recipe>

    @Query("SELECT * FROM recipes order by title")
    fun getRecipesLiveData(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE title like '%' || :query  || '%'")
    suspend fun getRecipesByTitleSearch(query: String): List<Recipe>

    @Delete
    fun deleteRecipe(recipe: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRecipe(recipe: Recipe)

}