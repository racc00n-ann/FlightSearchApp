package com.example.flightsearch.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightsearch.data.repository.FlightRepository
import com.example.flightsearch.data.repository.UserPreferencesRepository

class FlightSearchViewModelFactory(
    private val flightRepository: FlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
            return FlightSearchViewModel(flightRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}