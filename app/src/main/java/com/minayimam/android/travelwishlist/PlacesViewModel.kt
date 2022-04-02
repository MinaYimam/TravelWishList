package com.minayimam.android.travelwishlist


import androidx.lifecycle.ViewModel

class PlacesViewModel: ViewModel() {


    private val placeNames = mutableListOf<Place>(Place("Addis Ababa"), Place("Hawassa, Ethiopia"), Place("Axum, Ethiopia"))

    fun getPlaces(): List<Place> {
        return placeNames
    }

    fun addNewPlace(place: Place, position: Int? = null): Int {

        if (placeNames.any { it.name.uppercase() == place.name.uppercase() }) {

            return -1
        }

        return if (position != null) {
            placeNames.add(position, place)
            position
        } else {

            placeNames.add(place)
            placeNames.lastIndex
        }
    }

    fun deletePlace(position: Int): Place {
        return placeNames.removeAt(position)
    }

    fun movePlace(from: Int, to: Int) {

        val place = placeNames.removeAt(from)

        placeNames.add(to, place)
    }


}