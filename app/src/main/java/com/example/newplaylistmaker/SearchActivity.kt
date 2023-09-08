package com.example.newplaylistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.ButtonBarLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var editText = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Элементы разметки
        val buttonSearchInputText = findViewById<EditText>(R.id.search_input_text)
        val buttonBack = findViewById<Button>(R.id.id_search_back)
        val buttonClearSearch = findViewById<ImageView>(R.id.search_clearIcon)
        val errorButtonRefresh = findViewById<Button>(R.id.button_refresh)
        val errorLayout = findViewById<LinearLayout>(R.id.error_layout)
        errorLayout.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.search_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var trackList = ArrayList<Track>()

        val itunesService = retrofit.create(ItunesApi::class.java)

        lateinit var adapter: TracksAdapter


        buttonBack.setOnClickListener {
            finish()
        }
        buttonBack.setOnClickListener {
            finish()
        }

        errorButtonRefresh.setOnClickListener {
            startSearchByText(
                itunesService,
                trackList,
                adapter,
                errorLayout,
                errorButtonRefresh,
                buttonSearchInputText
            )
        }

        buttonClearSearch.setOnClickListener {
            buttonSearchInputText.setText("")
            trackList.clear()
            adapter.notifyDataSetChanged()
            errorLayout.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(buttonClearSearch.windowToken, 0)
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
            //buttonSearchInputText.setText("")
        }
        buttonSearchInputText.addTextChangedListener(textWatcher)

        buttonSearchInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startSearchByText(
                    itunesService,
                    trackList,
                    adapter,
                    errorLayout,
                    errorButtonRefresh,
                    buttonSearchInputText
                )
            }
            false
        }

        // Работаем с RecyclerView
        //val trackList = ArrayList<Track>()

        /*trackList.add(
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
        )*/

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

    private fun startSearchByText(
        itunesService: ItunesApi,
        trackList: ArrayList<Track>,
        adapter: TracksAdapter,
        errorLayout: LinearLayout,
        errorButtonRefresh: Button,
        buttonSearchInputText: EditText
    ) {

        itunesService.search(buttonSearchInputText.text.toString()).enqueue(object :
            Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                trackList.clear()

                if (response.code() == 200) {

                    if (response.body()?.results?.isNotEmpty() == true) {
                        errorLayout.visibility = View.GONE
                        trackList.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                    } else {
                        onFailureSearch(errorLayout, errorButtonRefresh, true)
                    }

                } else {
                    onFailureSearch(errorLayout, errorButtonRefresh, false)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                onFailureSearch(errorLayout, errorButtonRefresh, false)
            }
        }
        )
    }

    private fun onFailureSearch(
        errorLayout: LinearLayout,
        errorButtonRefresh: Button,
        isEmpty: Boolean
    ) {
        errorLayout.visibility = View.VISIBLE

        val errorIcon = findViewById<ImageView>(R.id.error_icon)
        val errorText = findViewById<TextView>(R.id.error_text)

        if (isEmpty) {
            errorIcon.setImageDrawable(getDrawable(R.drawable.vector_group_174))
            errorText.text = getString(R.string.empty_search_text)
            errorButtonRefresh.visibility = View.GONE
        } else {
            errorIcon.setImageDrawable(getDrawable(R.drawable.vector_group_175))
            errorText.text = getString(R.string.on_error_search_text)
            errorButtonRefresh.visibility = View.VISIBLE
        }
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

