package com.mattg.myrecipes.db


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 * imageTwo and imageThree exist for:  TODO add multiple image capability
 */
@Entity(tableName = "recipes")
data class Recipe (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    val title: String?,
    @ColumnInfo
    val ingredients: String?,
    @ColumnInfo
    val directions: String?,
    @ColumnInfo
    val notes: String?,
    @ColumnInfo
    val imageOne: String?,
    @ColumnInfo
    val imageTwo: String?,
    @ColumnInfo
    val imageThree: String?,
    @ColumnInfo
    val rating: Float?,
    @ColumnInfo
    val date: String?,
    @ColumnInfo
    val isLeftover: Boolean?
) {
    //SECONDARY CONSTRUCTOR TO ALLOW RECIPES TO BE CREATED WITHOUT THE ID PARAMETER (AUTOINCREMENT)
    @Ignore
    constructor(
        title: String?,
        ingredients: String?,
        directions: String?,
        notes: String?,
        imageOne: String?,
        imageTwo: String?,
        imageThree: String?,
        rating: Float?,
        date: String?,
        isLeftover: Boolean?
    ) : this(
        0, title, ingredients,
        directions, notes, imageOne, imageTwo, imageThree, rating, date, isLeftover
    )




}