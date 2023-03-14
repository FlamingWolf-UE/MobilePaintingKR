package com.example.paintingapplab3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paintingapplab3.adapters.PicturesRecyclerViewAdapter
import com.example.paintingapplab3.databinding.ActivityHomeBinding
import com.example.paintingapplab3.databinding.ActivityMainBinding
import com.example.paintingapplab3.viewModel.HomePageViewModel

class HomeActivity : AppCompatActivity() {
    lateinit var viewModel : HomePageViewModel
    lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        viewModel = ViewModelProvider(this)[HomePageViewModel::class.java]

        val adapter : PicturesRecyclerViewAdapter = PicturesRecyclerViewAdapter(viewModel)
        binding.recyclerView.adapter = adapter



        viewModel.getAllHeroesWithFraction().observe(this) {
            adapter.setHeroes(it)
        }
        val activityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){

            }
        }
        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this@HomeActivity, createNewPictureActivity::class.java)

            activityLauncher.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem : MenuItem? = menu?.findItem(R.id.search_action)
        val searchView = (menuItem?.actionView) as SearchView
        searchView.queryHint = "Search by name"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.setFilter(newText)
                return true
            }
        })



        return super.onCreateOptionsMenu(menu)
    }

}