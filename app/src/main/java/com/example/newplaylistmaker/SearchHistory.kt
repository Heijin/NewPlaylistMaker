package com.example.newplaylistmaker
import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory (private val sharedPref: SharedPreferences)
{
    fun saveTrackToHistory(track: Track) {
        var trackAdded = false
        val searchHistory = getSearchHistory()

        // Проверка на наличие ранее добавленного трека в список по trackId
        for (savedTrack in searchHistory) {
            if (savedTrack.trackId == track.trackId) {
                searchHistory.remove(savedTrack)
                searchHistory.add(track)
                trackAdded = true
                break
            }
        }

        if (!trackAdded) searchHistory.add(track)

        if (searchHistory.size > 10) searchHistory.remove(searchHistory[0])

        sharedPref.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(searchHistory))
            .apply()
    }

    fun getSearchHistory(): ArrayList<Track> {
        val json = sharedPref.getString(SEARCH_HISTORY, null) ?: return ArrayList<Track>()
        val arrayOfTracks = Gson().fromJson(json, Array<Track>::class.java)
        val tracks = ArrayList<Track>()
        for (track in arrayOfTracks)
            tracks.add(track)
        return tracks
    }

    fun clearSearchHistory(searchHistoryTracks: ArrayList<Track>) {

        searchHistoryTracks.clear()

        sharedPref.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(ArrayList<Track>()))
            .apply()
    }
}