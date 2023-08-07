package com.example.newplaylistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    var editText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val buttonSearchInputText = findViewById<EditText>(R.id.search_input_text)

        val buttonBack = findViewById<Button>(R.id.id_search_back)
        buttonBack.setOnClickListener {
            finish()
        }

        val buttonClearSearch = findViewById<ImageView>(R.id.search_clearIcon)
        buttonClearSearch.setOnClickListener {
            buttonSearchInputText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(buttonClearSearch.windowToken, 0)
        }


        buttonBack.setOnClickListener {
            finish()
        }

        val TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                editText = buttonSearchInputText.text.toString()

                if (editText.isEmpty()) {
                    buttonClearSearch.visibility = View.GONE
                } else {
                    buttonClearSearch.visibility = View.VISIBLE }

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        buttonSearchInputText.setOnClickListener {
            buttonSearchInputText.setText("")
        }
        buttonSearchInputText.addTextChangedListener(TextWatcher)


    }

    companion object {
        const val EDIT_TEXT = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, editText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editText = savedInstanceState.getString(EDIT_TEXT, "")
        findViewById<EditText>(R.id.search_input_text).setText(editText)
    }

}

