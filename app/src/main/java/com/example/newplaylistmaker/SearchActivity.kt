package com.example.newplaylistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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

        val sharedPrefs: SharedPreferences = getSharedPreferences(APP_CONFIG, MODE_PRIVATE)
        // Элементы разметки
        val historyLayout = findViewById<LinearLayout>(R.id.history_Layout)
        val resultLayout = findViewById<FrameLayout>(R.id.result_Layout)
        val buttonSearchInputText = findViewById<EditText>(R.id.search_input_text)
        val buttonClearHistory = findViewById<Button>(R.id.search_history_clear)
        val buttonBack = findViewById<Button>(R.id.id_search_back)
        val buttonClearSearch = findViewById<ImageView>(R.id.search_clearIcon)
        val errorButtonRefresh = findViewById<Button>(R.id.button_refresh)
        val errorLayout = findViewById<LinearLayout>(R.id.error_layout)
        errorLayout.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.search_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val trackList = ArrayList<Track>()
        val searchHistoryTracks = ArrayList<Track>()

        val itunesService = retrofit.create(ItunesApi::class.java)

        lateinit var adapter: TracksAdapter
        lateinit var adapterHistory: TracksAdapter

        buttonBack.setOnClickListener {
            finish()
        }

        buttonClearHistory.setOnClickListener {
            SearchHistory(sharedPrefs).clearSearchHistory(searchHistoryTracks)
            showSearchHistory(
                false,
                searchHistoryTracks,
                adapterHistory,
                resultLayout,
                historyLayout,
                sharedPrefs
            )
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
                showSearchHistory(
                    editText.isEmpty() && buttonSearchInputText.hasFocus(),
                    searchHistoryTracks,
                    adapterHistory,
                    resultLayout,
                    historyLayout,
                    sharedPrefs
                )
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

        buttonSearchInputText.setOnFocusChangeListener { _, hasFocus ->
            showSearchHistory(
                hasFocus && buttonSearchInputText.text.isEmpty(),
                searchHistoryTracks,
                adapterHistory,
                resultLayout,
                historyLayout,
                sharedPrefs
            )
        }

        // RecyclerView результата поиска
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                SearchHistory(sharedPrefs).saveTrackToHistory(item)
            }
        }
        adapter = TracksAdapter(trackList, onItemClickListener)
        recyclerView.adapter = adapter

        // RecyclerView истории поиска
        val recyclerViewHistory = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        adapterHistory = TracksAdapter(searchHistoryTracks)
        recyclerViewHistory.adapter = adapterHistory
    }

    private fun showSearchHistory(showHistory: Boolean,
                                  searchHistoryTracks: ArrayList<Track> ,
                                  adapter: TracksAdapter,
                                  resultLayout: FrameLayout,
                                  historyLayout: LinearLayout,
                                  sharedPrefs: SharedPreferences ) {

        // Если не нужно показывать историю, просто переключим слои.
        if (!showHistory) {
            resultLayout.visibility = View.VISIBLE
            historyLayout.visibility = View.GONE
            return
        }
        searchHistoryTracks.clear()
        searchHistoryTracks.addAll(SearchHistory(sharedPrefs).getSearchHistory().reversed())
        if (searchHistoryTracks.size == 0) return

        resultLayout.visibility = View.GONE
        historyLayout.visibility = View.VISIBLE
        adapter.notifyDataSetChanged()
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

