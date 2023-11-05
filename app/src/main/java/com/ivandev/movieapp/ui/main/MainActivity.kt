package com.ivandev.movieapp.ui.main


import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ivandev.movieapp.R
import com.ivandev.movieapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var splashScreen: SplashScreen
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private val movieViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSplashScreen()
        inflateView()
        setContentView(binding.root)
        initUI()
    }

    private fun inflateView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    private fun initUI() {
        flagNotTouchable()
        setBinding()
        setNavController()
        seUpNavController()
        getDataRepository()
        treeObserver()
    }

    private fun flagNotTouchable() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun treeObserver() {
        viewObserver(binding.root)
    }

    private fun setBinding() {
        bottomNavigationView = binding.bnv
    }

    private fun setNavController() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun seUpNavController() {
        bottomNavigationView.setupWithNavController(navController)

    }

    private fun getDataRepository() {
        movieViewModel.onCreate()
    }

    private fun addSplashScreen() {
        splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }
    }

    /*
    * Agrega un observador a una vista para comprobar
    * el estado de la información de la api antes de pintar la misma.
    */
    private fun viewObserver(view: View) {
        view.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val repositoryResponseReady = movieViewModel.repositoryResponseReady.value
                    return if (repositoryResponseReady == true) {
                        view.viewTreeObserver?.removeOnPreDrawListener(this)
                        removeSplashScreenCondition()
                        removeFlagNotTouchable()
                        true
                    } else false
                }
            }
        )
    }

    private fun removeFlagNotTouchable() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun removeSplashScreenCondition() {
        splashScreen.setKeepOnScreenCondition { false }
    }
}