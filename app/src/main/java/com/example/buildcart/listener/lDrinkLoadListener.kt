package com.example.buildcart.listener

import com.example.buildcart.model.DrinkModel

interface lDrinkLoadListener {

    fun onDrinkLoadSuccess(drinkModelList:List<DrinkModel>?)
    fun onDrinkLoadFailed(message:String?)}