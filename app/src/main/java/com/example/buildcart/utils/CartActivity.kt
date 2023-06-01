package com.example.buildcart.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buildcart.R
import com.example.buildcart.adapter.MyCartAdapter
import com.example.buildcart.adapter.MyDrinkAdapter
import com.example.buildcart.evenbus.UpdateCartEvent
import com.example.buildcart.listener.ICartLoadListener
import com.example.buildcart.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartActivity : AppCompatActivity(), ICartLoadListener {
    var cartLoadListener:ICartLoadListener?=null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
     fun onUpdateCartEvent(event: UpdateCartEvent) {
        loadCartFromFirebase()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        init()
        loadCartFromFirebase()
    }

    private fun init() {
        cartLoadListener=this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart23!!.layoutManager = layoutManager
        recycler_cart23!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnBack!!.setOnClickListener { finish() }
    }

    private fun loadCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }

            })
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum =0.0
        for (cartModel in cartModelList!!){
            sum+=cartModel!!.totalPrice
        }
        txtTotal.text=StringBuilder("$").append(sum)
        val adapter=MyCartAdapter(this, cartModelList)
        recycler_cart23!!.adapter=adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
}