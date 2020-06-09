
package com.augniture.beta.ui.home.productcategories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.databinding.BackUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentCategoryProductsListBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Producto
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.general.detalleproducto.DetalleProductoFragment
import com.augniture.beta.ui.general.productos.ProductosAdapter
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.Glide

class ProductosCategoriaFragment(
    private val categoria: CategoriasProductosViewModel.Categoria? = null
): Fragment(), ItemClickListener<Producto> {

    private var _fragmentCategoryProductsListBinding: FragmentCategoryProductsListBinding? = null
    private val fragmentCategoryProductsListBinding get() = _fragmentCategoryProductsListBinding!!

    private var _backUpperTopAppBarBinding: BackUpperTopAppBarBinding? = null
    private val backUpperTopAppBarBinding get() = _backUpperTopAppBarBinding!!

    private lateinit var categoriasProductosViewModel: CategoriasProductosViewModel

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var productosRecyclerView: RecyclerView

    private lateinit var productosAdapter: ProductosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentCategoryProductsListBinding = FragmentCategoryProductsListBinding.inflate(inflater, container, false)
        val productosCategoriaFragmentView = fragmentCategoryProductsListBinding.root

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and BackImageView)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _backUpperTopAppBarBinding = BackUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewHomeCategoryUpperTopAppBar = backUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewHomeCategoryUpperTopAppBar)

        // Setup lower container for the TopAppBar
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        lowerSubContainer.removeAllViews()

        // Setup back button onClick Listener
        val backBtnImageView: ImageView? = backUpperTopAppBarBinding.backTopAppBarBackButtonImg
        backBtnImageView?.setOnClickListener {

        }

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(
            ProductosViewModel::class.java)
        categoriasProductosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CategoriasProductosViewModel::class.java)
        productosAdapter =
            ProductosAdapter(
                itemRecyclerViewResourceId = R.layout.product_cardview_item_single,
                glide = Glide.with(this),
                itemClickListener = this
            )
        productosRecyclerView = fragmentCategoryProductsListBinding.categoryProductsRecyclerViewList
        productosRecyclerView.adapter = productosAdapter

        return productosCategoriaFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        categoriasProductosViewModel.categoriaSeleccionada.observe(viewLifecycleOwner, Observer {
            val categoria = it

            val fragmentTitle: TextView? = backUpperTopAppBarBinding.backTopAppBarTitleTxt
            fragmentTitle?.text = "Categoria - ${categoria.nombre}"

            productosViewModel.loadProductosCategoria(categoria)
        })
        */

        val fragmentTitle: TextView? = backUpperTopAppBarBinding.backTopAppBarTitleTxt
        fragmentTitle?.text = "Categoria - ${categoria?.nombre}"

        productosViewModel.loadProductosCategoria(categoria!!)

        productosViewModel.productosCategoria.observe(viewLifecycleOwner, Observer {
            val productosCategoria = it

            productosAdapter.update(productosCategoria)
        })

    }

    override fun onStop() {
        super.onStop()

        Log.i("ProductosCategoriasFragment: ", "ProductosCategoriasFragment onStop()")
        _fragmentCategoryProductsListBinding = null
        _backUpperTopAppBarBinding = null

        productosViewModel.productosCategoria.removeObservers(viewLifecycleOwner)
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
