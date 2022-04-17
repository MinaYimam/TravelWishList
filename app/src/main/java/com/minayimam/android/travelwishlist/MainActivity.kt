package com.minayimam.android.travelwishlist

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), OnListItemClickedListener, OnDataChangedListener {

    private lateinit var placeListRecyclerView: RecyclerView
    private lateinit var newPlaceEditText: EditText
    private lateinit var addNewPlaceButton: Button

    private lateinit var placesRecyclerAdapter: PlaceRecyclerAdapter
    private lateinit var reasonToVisitEditText: EditText

    private val placesListModel: PlacesViewModel by lazy {
        ViewModelProvider(this).get(PlacesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        placeListRecyclerView = findViewById(R.id.place_list)
        newPlaceEditText = findViewById(R.id.new_place_name)
        addNewPlaceButton = findViewById(R.id.add_new_place_button)
        reasonToVisitEditText = findViewById(R.id.reason_to_visit)

        val places = placesListModel.getPlaces()


        placesRecyclerAdapter = PlaceRecyclerAdapter(places, this)
        placeListRecyclerView.layoutManager = LinearLayoutManager(this)
        placeListRecyclerView.adapter = placesRecyclerAdapter


        ItemTouchHelper(OnListItemSwipeListener( this))
            .attachToRecyclerView(placeListRecyclerView)

        addNewPlaceButton.setOnClickListener {
            addNewPlace()
        }

    }

    private fun addNewPlace() {
        val placeName = newPlaceEditText.text.toString()
        val name = placeName.trim()
        val reason = reasonToVisitEditText.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a place name", Toast.LENGTH_SHORT).show()
        }
        //else {
            //val place = Place(name)
           // val positionAdded = placesListModel.addNewPlace(place)
        else if (reason.isEmpty()) {
                Toast.makeText(this, "Enter reason you want to visit", Toast.LENGTH_SHORT).show()
        } else {
            val newPlace = Place(name, reason)
            val positionAdded = placesListModel.addNewPlace(newPlace)
            if (positionAdded == -1) {
                Toast.makeText(this, getString(R.string.already_entered), Toast.LENGTH_SHORT).show()
}
        }
        }

    override fun onListItemClicked(place: Place) {
        showMapForPlace(place)
    }

    private fun showMapForPlace(place: Place) {
        Toast.makeText(this, getString(R.string.showing_map_message, place.name), Toast.LENGTH_LONG).show()
        val placeLocationUri = Uri.parse("geo:0,0?q=${place.name}")
        val mapIntent = Intent(Intent.ACTION_VIEW, placeLocationUri)
        startActivity(mapIntent)
    }

    override fun onListItemMoved(from: Int, to: Int) {
        placesListModel.movePlace(from, to)
        placesRecyclerAdapter.notifyItemMoved(from, to)
    }

    override fun onListItemDeleted(position: Int) {


        val place = placesListModel.deletePlace(position)
        placesRecyclerAdapter.notifyItemRemoved(position)

        Snackbar.make(findViewById(R.id.container), getString(R.string.place_deleted, place.name), Snackbar.LENGTH_LONG)
            .setActionTextColor(resources.getColor(R.color.red))
            .setBackgroundTint(resources.getColor(R.color.black))
            .setAction(getString(R.string.undo_delete)) {
                placesListModel.addNewPlace(place, position)
                placesRecyclerAdapter.notifyItemInserted(position)
            }
            .show()


    }

    private fun clearForm() {
        newPlaceEditText.text.clear()
        reasonToVisitEditText.text.clear()
    }

    private fun hideKeyboard() {

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}