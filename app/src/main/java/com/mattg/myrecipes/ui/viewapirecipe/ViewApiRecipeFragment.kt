package com.mattg.myrecipes.ui.viewapirecipe


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mattg.myrecipes.BaseFragment
import com.mattg.myrecipes.R
import com.mattg.myrecipes.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.view_api_recipe_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ViewApiRecipeFragment : BaseFragment() {

    private var position: Int = 0

    //passing the position in list of results via navArgs
    private val args: ViewApiRecipeFragmentArgs by navArgs()
    private lateinit var viewModel: HomeViewModel
    private var ingredientString = ""
    private var stepString = ""
    private var imageLocation: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        position = args.positionInt
        return inflater.inflate(R.layout.view_api_recipe_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        observeViewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.viewapirecipe_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.viewapirecipe_save -> {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.saveApiRecipe(tv_api_view_title.text.toString(),
                        ingredientString,
                        stepString,
                        imageLocation
                    )
                    MainScope().launch {
                        Toast.makeText(
                            requireContext(),
                            "Recipe Saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_viewApiRecipeFragment_to_nav_viewrecipes)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModels(){
        val directionsText = tv_api_view_directions
        val ingredientsText = tv_api_view_ingredients
        val imageHolder = iv_api_view_image_picture
        val urlText = tv_api_view_url_link
        val titleText = tv_api_view_title
        var stepNumber = 0

        viewModel.getSingleRecipe(position)

        viewModel.recipeResponse.observe(viewLifecycleOwner,  {
            titleText.text = it.title
            Glide.with(requireContext())
                .load(it.image)
                .into(imageHolder)

            val ingredients = it.extendedIngredients
            for (ingredient in ingredients) {
                val name = ingredient.name
                val amount = ingredient.measures.us.amount
                val measure = ingredient.measures.us.unitShort
                ingredientString += "$name - $amount $measure\n"

                ingredientsText.text = ingredientString
            }

            val directions = it.analyzedInstructions
            for (instruction in directions) {
                val step = it.analyzedInstructions[0].name
                stepString += "$step "
                val stepDetails = it.analyzedInstructions[0].steps
                //  TODO("Some recipes do not have clearly delimited steps.  The result is one long grammatically" +
               //         "incorrect string; need to figure out how to format these cases.")
                    for (item in stepDetails) {
                        stepNumber++
                        val name = item.step
                        stepString += "$stepNumber: $name\n\n"
                    }
                    directionsText.text = stepString

            }
            urlText.text = it.sourceUrl
            imageLocation = it.image
            initializeViews()
        })
    }

    private fun initializeViews() {
        val viewsList = listOf(
            iv_api_view_image_picture,
            tv_api_view_title,
            tv_api_view_directions,
            tv_api_view_ingredients,
            tv_api_view_url_link)
        for(view in viewsList){
            view.visibility = View.VISIBLE
        }
    }


}

