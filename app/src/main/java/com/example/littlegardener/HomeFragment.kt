package com.example.littlegardener

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService

class HomeFragment : Fragment() {
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initUI(view)
        initListeners()
        return view
    }

    private fun initUI(view: View) {
        searchEditText = view.findViewById(R.id.edit_text_search)
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

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}