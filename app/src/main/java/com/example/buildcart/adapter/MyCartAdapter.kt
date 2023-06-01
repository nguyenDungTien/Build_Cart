package com.example.buildcart.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buildcart.R
import com.example.buildcart.evenbus.UpdateCartEvent
import com.example.buildcart.model.CartModel
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus

class MyCartAdapter(
    private val context: Context,
    private val cartModelList: List<CartModel>
) : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnMinus: ImageView? = null
        var btnPlus: ImageView? = null
        var imageView: ImageView? = null
        var btnDelete: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null
        var txtQuantity: TextView? = null

        init {
            btnMinus = itemView.findViewById(R.id.btnMinus) as ImageView
            btnPlus = itemView.findViewById(R.id.btnPlus) as ImageView
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView
            btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].image).skipMemoryCache(true)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuffer().append(cartModelList[position].name)
        holder.txtPrice!!.text = StringBuffer("$").append(cartModelList[position].price)
        holder.txtQuantity!!.text = StringBuffer("").append(cartModelList[position].quantity)

        //event
        holder.btnMinus!!.setOnClickListener {  minusCartItem(holder, cartModelList[position]) }
        holder.btnPlus!!.setOnClickListener {  plusCartItem(holder, cartModelList[position]) }
        holder.btnDelete!!.setOnClickListener {

            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Do you want to delete item")
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("DELETE") { dialog, _ ->
                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance()
                        .getReference("Cart")
                        .child("UNIQUE_USER_ID")
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(UpdateCartEvent())
                        }
                }
                .create()
            dialog.show()
        }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {

            cartModel.quantity += 1
            cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toFloat()
            holder.txtQuantity!!.text = StringBuffer("").append(cartModel.quantity)
            updateFirebase(cartModel)


    }

    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if (cartModel.quantity > 1) {
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toFloat()
            holder.txtQuantity!!.text = StringBuffer("").append(cartModel.quantity)
            updateFirebase(cartModel)

        }
    }

    private fun updateFirebase(cartModel: CartModel) {
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID")
            .child(cartModel.key!!).setValue(cartModel)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }

    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }
}


