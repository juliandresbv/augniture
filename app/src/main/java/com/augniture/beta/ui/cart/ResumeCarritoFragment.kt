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
import com.augniture.beta.databinding.BackUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentResumeCartProductsListBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Producto
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.cart.ResumeCarritoAdapter
import com.augniture.beta.ui.general.detalleproducto.DetalleProductoFragment
import com.augniture.beta.ui.general.productos.CarritoViewModel
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_resume_cart_products_list.*

class ResumeCarritoFragment() : Fragment(), ItemClickListener<Producto> {

    private var _fragmentResumeCartProductsListBinding: FragmentResumeCartProductsListBinding? = null
    private val fragmentResumeCartProductsListBinding get() = _fragmentResumeCartProductsListBinding!!

    private var _backUpperTopAppBarBinding: BackUpperTopAppBarBinding? = null
    private val backUpperTopAppBarBinding get() = _backUpperTopAppBarBinding!!

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var carritoViewModel: CarritoViewModel

    private lateinit var resumenProductosCompraRecyclerView: RecyclerView

    private lateinit var resumenProductosCompraAdapter: ResumeCarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentResumeCartProductsListBinding = FragmentResumeCartProductsListBinding.inflate(inflater, container, false)
        val resumenCarritoFragmentView = fragmentResumeCartProductsListBinding.root

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer= (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _backUpperTopAppBarBinding = BackUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewSearchUpperTopAppBar= backUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewSearchUpperTopAppBar)

        val backTopAppBarTitleTxt: TextView = backUpperTopAppBarBinding.backTopAppBarTitleTxt
        backTopAppBarTitleTxt.text = "Resumen de compra"

        // Setup lower container for the TopAppBar
        val lowerSubContainer= (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        lowerSubContainer.removeAllViews()

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(ProductosViewModel::class.java)
        carritoViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CarritoViewModel::class.java)

        resumenProductosCompraRecyclerView = fragmentResumeCartProductsListBinding.resumeCartProductsRecyclerViewList
        resumenProductosCompraAdapter =
            ResumeCarritoAdapter(
                itemRecyclerViewResourceId = R.layout.resume_cart_product_cardview_item_single,
                glide = Glide.with(this),
                itemClickListener = this
            )
        resumenProductosCompraRecyclerView.adapter = resumenProductosCompraAdapter

        return resumenCarritoFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carritoViewModel.productosCompra.observe(viewLifecycleOwner, Observer {
            val productosCompra = it

            if (productosCompra.isNotEmpty()) {
                fragmentResumeCartProductsListBinding.goToPaymentButton.visibility = View.VISIBLE

                var totalResumenCarrito: Double = 0.0

                for (productoCompra in productosCompra) {
                    totalResumenCarrito += (productoCompra.cantidad!! * productoCompra.producto?.precio!!)
                }

                fragmentResumeCartProductsListBinding.resumeCartTotalCV.text = totalResumenCarrito.toString()
            }
            else {
                fragmentResumeCartProductsListBinding.goToPaymentButton.visibility = View.GONE
            }

            resumenProductosCompraAdapter.update(productosCompra)
        })
        carritoViewModel.loadProductosCompra()

        goToPaymentButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentMain, MetodoPagoCarritoFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    override fun onStop() {
        super.onStop()

        Log.i("ResumeCarritoFragment: ", "ResumeCarritoFragment onStop()")

        _fragmentResumeCartProductsListBinding = null
        _backUpperTopAppBarBinding = null

        carritoViewModel.productosCompra.removeObservers(viewLifecycleOwner)
    }

    override fun onItemClicked(t: Producto) {
        productosViewModel.setProductoSeleccionado(t)

        val detalleProductoFragment = DetalleProductoFragment(t)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentMain, detalleProductoFragment, DetalleProductoFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
