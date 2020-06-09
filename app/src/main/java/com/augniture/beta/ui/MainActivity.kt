package com.augniture.beta.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.augniture.beta.R
import com.augniture.beta.databinding.ActivityMainBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.home.HomeFragment
import com.augniture.beta.ui.home.featuredproducts.CarritoFragment
import com.augniture.beta.ui.search.FavoritosFragment
import com.augniture.beta.ui.search.BusquedaFragment
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.google.android.material.bottomappbar.BottomAppBar

class MainActivity : AppCompatActivity() {

    // View binding properties
    private var _mainActivityBinding: ActivityMainBinding? = null
    val mainActivityBinding get() = _mainActivityBinding!!

    private lateinit var optionsMenu: Menu

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var productosViewModel: ProductosViewModel

    private var networkManager: NetworkManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        val mainActivityView = mainActivityBinding.root

        setContentView(mainActivityView)

        // NetworkManager (Broadcast Receiver for Network changes)
        networkManager = NetworkManager(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)

        registerReceiver(networkManager, intentFilter)

        //Log.i("MAINACT", "Llego Main Act")

        sharedPreferences = getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        productosViewModel = ViewModelProviders.of(this, AugnitureViewModelFactory).get(ProductosViewModel::class.java)

        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        distributeIcons()
        setupARButton()
    }

    override fun onStart() {
        super.onStart()

        // Load favorites
        var usuarioActual = Usuario.USUARIO_VACIO
        usuarioActual.id = sharedPreferences.getString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID, "")

        if (usuarioActual.id != "") { productosViewModel.loadProductosFavoritos(usuarioActual) }

        // Initial call to start HomeFragment
        initHomeFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_nav_menu, menu)
        optionsMenu = menu

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        changeColors(item!!.itemId)
        var fragment: Fragment? = null

        when (item.itemId) {
            R.id.navigation_home -> {
                fragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.simpleName) ?: HomeFragment()
            }
            R.id.navigation_search -> {
                fragment = supportFragmentManager.findFragmentByTag(BusquedaFragment::class.simpleName) ?: BusquedaFragment()
            }
            R.id.navigation_favourites -> {
                fragment = supportFragmentManager.findFragmentByTag(FavoritosFragment::class.simpleName) ?: FavoritosFragment()
            }
            R.id.navigation_shopping_cart -> {
                fragment = supportFragmentManager.findFragmentByTag(CarritoFragment::class.simpleName) ?: CarritoFragment()
            }
        }

        if (fragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)

            when (fragment) {
                is HomeFragment -> {
                    fragmentTransaction.replace(R.id.contentMain, fragment, HomeFragment::class.simpleName)
                }
                is BusquedaFragment -> {
                    fragmentTransaction.replace(R.id.contentMain, fragment, BusquedaFragment::class.simpleName)
                }
                is FavoritosFragment -> {
                    fragmentTransaction.replace(R.id.contentMain, fragment, FavoritosFragment::class.simpleName)
                }
                else -> {
                    fragmentTransaction.replace(R.id.contentMain, fragment, CarritoFragment::class.simpleName)
                }
            }

            fragmentTransaction.commit()
        }

        return false
    }

    override fun onStop() {
        super.onStop()

        Log.i("MainActivity: ", "MainActivity onStop()")

        _mainActivityBinding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        if (networkManager != null) { unregisterReceiver(networkManager) }
    }

    private fun initHomeFragment() {
        val homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.simpleName) ?: HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)

        fragmentTransaction.replace(R.id.contentMain, homeFragment, HomeFragment::class.simpleName)
        fragmentTransaction.commit()
    }

    private fun distributeIcons() {
        val bottomAppBar: BottomAppBar = mainActivityBinding.bottomAppBar
        setSupportActionBar(bottomAppBar)

        if (bottomAppBar.childCount > 0) {
            val actionMenuView = bottomAppBar.getChildAt(0) as androidx.appcompat.widget.ActionMenuView
            actionMenuView.layoutParams.width = androidx.appcompat.widget.ActionMenuView.LayoutParams.MATCH_PARENT
        }
    }

    private fun setupARButton() {
        /*
        arBtn.setOnClickListener { view ->
            changeColors(view.id)
            val fragment = AugmentedFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentMain, fragment)
            transaction.commit()
        }
        */
    }

    private fun changeColors(id: Int) {
        //validateFab(id)
        Log.i("MainActivity: ", "MenuId: $id")

        validateHome(id, optionsMenu)
        validateSearch(id, optionsMenu)
        validateFavourites(id, optionsMenu)
        validateCart(id, optionsMenu)
    }

    private fun validateFab(id: Int) {
        /*
        val fab: View = mainActivityBinding.arBtn

        if (id == R.id.arBtn) {
            fab.setBackgroundResource(R.drawable.ic_photo)
        } else {
            fab.setBackgroundResource(R.drawable.ic_photo_no_selected)
        }
        */
    }

    private fun validateHome(id: Int, menu: Menu) {
        val item = menu.findItem(R.id.navigation_home)
        if (id == R.id.navigation_home) {
            item.setIcon(R.drawable.ic_home)
        } else {
            item.setIcon(R.drawable.ic_home_no_selected)
        }
    }

    private fun validateSearch(id: Int, menu: Menu) {
        val item = menu.findItem(R.id.navigation_search)
        if (id == R.id.navigation_search) {
            item.setIcon(R.drawable.ic_search)
        } else {
            item.setIcon(R.drawable.ic_search_no_selected)
        }
    }

    private fun validateFavourites(id: Int, menu: Menu) {
        val item = menu.findItem(R.id.navigation_favourites)
        if (id == R.id.navigation_favourites) {
            item.setIcon(R.drawable.ic_favourites)
        } else {
            item.setIcon(R.drawable.ic_favourites_no_selected)
        }
    }

    private fun validateCart(id: Int, menu: Menu) {
        val item = menu.findItem(R.id.navigation_shopping_cart)
        if (id == R.id.navigation_shopping_cart) {
            item.setIcon(R.drawable.ic_shopping_cart)
        } else {
            item.setIcon(R.drawable.ic_shopping_cart_no_selected)
        }
    }
}
