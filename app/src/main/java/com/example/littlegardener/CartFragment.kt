package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {
    private lateinit var cartAdapter: CartAdapter
    private var cartList: MutableList<Pair<String, HashMap<String, Int>>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCartListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val cartRecyclerView = view.findViewById<RecyclerView>(R.id.cart_recycler_view)
        cartRecyclerView.layoutManager = LinearLayoutManager(context)
        cartAdapter = CartAdapter(cartList)
        cartRecyclerView.adapter = cartAdapter
        return view
    }

    private fun initCartListener() {
        FirestoreHelper.initCart {
            val db = FirestoreHelper.getCurrCartDocument()
            db.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    cartList.clear()
                    for (i in snapshot.data!!.keys) {
                        cartList.add(Pair(i, snapshot.data!![i] as HashMap<String, Int>))
                    }
                    cartList.sortBy { it.first }
                    cartAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CartFragment()
    }
}