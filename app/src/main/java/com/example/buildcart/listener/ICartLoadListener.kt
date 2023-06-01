package com.example.buildcart.listener

import com.example.buildcart.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModelList:List<CartModel>)
    fun onLoadCartFailed(message:String?)
}