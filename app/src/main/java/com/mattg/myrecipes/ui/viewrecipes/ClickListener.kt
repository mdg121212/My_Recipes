package com.mattg.myrecipes.ui.viewrecipes

import com.mattg.myrecipes.db.Recipe
import com.mattg.myrecipes.models.responsePreset.Result


class ClickListener(val clickListener: (itemId: Int, itemPosition: Int, dialogInt: Int) -> Unit) {
fun onClickDelete(recipe: Recipe, position: Int, dialogInt: Int) = clickListener(recipe.id, position, dialogInt)
fun onClickEdit(recipe: Recipe, position: Int, dialogInt: Int) = clickListener(recipe.id, position, dialogInt)
fun onClickView(recipe: Recipe, position: Int, dialogInt: Int) = clickListener(recipe.id, position, dialogInt)
}

class ApiClickListener(val clickListener: (itemId: Int, position: Int) -> Unit) {
    fun onClick(result: Result, position: Int) = clickListener(result.id, position)
}