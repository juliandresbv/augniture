package com.augniture.beta.dependencyinjection

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class AugnitureViewModel(application: Application, protected val interactors: Interactors) : AndroidViewModel(application) {

    protected val application: AugnitureApplication = getApplication()

}