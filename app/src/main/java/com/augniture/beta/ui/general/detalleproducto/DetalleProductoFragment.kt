package com.augniture.beta.ui.general.detalleproducto

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.augniture.beta.R
import com.augniture.beta.databinding.BackProductUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentProductDetailBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.favorites.FavoritosViewModel
import com.augniture.beta.ui.general.productos.CarritoViewModel
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_product_detail.*
import org.json.JSONObject

class DetalleProductoFragment(
    private val producto: Producto? = null
) : Fragment() {

    private var _fragmentProductDetailBinding: FragmentProductDetailBinding? = null
    private val fragmentProductDetailBinding get() = _fragmentProductDetailBinding!!

    private var _backProductUpperTopAppBarBinding: BackProductUpperTopAppBarBinding? = null
    private val backProductUpperTopAppBarBinding get() = _backProductUpperTopAppBarBinding!!

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var favoritosViewModel: FavoritosViewModel

    private lateinit var carritoViewModel: CarritoViewModel

    private lateinit var glideInstance: RequestManager

    private var esFavorito: Boolean = false

    private var favorito: Favorito? = null

    private var isOnline: Boolean = false

    private var amplitudeInstance: AmplitudeClient? = null

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

        _fragmentProductDetailBinding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val detalleProductoView = fragmentProductDetailBinding.root

        // Init SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        // Init Glide
        glideInstance = Glide.with(this)

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _backProductUpperTopAppBarBinding = BackProductUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewProductDetailUpperTopAppBar = backProductUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewProductDetailUpperTopAppBar)

        // Setup lower container for the TopAppBar (TabLayout)
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        lowerSubContainer.removeAllViews()

        // Setup back button onClick Listener
        val backBtnImageView= backProductUpperTopAppBarBinding.productDetailTopAppBarBackButtonImg
        backBtnImageView.setOnClickListener {
            //fragmentManager?.popBackStackImmediate()
        }

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(ProductosViewModel::class.java)
        favoritosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(FavoritosViewModel::class.java)
        carritoViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CarritoViewModel::class.java)

        return detalleProductoView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = JSONObject(hashMapOf(
            "itemID" to producto?.id,
            "itemName" to producto?.nombre,
            "itemCategory" to producto?.categoria,
            "origin" to "No_registrado"
        ))

        amplitudeInstance?.logEvent("view_item", data)

        // Load title Producto
        val productDetailTopAppBarTitleTxt = backProductUpperTopAppBarBinding.productDetailTopAppBarTitleTxt
        productDetailTopAppBarTitleTxt.text = producto?.nombre

        // Load favorite state (is the product a favorite product or not?)
        productosViewModel.productosFavoritos.observe(viewLifecycleOwner, Observer { it ->
            val listaProductosFavoritos = it

            favorito = listaProductosFavoritos.find {
                it.referenciaProducto?.id == producto?.id
            }

            esFavorito = favorito != null

            val productDetailTopAppBarLikeButtonImg: ImageButton = backProductUpperTopAppBarBinding.productDetailTopAppBarLikeButtonImg

            updateLikeImageButton(esFavorito, glideInstance, productDetailTopAppBarLikeButtonImg)
        })

        // Load image Producto
        val productDetailPreviewImage = fragmentProductDetailBinding.productDetailPreviewImage

        glideInstance
            .load(producto?.imagen)
            .error(glideInstance.load(R.drawable.preview_missing))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(productDetailPreviewImage)

        // Load descripcion Producto
        val productDetailSectionContentTxt = fragmentProductDetailBinding.productDetailSectionContentTxt
        productDetailSectionContentTxt.text = producto?.descripcion

        val productDetailTopAppBarLikeButtonImg = backProductUpperTopAppBarBinding.productDetailTopAppBarLikeButtonImg

        productDetailTopAppBarLikeButtonImg.setOnClickListener{
            //Init isOnline from NetworkManager
            isOnline = NetworkManager.isOnline(requireActivity())

            var usuarioActual = Usuario.USUARIO_VACIO
            usuarioActual.id = sharedPreferences.getString(
                SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID,
                SharedPreferencesConstants.VALOR_DEF_STRING
            )

            val productDetailTopAppBarLikeButtonImg: ImageButton = backProductUpperTopAppBarBinding.productDetailTopAppBarLikeButtonImg

            if (isOnline) {
                if (usuarioActual.id != "") {
                    if (esFavorito) {
                        Log.i("Favorito ", "Para eliminar de favoritos")
                        favoritosViewModel.deleteFavorito(usuarioActual, favorito!!)
                        updateLikeImageButton(false, glideInstance, productDetailTopAppBarLikeButtonImg)
                    }
                    else {
                        Log.i("Favorito ", "Para agregar a favoritos")
                        favoritosViewModel.addFavorito(usuarioActual, producto!!)
                        updateLikeImageButton(true, glideInstance, productDetailTopAppBarLikeButtonImg)

                        val dataWish = JSONObject(hashMapOf(
                            "itemID" to producto?.id,
                            "itemName" to producto?.nombre,
                            "itemCategory" to producto?.categoria
                        ))

                        amplitudeInstance?.logEvent("add_to_wish_list", dataWish)
                    }

                    productosViewModel.loadProductosFavoritos(usuarioActual)
                }
            }
            else {
                // TODO("Network offline")
                var offlineSnackbarText = ""

                offlineSnackbarText =
                    if(esFavorito) { "No puedes desmarcar este producto de tus favoritos." }
                    else { "No puedes marcar este producto como favorito." }

                val mainActivityView = (requireActivity() as MainActivity).mainActivityBinding.root

                val snackbar = Snackbar.make(
                    mainActivityView,
                    "No hay conexión a Internet. \n" +
                            offlineSnackbarText,
                    2500)
                snackbar.setActionTextColor(Color.DKGRAY)
                snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.snackbarWarning, null))
                snackbar.setTextColor(Color.DKGRAY)
                snackbar.setAction("Cerrar") {
                    snackbar.dismiss()
                }
                snackbar.show()
            }

        }

        // Setup Add to Cart
        productDetailAddToCartButton.setOnClickListener {

            val dataCart = JSONObject(hashMapOf(
                "itemID" to producto?.id,
                "itemName" to producto?.nombre,
                "itemCategory" to producto?.categoria
            ))

            amplitudeInstance?.logEvent("add_to_cart", dataCart)

            carritoViewModel.addProductoCompra(producto!!)
            //Toast.makeText(requireActivity(), "Producto agregado!", Toast.LENGTH_SHORT).show()

            val mainActivityView = (requireActivity() as MainActivity).mainActivityBinding.root

            val snackbar = Snackbar.make(
                mainActivityView,
                "Accion completada. \n" +
                "Producto agregado al carrito.",
                2500)
            snackbar.setActionTextColor(Color.DKGRAY)
            snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.snackbarSuccess, null))
            snackbar.setTextColor(Color.DKGRAY)
            snackbar.setAction("Cerrar") {
                snackbar.dismiss()
            }
            snackbar.show()
        }

    }

    override fun onStop() {
        super.onStop()
        Log.i("DetalleProductoFragment: ", "DetalleProductoFragment onStop()")

        _fragmentProductDetailBinding = null
        _backProductUpperTopAppBarBinding = null

        productosViewModel.productosFavoritos.removeObservers(viewLifecycleOwner)
    }

    private fun updateLikeImageButton(esFavorito: Boolean, glideInstance: RequestManager, productDetailTopAppBarLikeButtonImg: ImageButton) {
        if (esFavorito) {
            glideInstance
                .load(R.drawable.ic_like_filled)
                .error(glideInstance.load(R.drawable.ic_like_border))
                .override(productDetailTopAppBarLikeButtonImg.width, productDetailTopAppBarLikeButtonImg.height)
                .into(productDetailTopAppBarLikeButtonImg)
        }
        else {
            glideInstance
                .load(R.drawable.ic_like_border)
                .error(glideInstance.load(R.drawable.ic_like_border))
                .override(productDetailTopAppBarLikeButtonImg.width, productDetailTopAppBarLikeButtonImg.height)
                .into(productDetailTopAppBarLikeButtonImg)
        }
    }

}