package com.example.shoppinglistapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LocationViewModel: ViewModel() {
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _address = mutableStateOf(listOf<GeocodingResults>())
    val address: State<List<GeocodingResults>> = _address
    fun updateLocation(newLocation:LocationData){
        _location.value = newLocation
    }


    fun fetchAddress(latlng: String){
        try{
            viewModelScope.launch {

                val result = RetrofitClient.create().getAddressFromCoordinates(
                        latlng,
                        apiKey = "AIzaSyDAuKogDh4deX26vF0nh_1MzjynpK4PFs4"
                    )
                _address.value = result.results

            }
        }catch (e: Exception){
            Log.d("res1", "${e.cause} ${e.message}")
        }
    }

}