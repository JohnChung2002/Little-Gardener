package com.example.littlegardener

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initUI(view)
        initListeners(view)
        return view
    }

    private fun initUI(view: View) {
        FirestoreHelper.getAccountInformation {
             view.findViewById<TextView>(R.id.email).text = it
        }
    }

    private fun initListeners(view: View) {
        view.findViewById<Button>(R.id.test).setOnClickListener {
            val intent = Intent(context, LiveChatActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<EditText>(R.id.edit_text_search).setOnEditorActionListener { textView, i, keyEvent ->
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