package com.mattg.myrecipes.ui.home


import android.os.Bundle
import android.view.*
import android.view.animation.Animation
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
import kotlinx.android.synthetic.main.fragment_home_v2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: ApiRecipeAdapter
    private lateinit var clickListener: ApiClickListener
    private lateinit var homeScreenViews: List<TextView>
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    //animation for items
    private lateinit var fadeAnimation : Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        fadeAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_v2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //create a list of all views that need to have their visibility attribute changed

        homeScreenViews = listOf<TextView>(
                (tv_breakfast_titlev2),
               (tv_brunch_titlev2),
                (tv_lunch_titlev2),
                (tv_dinner_titlev2),
                (tv_appetizer_titlev2),
                (tv_dessert_titlev2)
        )

        progressBar_homev2.visibility = View.INVISIBLE

        observeViewModel()

        setOnClickListeners(homeScreenViews)
    }

    private fun observeViewModel() {
        val recycler = rv_home_apirecipesv2


        //observe view model flag (whether an api call has been made)
        homeViewModel.hasCalled.observe(viewLifecycleOwner, {
            if (it) {
                //if call has been made, show the list
                adapter = ApiRecipeAdapter(clickListener)
                recycler.adapter = adapter
                recycler.visibility = View.VISIBLE
                setViewsGone(homeScreenViews)
            } else {
                //if a call has not been made, make views visible
                setViewsVisible(homeScreenViews)
                recycler.visibility = View.GONE
            }
        })
        homeViewModel.resultList.observe(viewLifecycleOwner, {
            adapter.apply {
                setList(it)
                clickListener = ApiClickListener { id, _ ->
                    // define action with id passed to nav args
                    val args = HomeFragmentDirections.actionNavHomeToViewApiRecipeFragment(id)
                    //use action to navigate
                    Navigation.findNavController(requireParentFragment().requireView())
                            .navigate(args)
                }
            }
        })
    }

    private fun setViewsVisible(homeScreenViews: List<TextView>) {
        for (view in homeScreenViews) {
            view.visibility = View.VISIBLE

        }
    }

private fun setViewsGone(homeScreenViews: List<TextView>) {
    for (view in homeScreenViews) {
        view.visibility = View.GONE

    }
}
    private fun getTitleString(tv: TextView): String {
        return tv.text.toString().toLowerCase(Locale.ROOT)
    }

    private fun setOnClickListeners(homeScreenViews: List<TextView>){
        val recycler = rv_home_apirecipesv2
        clickListener = ApiClickListener{id, _ ->
            //define action with id passed to nav args
            val args = HomeFragmentDirections.actionNavHomeToViewApiRecipeFragment(id)
            //use action to navigate
            Navigation.findNavController(requireParentFragment().requireView()).navigate(args)
        }
        adapter = ApiRecipeAdapter(clickListener)
        recycler.adapter = adapter

        setHomeScreenItemsClickListeners(recycler, homeScreenViews)

    }

private fun viewOnClickedMethod(
        recycler: RecyclerView,
        homeScreenViews: List<TextView>,
        textView: TextView

) {
    val string = getTitleString(textView)
    //setting the flag in the viewModel to inform if a call has been made
    homeViewModel.setHasCalled()
    coroutineScope.launch {
        homeViewModel.getHomeScreenRecipes(string)
    }
    //linking up the progress bar
    homeViewModel.progressState()
    homeViewModel.showProgress.observe(viewLifecycleOwner, {
        if (it) {
            progressBar_homev2.visibility = View.VISIBLE
        } else
            progressBar_homev2.visibility = View.INVISIBLE
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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    homeViewModel.searchString = query
                }

                homeViewModel.setLoading()


                homeViewModel.fetchSearchResults(searchString = homeViewModel.searchString)



                homeViewModel.setSearchResults()

                hideHomeDefaultItems(homeScreenViews)
                rv_home_apirecipes.visibility = View.VISIBLE

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

    private fun hideHomeDefaultItems(views: List<TextView>) {
        for (view in views) {
            view.visibility = View.GONE

        }
    }

    private fun setHomeScreenItemsClickListeners(
            recycler: RecyclerView,
            homeScreenViews: List<TextView>
    ) {
        //animation for these items
        //  val fadeAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
            for(view in homeScreenViews){
                view.setOnClickListener {
                    viewOnClickedMethod(recycler, homeScreenViews, it as TextView)
                    it.startAnimation(fadeAnimation)
                }
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