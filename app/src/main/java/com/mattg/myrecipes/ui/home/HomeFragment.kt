package com.mattg.myrecipes.ui.home


import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.mattg.myrecipes.MainActivity
import com.mattg.myrecipes.R
import com.mattg.myrecipes.ui.viewrecipes.ApiClickListener
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: ApiRecipeAdapter
    private lateinit var clickListener: ApiClickListener
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            //create a list of all views that need to have their visibility attribute changed
            //so they can be referenced as a group
        val homeScreenViews = listOf<View>(
            mexican_button_layout,
            thai_button_layout,
            american_button_layout,
            breakfast_button_layout,
            dessert_button_layout,
            indian_button_layout,
            chinese_button_layout
        )

        val recycler = rv_home_apirecipes

        progressBar_home.visibility = View.INVISIBLE
        //observe viewModel flag (whether an api call has been made)
        homeViewModel.hasCalled.observe(viewLifecycleOwner, {
            if (it) {
                //if call has been made, show the list, not the options for searching
                adapter = ApiRecipeAdapter(requireContext(), clickListener)
                recycler.adapter = adapter
                recycler.visibility = View.VISIBLE
                setViewsGone(homeScreenViews)
            } else {
                //if a call has not been made, make views visible so they can be clicked
                setViewsVisible(homeScreenViews)
                recycler.visibility = View.GONE
            }
        })
        homeViewModel.resultList.observe(viewLifecycleOwner, {
            adapter.apply {
                setList(it)
                clickListener = ApiClickListener { id, _ ->
                    //get a reference to the current result to send to fragment
                    // define action with id passed to nav args
                    val args = HomeFragmentDirections.actionNavHomeToViewApiRecipeFragment(id)
                    //use action to navigate
                    Navigation.findNavController(requireParentFragment().requireView())
                        .navigate(args)
                }
            }
        })
        setOnClickListeners(homeScreenViews)
    }

    private fun setViewsVisible(homeScreenViews: List<View>) {
        for (buttonView in homeScreenViews) {
                buttonView.visibility = View.VISIBLE
            }
    }

    private fun setViewsGone(homeScreenViews: List<View>) {
        for (buttonView in homeScreenViews) {
            buttonView.visibility = View.GONE
        }
    }
    private fun getTitleString(tv: TextView): String {
        return tv.text.toString().toLowerCase(Locale.ROOT)
    }

    private fun setOnClickListeners(homeScreenViews: List<View>){
        val recycler = rv_home_apirecipes
        clickListener = ApiClickListener{id, _ ->
            //define action with id passed to nav args
            val args = HomeFragmentDirections.actionNavHomeToViewApiRecipeFragment(id)
            //use action to navigate
            Navigation.findNavController(requireParentFragment().requireView()).navigate(args)
        }
        adapter = ApiRecipeAdapter(requireContext(), clickListener)
        recycler.adapter = adapter

        setHomeScreenItemsClickListeners(recycler, homeScreenViews)

    }

    private fun viewOnClickedMethod(
        recycler: RecyclerView,
        homeScreenViews: List<View>,
        textView: TextView
    ) {
        val string = getTitleString(textView)
        //setting the flag in the viewModel to inform if a call has been made
        homeViewModel.setHasCalled()
        homeViewModel.getHomeScreenRecipes(string)
        //linking up the progress bar all the way to repository via viewModel from here
        homeViewModel.progressState()
        homeViewModel.showProgress.observe(viewLifecycleOwner, {
            if (it) {
                progressBar_home.visibility = View.VISIBLE
            } else
                progressBar_home.visibility = View.INVISIBLE
        })

        recycler.visibility = View.VISIBLE
        setViewsGone(homeScreenViews)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)

        val searchView = SearchView((context as MainActivity))
        menu.findItem(R.id.search_home_menu).apply {
            actionView = searchView
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    homeViewModel.searchString = query
                }
                homeViewModel.fetchSearchResults(searchString = homeViewModel.searchString)
                hideHomeDefaultItems()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    homeViewModel.searchString = newText
                }
                return false
            }
        })
    }

    private fun hideHomeDefaultItems() {
        american_button_layout.visibility = View.GONE
        thai_button_layout.visibility = View.GONE
        chinese_button_layout.visibility = View.GONE
        breakfast_button_layout.visibility = View.GONE
        dessert_button_layout.visibility = View.GONE
        mexican_button_layout.visibility = View.GONE
        indian_button_layout.visibility = View.GONE
        rv_home_apirecipes.visibility = View.VISIBLE
    }

    private fun setHomeScreenItemsClickListeners(
        recycler: RecyclerView,
        homeScreenViews: List<View>
    ) {
        //animation for these items
        val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)

        mexican_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_mexican_title)
            it.startAnimation(bounceAnimation)
        }
        american_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_american_title)
            it.startAnimation(bounceAnimation)
        }
        dessert_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_dessert_title)
            it.startAnimation(bounceAnimation)
        }
        breakfast_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_breakfast_title)
            it.startAnimation(bounceAnimation)
        }
        chinese_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_chinese_title)
            it.startAnimation(bounceAnimation)
        }
        thai_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_thai_title)
            it.startAnimation(bounceAnimation)
        }

        indian_button_layout.setOnClickListener {
            viewOnClickedMethod(recycler, homeScreenViews, tv_indian_title)
            it.startAnimation(bounceAnimation)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homemenu_refresh -> {
                homeViewModel.resetHasCalled()
                return true
            }
        }
        return false
    }

}