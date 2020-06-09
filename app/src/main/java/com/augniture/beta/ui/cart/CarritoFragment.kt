package com.augniture.beta.ui.home.featuredproducts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.databinding.BaseUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentCartCartProductsListBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Producto
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.cart.CarritoAdapter
import com.augniture.beta.ui.general.detalleproducto.DetalleProductoFragment
import com.augniture.beta.ui.general.productos.CarritoViewModel
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.AddDeleteItemClickListener
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_cart_cart_products_list.*

class CarritoFragment() : Fragment(), ItemClickListener<Producto>, AddDeleteItemClickListener<Producto> {

    private var _fragmentCartCartProductsListBinding: FragmentCartCartProductsListBinding? = null
    private val fragmentCartCartProductsListBinding get() = _fragmentCartCartProductsListBinding!!

    private var _baseUpperTopAppBarBinding: BaseUpperTopAppBarBinding? = null
    private val baseUpperTopAppBarBinding get() = _baseUpperTopAppBarBinding!!

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var carritoViewModel: CarritoViewModel

    private lateinit var productosCompraRecyclerView: RecyclerView

    private lateinit var productosCompraAdapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentCartCartProductsListBinding = FragmentCartCartProductsListBinding.inflate(inflater, container, false)
        val productosFragmentView = fragmentCartCartProductsListBinding.root

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _baseUpperTopAppBarBinding = BaseUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewSearchUpperTopAppBar = baseUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewSearchUpperTopAppBar)

        val baseTopAppBarTitleTxt: TextView = baseUpperTopAppBarBinding.baseTopAppBarTitleTxt
        baseTopAppBarTitleTxt.text = "Carrito de compras"

        // Setup lower container for the TopAppBar
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        lowerSubContainer.removeAllViews()

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(ProductosViewModel::class.java)
        carritoViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CarritoViewModel::class.java)

        productosCompraRecyclerView = fragmentCartCartProductsListBinding.cartProductsRecyclerViewList
        productosCompraAdapter =
            CarritoAdapter(
                itemRecyclerViewResourceId = R.layout.cart_product_cardview_item_single,
                glide = Glide.with(this),
                itemClickListener = this,
                addDeleteItemClickListener = this
            )
        productosCompraRecyclerView.adapter = productosCompraAdapter

        return productosFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carritoViewModel.productosCompra.observe(viewLifecycleOwner, Observer {
            val productosCompra = it

            if (productosCompra.isNotEmpty()) {
                goToResumeButton.visibility = View.VISIBLE
            }
            else {
                goToResumeButton.visibility = View.GONE
            }

            productosCompraAdapter.update(productosCompra)
        })
        carritoViewModel.loadProductosCompra()


        goToResumeButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentMain, ResumeCarritoFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    override fun onStop() {
        super.onStop()

        Log.i("CarritoFragment: ", "CarritoFragment onStop()")

        _fragmentCartCartProductsListBinding = null
        _baseUpperTopAppBarBinding = null

        carritoViewModel.productosCompra.removeObservers(viewLifecycleOwner)
    }

    override fun onItemClicked(t: Producto) {
        //productosViewModel.setProductoSeleccionado(t)

        val detalleProductoFragment = DetalleProductoFragment(t)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentMain, detalleProductoFragment, DetalleProductoFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onAddButtonClicked(t: Producto) {
        carritoViewModel.addProductoCompra(t)
    }

    override fun onDeleteButtonClicked(t: Producto) {
        carritoViewModel.deleteProductoCompra(t)
    }

}
