package com.mattg.myrecipes.ui.addrecipe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mattg.myrecipes.BaseFragment
import com.mattg.myrecipes.R
import com.mattg.myrecipes.db.RecipeRepository
import com.mattg.myrecipes.db.RecipesDatabase
import com.mattg.myrecipes.ui.viewrecipes.RecipesViewModel
import com.mattg.myrecipes.utils.GlideApp
import kotlinx.android.synthetic.main.fragment_addrecipe.*
import kotlinx.android.synthetic.main.fragment_addrecipe.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


private const val TAKE_PICTURE_REQUEST_CODE = 1
private const val GALLERY_PICTURE_REQUEST_CODE = 2

//uri placeholders

private var galleryUri = ""
private var cameraUri = ""
private var wasGallery: Boolean = false
private var wasCamera: Boolean = false

//for args
private var wasLoaded: Boolean = false
private var isLeftover: Boolean = false

class AddRecipeFragment : BaseFragment() {

    private lateinit var recipesViewModel: RecipesViewModel
    private var uriToSave = ""
    private val args: AddRecipeFragmentArgs by navArgs()
    private var loadedId = "notloaded"
    private var loadedImage = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.fragment_addrecipe, container, false)

        val date = root.textView_date
        val image = root.imageView_addphoto
        val checkBox = root.toggle_leftovers

        //get the date
        val currentTime: String = SimpleDateFormat(
            " d MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        //set date
        date.text = currentTime

        image.setOnClickListener {
            startPictureDialog()
        }

        loadedId = args.id.toString()

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            isLeftover = when (isChecked) {
                true -> {
                    true
                }
                false -> {
                    false
                }
            }
        }

        val dao = RecipesDatabase.getInstance(requireContext()).recipeDao()
        //get repository with dao
        val repository = RecipeRepository(dao)
        recipesViewModel = RecipesViewModel(repository)
        return root
    }

    private fun startPictureDialog() {

        AlertDialog.Builder(requireContext())
            .setPositiveButton("Take Picture") { _, _ ->
                takePicture()
            }
            .setNegativeButton("Choose from gallery") { _, _ ->
                openGalleryGetImage()

            }.setNeutralButton("Cancel") { _, _ ->
                exitTransition
            }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            //making sure that from this fragment, when back button is pressed there is an option to save current recipe progress
            showSaveDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModelRecipe()
    }

    private fun showSaveDialog() {

        AlertDialog.Builder(requireContext()).setTitle("Do you want to save?")
            .setPositiveButton("Yes") { dialog, _ ->
                saveRecipe()
                findNavController().navigate(R.id.action_nav_addrecipe_to_nav_viewrecipes)
                dialog.dismiss()

            }
            .setNegativeButton("No") { dialog, _ ->
                findNavController().navigate(R.id.action_nav_addrecipe_to_nav_viewrecipes)
                dialog.dismiss()

            }.show()
    }

    private fun observeViewModelRecipe() {

        recipesViewModel.getRecipeById2(loadedId).observe(viewLifecycleOwner, {
            if (it != null) {
                wasLoaded = true
                editText_addrecipe_directions.setText(it.directions)
                if (it.imageOne != null || it.imageOne != "") {
                    uriToSave = it.imageOne.toString()
                    GlideApp.with(this)
                        .load(it.imageOne)
                        .error(R.drawable.ic_baseline_add_a_photo_24)
                        .into(imageView_addphoto)


                    loadedImage = it.imageOne.toString()
                }
                editText_addrecipe_ingredients.setText(it.ingredients)
                editText_addrecipe_title.setText(it.title)
                editText_addrecipe_notes.setText(it.notes)
                if (it.rating != null) {
                    ratingBar.rating = it.rating
                }
                loadedId = it.id.toString()
                if (it.isLeftover == true) {
                    isLeftover
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addrecipe, menu)

        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun saveRecipe() {

        when (wasGallery) {
            true -> {
                uriToSave = galleryUri
                wasGallery = false
            }
        }
        when (wasCamera) {
            true -> {
                uriToSave = cameraUri
                wasCamera = false
            }
        }

        recipesViewModel.saveUserRecipeToDb(
            editText_addrecipe_title.text.toString(),
            editText_addrecipe_ingredients.text.toString(),
            editText_addrecipe_directions.text.toString(),
            editText_addrecipe_notes.text.toString(),
            uriToSave,
            ratingBar.rating,
            textView_date.text.toString(),
            isLeftover,
            loadedId
        )

    }

    private fun openGalleryGetImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_PICTURE_REQUEST_CODE)
    }

    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, TAKE_PICTURE_REQUEST_CODE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_save_recipe -> {
                saveRecipe()
                findNavController().navigate(R.id.action_nav_addrecipe_to_nav_viewrecipes)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_PICTURE_REQUEST_CODE) {
            wasGallery = true
            wasCamera = false
            val uri = data?.data!!
            galleryUri = uri.toString()
            GlideApp.with(this)
                .load(uri)
                .into(imageView_addphoto)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PICTURE_REQUEST_CODE) {
            val result = data?.data
            wasCamera = true
            wasGallery = false
            val d = data?.extras?.get("data")
            val forPic = getImageUri(requireContext(), d as Bitmap)
            cameraUri = forPic.toString()
            GlideApp.with(this)
                .load(forPic)
                .into(imageView_addphoto)
        }

    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        @Suppress("DEPRECATION") val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

}