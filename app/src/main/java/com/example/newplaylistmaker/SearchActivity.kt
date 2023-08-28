package com.example.newplaylistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var editText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        lateinit var adapter: TracksAdapter

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

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                editText = buttonSearchInputText.text.toString()
                buttonClearSearch.isVisible = editText.isNotEmpty()
                /*if (editText.isEmpty()) {
                    buttonClearSearch.visibility = View.GONE
                } else {
                    buttonClearSearch.visibility = View.VISIBLE }
                 */


            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        buttonSearchInputText.setOnClickListener {
            buttonSearchInputText.setText("")
        }
        buttonSearchInputText.addTextChangedListener(textWatcher)



        // Работаем с RecyclerView
        val trackList = ArrayList<Track>()

        trackList.add(
            Track("Long track name Long track name Long track name Long track name","Long artist name Long artist name Long artist name Long artist name","5:01",
                "")
        )

        trackList.add(
            Track("Smells Like Teen Spirit","Nirvana","5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg")
        )

        trackList.add(
            Track("Billie Jean","Michael Jackson","4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg")
        )

        trackList.add(
            Track("Stayin' Alive","Bee Gees","4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg")
        )

        trackList.add(
            Track("Whole Lotta Love","Led Zeppelin","5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg")
        )

        trackList.add(
            Track("Sweet Child O'Mine","Guns N' Roses","5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                /*val position = trackList.indexOf(track)
                trackList.remove(item)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, trackList.size)*/
            }
        }
        adapter = TracksAdapter(trackList, onItemClickListener)
        recyclerView.adapter = adapter

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

    private companion object {
        const val EDIT_TEXT = ""
    }
}

