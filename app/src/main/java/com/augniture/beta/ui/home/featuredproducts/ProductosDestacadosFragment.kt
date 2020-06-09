package com.augniture.beta.ui.home.featuredproducts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.databinding.FragmentHomeProductsListBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Producto
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.general.detalleproducto.DetalleProductoFragment
import com.augniture.beta.ui.general.productos.ProductosAdapter
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.home.productcategories.CategoriasProductosViewModel
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.Glide

class ProductosDestacadosFragment() : Fragment(), ItemClickListener<Producto> {

    private var _fragmentHomeProductsListBinding: FragmentHomeProductsListBinding? = null
    private val fragmentHomeProductsListBinding get() = _fragmentHomeProductsListBinding!!

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var categoriasProductosViewModel: CategoriasProductosViewModel

    private lateinit var productosRecyclerView: RecyclerView

    private lateinit var productosAdapter: ProductosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentHomeProductsListBinding = FragmentHomeProductsListBinding.inflate(inflater, container, false)
        val productosFragmentView = fragmentHomeProductsListBinding.root

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(
            ProductosViewModel::class.java)
        categoriasProductosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CategoriasProductosViewModel::class.java)
        productosRecyclerView = fragmentHomeProductsListBinding.productsRecyclerViewList
        productosAdapter =
            ProductosAdapter(
                itemRecyclerViewResourceId = R.layout.product_cardview_item_single,
                glide = Glide.with(this),
                itemClickListener = this
            )
        productosRecyclerView.adapter = productosAdapter

        return productosFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productosViewModel.productos.observe(viewLifecycleOwner, Observer {
            val productos = it

            productosAdapter.update(productos)
        })
        productosViewModel.loadProductos()
    }

    override fun onStop() {
        super.onStop()

        Log.i("ProductosDestacadosFragment: ", "ProductosDestacadosFragment onStop()")
        _fragmentHomeProductsListBinding = null
        productosViewModel.productos.removeObservers(viewLifecycleOwner)
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
