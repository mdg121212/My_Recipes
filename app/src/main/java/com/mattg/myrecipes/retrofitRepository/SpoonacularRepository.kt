package com.mattg.myrecipes.retrofitRepository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mattg.myrecipes.MainActivity
import com.mattg.myrecipes.R
import com.mattg.myrecipes.models.recipeSearchResult.RecipeResponse
import com.mattg.myrecipes.models.responsePreset.Result
import com.mattg.myrecipes.models.responsePreset.SpoonacularResponsePreset
import com.mattg.myrecipes.network.ApiCallService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpoonacularRepository(private val application: Application) {

    //set the progress bar variable
    val showProgress = MutableLiveData<Boolean>()
    val responseList = MutableLiveData<List<Result>>()
    val responseListSearch = MutableLiveData<List<Result>>()
    val recipeResponse = MutableLiveData<RecipeResponse>()
    var resultString = ""
    private val key = MainActivity().getNativeKey1()
    fun progressState() {
        showProgress.value = !(showProgress.value != null && showProgress.value!!)
    }

    var listToSend = emptyList<Result>()


    fun getSingleRecipe(id: Int) {
        ApiCallService.callSingleRecipe(application, id, key)
                .enqueue(object : Callback<RecipeResponse> {
                    override fun onResponse(
                            call: Call<RecipeResponse>,
                            response: Response<RecipeResponse>
                    ) {
                        showProgress.value = false
                        recipeResponse.value = response.body()
                    }

                    override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                        showProgress.value = false
                        Toast.makeText(application, "Request Failed: Network Error", Toast.LENGTH_SHORT)
                                .show()
                    }
                })
    }

    fun getRetrofitResultsSearch(query: String) {
        ApiCallService.callSearch(application, key, query)
                .enqueue(object : Callback<SpoonacularResponsePreset> {
                    override fun onFailure(call: Call<SpoonacularResponsePreset>, t: Throwable) {
                        showProgress.value = false
                        Toast.makeText(application, "Request Failed: Network Error", Toast.LENGTH_SHORT)
                                .show()
                    }

                    override fun onResponse(
                            call: Call<SpoonacularResponsePreset>,
                            response: Response<SpoonacularResponsePreset>
                    ) {
                        showProgress.value = false
                        if (response.isSuccessful) {
                            response.let {
                                responseList.value = it.body()!!.results

                            }
                        } else {
                            Toast.makeText(application, "Network not responding", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                })
    }

    fun getRetrofitResults() {

        val cuisines = application.resources.getStringArray(R.array.cuisines)
        val proteins = application.resources.getStringArray(R.array.proteins)
        val randomCuisine = getRandomString(cuisines)
        val randomProtien = getRandomString(proteins)

        ApiCallService.callPresetOnOpen(
                application,
                key,
                10,
                randomCuisine,
                randomProtien,
                "random"
        )
                .enqueue(object : Callback<SpoonacularResponsePreset> {
                    override fun onFailure(call: Call<SpoonacularResponsePreset>, t: Throwable) {
                        showProgress.value = false
                        Toast.makeText(
                                application,
                                "Request Failed: Network Error $t, Trying again..",
                                Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onResponse(
                            call: Call<SpoonacularResponsePreset>,
                            response: Response<SpoonacularResponsePreset>
                    ) {
                        showProgress.value = false
                        //Generate a list of results when the response code is success
                        if (response.code() == 200) {
                            val list = response.body()!!.results
                            resultString = list.toString()
                            listToSend = list
                            responseList.value = list
                            //now link live data to viewmodel inside init function
                        } else {
                            Toast.makeText(application, "Network not responding..", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                })
    }

    private fun getRandomString(List: Array<String>): String {
        return List.random()
    }

    fun getMainScreenResult(input: String) {
        ApiCallService.callMainScreenGetRecipes(application, key, 15, input, "random")
                .enqueue(object : Callback<SpoonacularResponsePreset> {
                    override fun onFailure(call: Call<SpoonacularResponsePreset>, t: Throwable) {
                        showProgress.value = false
                        Toast.makeText(
                                application,
                                "Request Failed: Network Error $t",
                                Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onResponse(
                            call: Call<SpoonacularResponsePreset>,
                            response: Response<SpoonacularResponsePreset>
                    ) {
                        showProgress.value = false
                        if (response.code() == 200) {
                            val list = response.body()!!.results
                            resultString = list.toString()
                            listToSend = list
                            responseList.value = list
                        } else {
                            Toast.makeText(application, "Network not responding..", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                })
    }
}

