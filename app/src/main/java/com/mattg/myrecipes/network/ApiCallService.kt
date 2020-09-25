package com.mattg.myrecipes.network

import android.content.Context
import com.karumi.dexter.BuildConfig
import com.mattg.myrecipes.models.recipeSearchResult.RecipeResponse
import com.mattg.myrecipes.models.responsePreset.AnalyzedInstruction
import com.mattg.myrecipes.models.responsePreset.SpoonacularResponsePreset
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiCallService {

    private const val BASE_URL = "https://api.spoonacular.com/recipes/"
    private var api: ApiCall? = null


    private fun getApi(context: Context): ApiCall {
        if(api == null){
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            if(BuildConfig.DEBUG){
                okHttpClient.addInterceptor(logging)
            }
            //define and add a cache
            val cacheSize = 5 * 1024 * 1024L // = 5mb cache
            val cache = Cache(context.cacheDir, cacheSize) // the directory is obtained with context.cacheDir
            //add cache to client
            okHttpClient.cache(cache)
            //add custom interceptor
            okHttpClient.addInterceptor{chain ->
                //get the outgoing request
                val request = chain.request()
                //add another header to this request, its been built but build a new one
                val newRequest = request.newBuilder() //can add headers below this
                    .build()
                chain.proceed(newRequest)
            }
            api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
                .create(ApiCall::class.java)
        }
        return api!!
    }

    //for getting a single recipe from recyclerview
    fun callSingleRecipe(context: Context, id: Int, key: String): Call<RecipeResponse> {
        return getApi(context).getSingleRecipe(id, key)
    }

    //for getting recipes via the search box
    fun callSearch(
        context: Context,
        key: String,
        searchString: String
    ): Call<SpoonacularResponsePreset> {
        return getApi(context).getSearchedRecipes(key, searchString)
    }

    //originally loaded a random list of recipes to the home screen, still available but not in use
    fun callPresetOnOpen(
        context: Context,
        key: String,
        number: Int,
        cuisine: String,
        query: String,
        sort: String
    ): Call<SpoonacularResponsePreset> {
        return getApi(context).getRecipes(key, number, cuisine, query, sort)
    }

    fun callIdInstructions(context: Context, id: Int, key: String): Call<AnalyzedInstruction> {
        return getApi(context).getRecipeDetailsById(id, key)
    }

    //getting a recipe search result from home screen buttons
    fun callMainScreenGetRecipes(
        context: Context,
        key: String,
        number: Int,
        cuisine: String,
        sort: String
    ): Call<SpoonacularResponsePreset> {
        return getApi(context).getRecipesMain(key, number, cuisine, sort)
    }


}