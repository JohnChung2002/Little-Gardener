package com.example.littlegardener

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    private lateinit var searchEditText: EditText
    private lateinit var viewStub: ViewStub

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initUI(view)
        initListeners()
        FirestoreHelper.getRole {
            if (it == "Admin") {
                loadAdminOptions(view)
            } else {
                loadUserOptions(view)
            }
        }
        return view
    }

    private fun initUI(view: View) {
        searchEditText = view.findViewById(R.id.edit_text_search)
        viewStub = view.findViewById(R.id.view_stub)
        FirestoreHelper.getRole {
            viewStub.layoutResource = if (it == "Admin") {
                R.layout.admin_home_view
            } else {
                R.layout.user_home_view
            }
            viewStub.inflate()
        }
    }

    private fun initListeners() {
        searchEditText.setOnEditorActionListener { textView, i, _ ->
            if ((i == EditorInfo.IME_ACTION_SEARCH) && (textView.text.isNotEmpty())) {
                textView.text = ""
                val intent = Intent(context, LiveChatActivity::class.java)
                intent.putExtra("search", textView.text.toString())
                startActivity(intent)
            }
            true
        }
    }

    private fun loadAdminOptions(view: View) {
        val addProductButton = view.findViewById<TextView>(R.id.create_product)
        addProductButton.setOnClickListener {
            val intent = Intent(context, CrudActivity::class.java)
            intent.putExtra("type", "add")
            startActivity(intent)
        }
        val editProductButton = view.findViewById<TextView>(R.id.edit_product)
        editProductButton.setOnClickListener {
            val intent = Intent(context, CrudActivity::class.java)
            intent.putExtra("type", "edit_list")
            startActivity(intent)
        }
        val deleteProductButton = view.findViewById<TextView>(R.id.delete_product)
        deleteProductButton.setOnClickListener {
            val intent = Intent(context, CrudActivity::class.java)
            intent.putExtra("type", "delete_list")
            startActivity(intent)
        }
    }

    private fun loadUserOptions(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.user_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = HomeCategoryAdapter()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}