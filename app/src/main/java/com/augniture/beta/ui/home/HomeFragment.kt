package com.augniture.beta.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.augniture.beta.R
import com.augniture.beta.databinding.BaseUpperTopAppBarBinding
import com.augniture.beta.databinding.HomeLowerTopAppBarBinding
import com.augniture.beta.databinding.HomeViewpagerBinding
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.UserProfileActivity
import com.augniture.beta.ui.home.featuredproducts.ProductosDestacadosFragment
import com.augniture.beta.ui.home.productcategories.CategoriasProductosFragment
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private var _homeViewPagerBinding: HomeViewpagerBinding? = null
    private val homeViewpagerBinding get() = _homeViewPagerBinding!!

    private var _baseUpperTopAppBarBinding: BaseUpperTopAppBarBinding? = null
    private val baseUpperTopAppBarBinding get() = _baseUpperTopAppBarBinding!!

    private var _homeLowerTopAppBar: HomeLowerTopAppBarBinding? = null
    private val homeLowerTopAppBar get() = _homeLowerTopAppBar!!

    private var homeFragmentStatePagerAdapter: HomeFragmentStatePagerAdapter? = null

    private var viewPager: ViewPager? = null

    private var tabLayout: TabLayout? = null

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // INFLATE VIEW
        _homeViewPagerBinding = HomeViewpagerBinding.inflate(inflater, container, false)
        val homeFragmentView = homeViewpagerBinding.root
        //val homeFragmentView = inflater.inflate(R.layout.home_viewpager, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        // SETUP TOP APP BAR
        // Setup upper container for the TopAppBar (Title TextView and UserImage)
        val upperSubContainer = (requireActivity() as MainActivity).mainActivityBinding.upperTopAppBarContainer
        _baseUpperTopAppBarBinding = BaseUpperTopAppBarBinding.inflate(inflater, upperSubContainer, false)
        val viewHomeUpperTopAppBar: View = baseUpperTopAppBarBinding.root

        upperSubContainer.removeAllViews()
        upperSubContainer.addView(viewHomeUpperTopAppBar)

        val topAppBarUserImage= baseUpperTopAppBarBinding.baseTopAppBarUserImg
        topAppBarUserImage.visibility = ImageView.VISIBLE
        val glideInstance = Glide.with(this)
        val photoUrl = sharedPreferences?.getString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_FOTO, "")

        glideInstance
            .load(photoUrl)
            .error(glideInstance.load(R.drawable.ic_user))
            .apply(RequestOptions.circleCropTransform())
            .into(topAppBarUserImage)

        topAppBarUserImage?.setOnClickListener{
            val profileActivityIntent = Intent(requireActivity(), UserProfileActivity::class.java)

            startActivity(profileActivityIntent)
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val topAppBarTitleTxt = baseUpperTopAppBarBinding.baseTopAppBarTitleTxt
        topAppBarTitleTxt.text = "Inicio"

        // Setup lower container for the TopAppBar (TabLayout)
        val lowerSubContainer = (requireActivity() as MainActivity).mainActivityBinding.lowerTopAppBarContainer
        _homeLowerTopAppBar = HomeLowerTopAppBarBinding.inflate(inflater, lowerSubContainer, false)
        val viewHomeLowerTopAppBar = homeLowerTopAppBar.root
        lowerSubContainer.removeAllViews()
        lowerSubContainer.addView(viewHomeLowerTopAppBar)

        // INTI ATTRIBUTES
        homeFragmentStatePagerAdapter = HomeFragmentStatePagerAdapter(fragmentManager = childFragmentManager)

        homeFragmentStatePagerAdapter?.addFragment(ProductosDestacadosFragment(), "Destacados")
        homeFragmentStatePagerAdapter?.addFragment(CategoriasProductosFragment(), "Categorias")

        viewPager = homeViewpagerBinding.homeViewPager
        viewPager?.adapter = homeFragmentStatePagerAdapter

        tabLayout = homeLowerTopAppBar.homeTabLayout
        tabLayout?.setupWithViewPager(viewPager)

        return homeFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStop() {
        super.onStop()

        Log.i("HomeFragment: ", "HomeFragment onStop()")
        _homeLowerTopAppBar = null
        _baseUpperTopAppBarBinding = null
        _homeViewPagerBinding = null

        homeFragmentStatePagerAdapter = null
        viewPager = null
        tabLayout = null

        sharedPreferences = null
    }
}
