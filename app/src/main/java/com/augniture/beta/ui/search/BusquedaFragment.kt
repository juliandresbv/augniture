package com.augniture.beta.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.augniture.beta.R
import com.augniture.beta.databinding.BaseUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentSearchProductsGridBinding
import com.augniture.beta.databinding.SearchLowerTopAppBarBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Producto
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.general.detalleproducto.DetalleProductoFragment
import com.augniture.beta.ui.general.productos.ProductosAdapter
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.Glide

class BusquedaFragment : Fragment(), ItemClickListener<Producto> {

    private var _fragmentSearchProductsGridBinding: FragmentSearchProductsGridBinding? = null
    private val fragmentSearchProductsGridBinding get() = _fragmentSearchProductsGridBinding!!

    private var _baseUpperTopAppBarBinding: BaseUpperTopAppBarBinding? = null
    private val baseUpperTopAppBarBinding get() = _baseUpperTopAppBarBinding!!

    private var _searchLowerTopAppBar: SearchLowerTopAppBarBinding? = null
    private val searchLowerTopAppBar get() = _searchLowerTopAppBar!!

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var productosRecyclerView: RecyclerView

    private lateinit var productosAdapter: ProductosAdapter

    private lateinit var amplitudeInstance: AmplitudeClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        amplitudeInstance = Amplitude
            .getInstance()
            .initialize(requireActivity(), getString(R.string.amplitude_ak))
            .enableForegroundTracking(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentSearchProductsGridBinding = FragmentSearchProductsGridBinding.inflate(inflater, container, false)
        val productosBusquedaFragmentView = fragmentSearchProductsGridBinding.root

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _baseUpperTopAppBarBinding = BaseUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewSearchUpperTopAppBar = baseUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewSearchUpperTopAppBar)

        val baseTopAppBarTitleTxt: TextView = baseUpperTopAppBarBinding.baseTopAppBarTitleTxt
        baseTopAppBarTitleTxt.text = "Buscar"

        // Setup lower container for the TopAppBar
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        _searchLowerTopAppBar = SearchLowerTopAppBarBinding.inflate(inflater, lowerSubContainer, false)
        val viewSearchLowerTopAppBar = searchLowerTopAppBar.root
        lowerSubContainer.removeAllViews()
        lowerSubContainer.addView(viewSearchLowerTopAppBar)

        val searchView: SearchView = searchLowerTopAppBar.searchSearchView
        searchView.setOnClickListener {
            searchView.onActionViewExpanded()
        }

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(ProductosViewModel::class.java)
        productosRecyclerView = fragmentSearchProductsGridBinding.productsRecyclerViewGrid
        productosRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        productosAdapter = ProductosAdapter(
            itemRecyclerViewResourceId = R.layout.product_cardview_item_grid,
            glide = Glide.with(this),
            itemClickListener = this
        )
        productosRecyclerView.adapter = productosAdapter

        return productosBusquedaFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productosViewModel.productosBusqueda.observe(viewLifecycleOwner, Observer { itList ->
            val productos = itList

            productosAdapter.update(productos)
        })
        productosViewModel.loadProductosBusqueda("")

        val searchSearchView : SearchView = searchLowerTopAppBar.searchSearchView
        searchSearchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }

            override fun onQueryTextChange(newText: String): Boolean {
                productosViewModel.loadProductosBusqueda(newText)

                return true
            }
        })
    }

    override fun onStop() {
        super.onStop()

        Log.i("BusquedaFragment: ", "BusquedaFragment onStop()")

        _fragmentSearchProductsGridBinding = null
        _searchLowerTopAppBar = null
        _baseUpperTopAppBarBinding = null

        productosViewModel.productosBusqueda.removeObservers(viewLifecycleOwner)
    }

    override fun onItemClicked(t: Producto) {
        //productosViewModel.setProductoSeleccionado(t)

        val detalleProductoFragment = DetalleProductoFragment(t)
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentMain, detalleProductoFragment, DetalleProductoFragment::class.simpleName)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}