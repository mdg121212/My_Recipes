package com.mattg.myrecipes.ui.viewrecipes

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mattg.myrecipes.BaseFragment
import com.mattg.myrecipes.MainActivity
import com.mattg.myrecipes.R
import com.mattg.myrecipes.db.Recipe
import com.mattg.myrecipes.db.RecipeRepository
import com.mattg.myrecipes.db.RecipesDatabase
import kotlinx.android.synthetic.main.fragment_edit_or_view.view.*
import kotlinx.android.synthetic.main.fragment_viewrecipes.*

class ViewRecipesFragment : BaseFragment() {

    private lateinit var recipesViewModel : RecipesViewModel
    private var positionFromAdapterFragment: Int? = null
    private var listForFragment: List<Recipe>? = null
    private lateinit var clickListener: ClickListener
    private var dialogInt: Int? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        //inflate view
        val view = inflater.inflate(R.layout.fragment_viewrecipes, container, false)
        //create dao
        val dao = RecipesDatabase.getInstance(requireContext()).recipeDao()
        //get repository with dao
        val repository = RecipeRepository(dao)
        //create factory with repository
        val factory = RecipeViewModelFactory(repository)

        //assign viewModel instance to data binding object
        recipesViewModel = ViewModelProvider(this, factory).get(RecipesViewModel::class.java)
        observeViewModel()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

    }

    private fun initRecyclerView() {
        recipesViewModel.recipesList.observe(viewLifecycleOwner, {
          //update recyclerview
            val list = it
            listForFragment = it
            clickListener = ClickListener { id, position, dialogInt ->
                recipesViewModel.apply {
                    setPositionFromAdapter(position)
                    setDialogRecipe(id)
                }
                when (dialogInt) {
                    1 -> startDeleteDialog(position)
                    2 -> startEditDialog(position)
                    3 -> startViewDialog(position)
                }
            }
            rv_viewrecipes.adapter = ViewRecipesAdapter(requireContext(), list, this, clickListener)

            rv_viewrecipes.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        })
    }

    private fun changeRecyclerView(){
        recipesViewModel.recipesListFromSearch.observe(viewLifecycleOwner, {
            rv_viewrecipes.adapter = ViewRecipesAdapter(requireContext(), it, this, clickListener)
            rv_viewrecipes.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        })
    }

    private fun observeViewModel(){
        //get way to delete and edit items
        val dao = RecipesDatabase.getInstance(this.requireContext()).recipeDao()
        //get repository with dao
        val repository = RecipeRepository(dao)
        recipesViewModel = RecipesViewModel(repository)
        recipesViewModel.positionFromAdapter.observe(viewLifecycleOwner, {
            positionFromAdapterFragment = it
        })
        recipesViewModel.dialogInt.observe(viewLifecycleOwner, {
            dialogInt = it
        })

    }

    private fun startDeleteDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete recipe?")
            .setPositiveButton("Yes") { _, _ ->
                listForFragment?.get(position)?.let { recipesViewModel.deleteRecipe(it) }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

     private fun startViewDialog(position: Int) {
         fun disableEdit(view: View) {
             view.editText_editrecipe_title.keyListener = null
             view.editText_editrecipe_directions.keyListener = null
             view.editText_editrecipe_ingredients.keyListener = null
             view.editText_editrecipe_notes.keyListener = null
             view.ratingBar_edit.setIsIndicator(true)
         }

         AlertDialog.Builder(requireContext()).setTitle("View recipe?")
            .setPositiveButton("Yes") { _, _ ->
                //get relevant data from current recipe
                val recipe = listForFragment?.get(position)
                //create a dialog that shows this data in an inflated layout
                val viewDialog = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.fragment_edit_or_view, null)
                view.editText_editrecipe_directions.setText(recipe?.directions)
                view.editText_editrecipe_ingredients.setText(recipe?.ingredients)
                view.editText_editrecipe_notes.setText(recipe?.notes)
                view.editText_editrecipe_title.setText(recipe?.title)
                view.textView_date_edit.text = recipe?.date
                disableEdit(view)
                recipe?.rating.apply { view.ratingBar_edit.rating = recipe?.rating!! }
                Glide.with(this)
                    .load(recipe?.imageOne)
                    .into(view.imageView_addphoto_edit)
                viewDialog.setView(view).show()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
        }


    private fun startEditDialog(position: Int){
        AlertDialog.Builder(context).setTitle("Edit recipe?")
            .setPositiveButton("Yes") { _, _ ->
                //get relevant data from current recipe
                val recipe = listForFragment?.get(position)
                val idString = recipe?.id.toString()
                recipesViewModel.setId(idString)
                recipesViewModel.getRecipeById2(idString)

                val controller = findNavController()
                if (recipe != null) {
                    controller.navigate(ViewRecipesFragmentDirections.actionNavViewrecipesToNavAddrecipe(
                        recipe.id.toString()))
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_menu , menu)

        val searchView = SearchView((context as MainActivity))
        menu.findItem(R.id.search_view_menu).apply {
            actionView = searchView
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                   recipesViewModel.searchString = query
                   recipesViewModel.getRecipesFromQuery(recipesViewModel.searchString)
                   changeRecyclerView()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    recipesViewModel.searchString = newText
                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.viewmenu_refresh -> {
                initRecyclerView()
                return true
            }
        }
        return false
    }
}


