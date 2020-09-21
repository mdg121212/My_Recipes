package com.mattg.myrecipes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(entities = [Recipe::class], version = 3, exportSchema = false)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    /**
     * For now destructive migration is implemented b/c no schema change is planned.  Were schema to change
     * need to add a migration object, laid out clearly here: https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
     */

    //singleton implementation
    companion object{
        //reference to database
        @Volatile private var INSTANCE: RecipesDatabase? = null

            fun getInstance(context: Context): RecipesDatabase {

                synchronized(this){

                    var instance = INSTANCE

                    if(instance == null) instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecipesDatabase::class.java,
                        "recipes.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                   INSTANCE = instance
                   return instance
                }

    }
    }
}
