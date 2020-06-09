package com.augniture.beta.ui.home.featuredproducts

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.augniture.beta.R
import com.augniture.beta.databinding.BackUpperTopAppBarBinding
import com.augniture.beta.databinding.FragmentPaymentMethodBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.SuccessActivity
import com.augniture.beta.ui.general.productos.CarritoViewModel
import com.augniture.beta.ui.general.productos.ProductosViewModel
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_payment_method.*

class MetodoPagoCarritoFragment() : Fragment() {

    private var _fragmentPaymentMethodBinding: FragmentPaymentMethodBinding? = null
    private val fragmentPaymentMethodBinding get() = _fragmentPaymentMethodBinding!!

    private var _backUpperTopAppBarBinding: BackUpperTopAppBarBinding? = null
    private val backUpperTopAppBarBinding get() = _backUpperTopAppBarBinding!!

    private lateinit var productosViewModel: ProductosViewModel

    private lateinit var carritoViewModel: CarritoViewModel

    private lateinit var sharedPreferences: SharedPreferences

    private var isOnline: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _fragmentPaymentMethodBinding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        val metodoPagoCarritoFragmentView = fragmentPaymentMethodBinding.root

        // Setup SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _backUpperTopAppBarBinding = BackUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewSearchUpperTopAppBar= backUpperTopAppBarBinding.root
        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewSearchUpperTopAppBar)

        val backTopAppBarTitleTxt= backUpperTopAppBarBinding.backTopAppBarTitleTxt
        backTopAppBarTitleTxt.text = "Metodo de pago"

        // Setup lower container for the TopAppBar
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        lowerSubContainer.removeAllViews()

        // INIT ATTRIBUTES
        productosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(ProductosViewModel::class.java)
        carritoViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CarritoViewModel::class.java)

        return metodoPagoCarritoFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        completePaymentButton.setOnClickListener {
            isOnline = NetworkManager.isOnline((requireActivity() as MainActivity))

            if (isOnline) {
                var usuarioActual = Usuario.USUARIO_VACIO
                usuarioActual.id = sharedPreferences.getString(
                    SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID,
                    SharedPreferencesConstants.VALOR_DEF_STRING
                )

                carritoViewModel.addProductosOrden(usuarioActual)

                // Go to success activity
                val successActivity = Intent(requireActivity(), SuccessActivity::class.java)
                successActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                successActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                successActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                successActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(successActivity)

                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            else {
                // Go to error handling activity
                val mainActivityView = (requireActivity() as MainActivity).mainActivityBinding.root

                val snackbar = Snackbar.make(
                    mainActivityView,
                    "Oops!, no es posible completar la transaccion. \n" +
                            "No te preocupes, guardaremos el estado de tu carrito.",
                    3500)
                snackbar.setActionTextColor(Color.DKGRAY)
                snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.snackbarWarning, null))
                snackbar.setTextColor(Color.DKGRAY)
                snackbar.setAction("Cerrar") {
                    snackbar.dismiss()
                }
                snackbar.show()

                Handler().postDelayed({
                    val carritoFragment = (requireActivity() as MainActivity).supportFragmentManager.findFragmentByTag(CarritoFragment::class.simpleName) ?: CarritoFragment()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.contentMain, carritoFragment, CarritoFragment::class.simpleName)
                    transaction.commit()
                }, 4000)
            }

        }

    }

    override fun onStop() {
        super.onStop()

        Log.i("MetodoPagoCarritoFragment: ", "MetodoPagoCarritoFragment onStop()")

        _fragmentPaymentMethodBinding = null
        _backUpperTopAppBarBinding = null
    }

}
