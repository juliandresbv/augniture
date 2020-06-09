package com.augniture.beta.dependencyinjection

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object AugnitureViewModelFactory : ViewModelProvider.Factory {

    lateinit var application: Application

    lateinit var dependencies: Interactors

    fun inject(application: Application, dependencies: Interactors) {
        AugnitureViewModelFactory.application = application
        AugnitureViewModelFactory.dependencies = dependencies
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (AugnitureViewModel::class.java.isAssignableFrom(modelClass)) {
            return modelClass
                .getConstructor(Application::class.java, Interactors::class.java)
                .newInstance(
                    application,
                    dependencies
                )
        } else {
            throw IllegalStateException("ViewModel must extend from AugnitureViewModel")
        }
    }
}