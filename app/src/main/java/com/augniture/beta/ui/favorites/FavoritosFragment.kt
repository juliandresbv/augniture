package com.augniture.beta.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.databinding.BaseUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentFavoriteProductsGridBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.general.detalleproducto.DetalleProductoFragment
import com.augniture.beta.ui.general.productos.ProductosAdapter
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.bumptech.glide.Glide

class FavoritosFragment : Fragment(), ItemClickListener<Producto> {

    private var _fragmentFavoriteProductsGridBinding: FragmentFavoriteProductsGridBinding? = null
    private val fragmentFavoriteProductsGridBinding get() = _fragmentFavoriteProductsGridBinding!!

    private var _baseUpperTopAppBarBinding: BaseUpperTopAppBarBinding? = null
    private val baseUpperTopAppBarBinding get() = _baseUpperTopAppBarBinding!!

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var productsRecyclerView: RecyclerView

    private lateinit var productosAdapter: ProductosAdapter

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentFavoriteProductsGridBinding = FragmentFavoriteProductsGridBinding.inflate(inflater, container, false)
        val productosFavoritosFragmentView = fragmentFavoriteProductsGridBinding.root

        // Init SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _baseUpperTopAppBarBinding = BaseUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewSearchUpperTopAppBar = baseUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewSearchUpperTopAppBar)

        val baseTopAppBarTitleTxt: TextView = baseUpperTopAppBarBinding.baseTopAppBarTitleTxt
        baseTopAppBarTitleTxt.text = "Favoritos"

        // Setup lower container for the TopAppBar
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        lowerSubContainer.removeAllViews()

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(
            ProductosViewModel::class.java)
        productsRecyclerView = fragmentFavoriteProductsGridBinding.favoriteProductsRecyclerViewGrid
        productsRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        productosAdapter =
            ProductosAdapter(
                itemRecyclerViewResourceId = R.layout.product_cardview_item_grid,
                glide = Glide.with(this),
                itemClickListener = this
            )
        productsRecyclerView.adapter = productosAdapter

        return productosFavoritosFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUsuarioId= sharedPreferences.getString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID, "")
        var usuarioActual = Usuario.USUARIO_VACIO
        usuarioActual.id = currentUsuarioId

        productosViewModel.productosFavoritos.observe(viewLifecycleOwner, Observer { itList ->
            val favoritos = itList

            val productosFavoritos= favoritos.map {
                val productoFavorito = it.referenciaProducto

                Producto(
                    productoFavorito?.id,
                    productoFavorito?.nombre,
                    productoFavorito?.imagen,
                    productoFavorito?.destacado,
                    productoFavorito?.descripcion,
                    productoFavorito?.categoria,
                    productoFavorito?.precio,
                    productoFavorito?.modeloar
                )
            }

            productosAdapter.update(productosFavoritos)
        })
        productosViewModel.loadProductosFavoritos(usuarioActual)
    }

    override fun onStop() {
        super.onStop()

        Log.i("FavoritosFragment: ", "FavoritosFragment onStop()")

        _fragmentFavoriteProductsGridBinding = null
        _baseUpperTopAppBarBinding = null

        productosViewModel.productosFavoritos.removeObservers(viewLifecycleOwner)
    }

    override fun onItemClicked(t: Producto) {
        //productosViewModel.setProductoSeleccionado(t)

        val detalleProductoFragment = DetalleProductoFragment(t)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentMain, detalleProductoFragment, DetalleProductoFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}