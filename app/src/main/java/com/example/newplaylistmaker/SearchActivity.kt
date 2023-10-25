package com.example.newplaylistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newplaylistmaker.databinding.ActivitySearchBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var isClickAllowed = true
    private var editText = ""
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs: SharedPreferences = getSharedPreferences(APP_CONFIG, MODE_PRIVATE)

        binding.idSearchBack.setNavigationOnClickListener {
            finish()
        }

        binding.errorLayout.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.search_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val trackList = ArrayList<Track>()
        val searchHistoryTracks = ArrayList<Track>()

        val itunesService = retrofit.create(ItunesApi::class.java)

        lateinit var adapter: TracksAdapter
        lateinit var adapterHistory: TracksAdapter

        val startSearchRunnable = Runnable {
            startSearchByText(
                itunesService,
                trackList,
                adapter
            )
        }

        fun startSearchDebounce() {
            handler.removeCallbacks(startSearchRunnable)
            handler.postDelayed(startSearchRunnable, SEARCH_DEBOUNCE_DELAY)
        }

        binding.searchHistoryClear.setOnClickListener {
            SearchHistory(sharedPrefs).clearSearchHistory(searchHistoryTracks)
            showSearchHistory(
                false,
                searchHistoryTracks,
                adapterHistory,
                sharedPrefs
            )
        }

        binding.buttonRefresh.setOnClickListener {
            startSearchByText(
                itunesService,
                trackList,
                adapter
            )
        }

        binding.searchClearIcon.setOnClickListener {
            binding.searchInputText.setText("")
            trackList.clear()
            adapter.notifyDataSetChanged()
            binding.errorLayout.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchClearIcon.windowToken, 0)
        }
        binding.searchInputText.doOnTextChanged { _, _, _, _ ->

            editText = binding.searchInputText.text.toString()
            binding.searchClearIcon.isVisible = editText.isNotEmpty()
            showSearchHistory(
                editText.isEmpty() && binding.searchInputText.hasFocus(),
                searchHistoryTracks,
                adapterHistory,
                sharedPrefs
            )
            startSearchDebounce()
        }

        binding.searchInputText.setOnFocusChangeListener { _, hasFocus ->
            showSearchHistory(
                hasFocus && binding.searchInputText.text.isEmpty(),
                searchHistoryTracks,
                adapterHistory,
                sharedPrefs
            )
        }

        // RecyclerView результата поиска

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {

                if (clickDebounce()) {
                    SearchHistory(sharedPrefs).saveTrackToHistory(item)
                    startActivity(
                        Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                            .putExtra(TRACK_JSON, Gson().toJson(item))
                    )
                }
            }
        }
        adapter = TracksAdapter(trackList, onItemClickListener)
        binding.recyclerView.adapter = adapter

        // RecyclerView истории поиска

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)

        val onItemClickListenerHistory = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                if (clickDebounce()) {
                    startActivity(
                        Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                            .putExtra(TRACK_JSON, Gson().toJson(item))
                    )

                }
            }
        }

        adapterHistory = TracksAdapter(searchHistoryTracks, onItemClickListenerHistory)
        binding.recyclerViewHistory.adapter = adapterHistory
    }

    private fun showSearchHistory(
        showHistory: Boolean,
        searchHistoryTracks: ArrayList<Track>,
        adapter: TracksAdapter,
        sharedPrefs: SharedPreferences
    ) {

        // Если не нужно показывать историю, просто переключим слои.
        if (!showHistory) {
            binding.resultLayout.visibility = View.VISIBLE
            binding.historyLayout.visibility = View.GONE
            return
        }
        searchHistoryTracks.clear()
        searchHistoryTracks.addAll(SearchHistory(sharedPrefs).getSearchHistory().reversed())
        if (searchHistoryTracks.size == 0) return

        binding.resultLayout.visibility = View.GONE
        binding.historyLayout.visibility = View.VISIBLE
        adapter.notifyDataSetChanged()
    }

    private fun startSearchByText(
        itunesService: ItunesApi,
        trackList: ArrayList<Track>,
        adapter: TracksAdapter
    ) {

        val textForSearch = binding.searchInputText.text.toString().trim()

        // Не делаем запросы в сеть если пустой ввод
        if (textForSearch.isEmpty()) return

        binding.progressBar.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE

        itunesService.search(textForSearch).enqueue(object :
            Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                trackList.clear()

                if (response.code() == 200) {

                    if (response.body()?.results?.isNotEmpty() == true) {
                        binding.errorLayout.visibility = View.GONE
                        val result = response.body()?.results

                        if (result !== null)
                            trackList.addAll(result)

                        adapter.notifyDataSetChanged()
                    } else {
                        onFailureSearch(true)
                    }

                } else {
                    onFailureSearch(false)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                onFailureSearch(false)
            }
        }
        )
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onFailureSearch(
        isEmpty: Boolean
    ) {
        binding.errorLayout.visibility = View.VISIBLE

        if (isEmpty) {
            binding.errorIcon.setImageDrawable(getDrawable(R.drawable.vector_group_174))
            binding.errorText.text = getString(R.string.empty_search_text)
            binding.buttonRefresh.visibility = View.GONE
        } else {
            binding.errorIcon.setImageDrawable(getDrawable(R.drawable.vector_group_175))
            binding.errorText.text = getString(R.string.on_error_search_text)
            binding.buttonRefresh.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, editText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editText = savedInstanceState.getString(EDIT_TEXT, "")
        binding.searchInputText.setText(editText)
    }

    private companion object {
        const val EDIT_TEXT = "edit_text"
        const val TRACK_JSON = "track_json"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}

