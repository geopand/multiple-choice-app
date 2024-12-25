package com.panco.multichoice

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //set application's toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)  ///tells android to treat this as the default toolbar

        //we do this because calling findNavController() from an activity's onCreate() method can fail
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //get a reference to the navigation controller from the navigation host
        val navControler = navHostFragment.navController

        //this builds a configuration that links the toolbar to the navigation graph
        val builder = AppBarConfiguration.Builder(navControler.graph)
        val appBarConfiguration = builder.build()
        toolbar.setupWithNavController(navControler, appBarConfiguration)  //this applies the configuration to the toolbar

        //setup the navigation when clicking items on bottom nav bar
        val bottomNavMenu = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavMenu.setupWithNavController(navControler)
    }

    //implementing the method adds any items in the new resource file to the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) //inflates the menu resource file to MenuItem objects
        return super.onCreateOptionsMenu(menu)
    }

    //this makes the menu items respond to clicks. The 'item' parameter refers to the menuItem clicked
    //navigate to the NavDestination associated with the clicked MenuItem
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}