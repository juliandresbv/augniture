package com.augniture.beta.ui.home.productcategories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.augniture.beta.R
import com.augniture.beta.databinding.FragmentHomeProductCategoriesBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.home.featuredproducts.CategoriasProductosAdapter
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.Glide
import org.json.JSONObject

class CategoriasProductosFragment : Fragment(),
    ItemClickListener<CategoriasProductosViewModel.Categoria> {

    private var _fragmentHomeProductCategoriesBinding: FragmentHomeProductCategoriesBinding? = null
    private val fragmentHomeProductCategoriesBinding get() = _fragmentHomeProductCategoriesBinding!!

    private lateinit var categoriasProductosViewModel: CategoriasProductosViewModel

    private lateinit var productCategoriesRecyclerView: RecyclerView

    private lateinit var categoriasProductosAdapter: CategoriasProductosAdapter

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
        _fragmentHomeProductCategoriesBinding = FragmentHomeProductCategoriesBinding.inflate(inflater, container, false)
        val categoriasProductosFragmentView = fragmentHomeProductCategoriesBinding.root


        // INIT ATTRIBUTES
        categoriasProductosViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(CategoriasProductosViewModel::class.java)
        categoriasProductosAdapter = CategoriasProductosAdapter(glide = Glide.with(this), itemClickListener = this)
        productCategoriesRecyclerView = fragmentHomeProductCategoriesBinding.productCategoriesRecyclerView
        productCategoriesRecyclerView.adapter = categoriasProductosAdapter

        return categoriasProductosFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriasProductosViewModel.categorias.observe(viewLifecycleOwner, Observer {
            val categorias = it

            categoriasProductosAdapter.update(categorias)
        })
        categoriasProductosViewModel.loadCategorias()
    }

    override fun onStop() {
        super.onStop()

        Log.i("CategoriasProductosFragment: ", "CategoriasProductosFragment onStop()")

        _fragmentHomeProductCategoriesBinding = null
        categoriasProductosViewModel.categorias.removeObservers(viewLifecycleOwner)
    }

    override fun onItemClicked(t: CategoriasProductosViewModel.Categoria) {
        //categoriasProductosViewModel.setCategoriaSeleccionada(t)

        val data = JSONObject(hashMapOf(
            "searchTerm" to t.nombre,
            "origin" to "Categoria"
        ))

        amplitudeInstance.logEvent("search", data)

        val productCategoriesFragment = ProductosCategoriaFragment(t)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentMain, productCategoriesFragment, ProductosCategoriaFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
