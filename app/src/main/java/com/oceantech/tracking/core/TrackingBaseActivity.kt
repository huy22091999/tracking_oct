/*
 * Copyright 2019 New nimpe Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oceantech.tracking.core

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.di.DaggerTrackingComponent
import com.oceantech.tracking.di.HasScreenInjector
import com.oceantech.tracking.di.TrackingComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import kotlin.system.measureTimeMillis

abstract class TrackingBaseActivity<VB: ViewBinding> : AppCompatActivity(), HasScreenInjector {

    protected lateinit var views: VB

    private lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModelProvider
        get() = ViewModelProvider(this, viewModelFactory)

    protected fun <T : NimpeViewEvents> TrackingViewModel<*, *, T>.observeViewEvents(observer: (T?) -> Unit) {
        viewEvents
                .observe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    hideWaitingView()
                    observer(it)
                }
    }

    private lateinit var fragmentFactory: FragmentFactory


    // Filter for multiple invalid token error
    private var mainActivityStarted = false

    private var savedInstanceState: Bundle? = null

    private lateinit var nimpeComponent: TrackingComponent
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate Activity ${javaClass.simpleName}")

        nimpeComponent= DaggerTrackingComponent.factory().create(this)
        val timeForInjection = measureTimeMillis {
            injectWith(nimpeComponent)
        }
        Timber.v("Injecting dependencies into ${javaClass.simpleName} took $timeForInjection ms")
        fragmentFactory = nimpeComponent.fragmentFactory()
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)

        doBeforeSetContentView()

        // Hack for font size
        applyFontSize()

        views = getBinding()
        setContentView(views.root)

        this.savedInstanceState = savedInstanceState

        initUiAndData()

        val titleRes = getTitleRes()
        if (titleRes != -1) {
            supportActionBar?.let {
                it.setTitle(titleRes)
            } ?: run {
                setTitle(titleRes)
            }
        }
    }

    /**
     * This method has to be called for the font size setting be supported correctly.
     */
    private fun applyFontSize() {
        @Suppress("DEPRECATION")
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
    }


    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Activity ${javaClass.simpleName}")

    }


    override fun onResume() {
        super.onResume()
        Timber.i("onResume Activity ${javaClass.simpleName}")

    }

    private val postResumeScheduledActions = mutableListOf<() -> Unit>()

    /**
     * Schedule action to be done in the next call of onPostResume()
     * It fixes bug observed on Android 6 (API 23)
     */
    protected fun doOnPostResume(action: () -> Unit) {
        synchronized(postResumeScheduledActions) {
            postResumeScheduledActions.add(action)
        }
    }


    override fun onPause() {
        super.onPause()
        Timber.i("onPause Activity ${javaClass.simpleName}")

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && displayInFullscreen()) {
            setFullScreen()
        }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration?) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)

        Timber.w("onMultiWindowModeChanged. isInMultiWindowMode: $isInMultiWindowMode")
//        bugReporter.inMultiWindowMode = isInMultiWindowMode
    }

    override fun injector(): TrackingComponent {
        return nimpeComponent
    }

    protected open fun injectWith(injector: TrackingComponent) = Unit

    protected fun createFragment(fragmentClass: Class<out Fragment>, args: Bundle?): Fragment {
        return fragmentFactory.instantiate(classLoader, fragmentClass.name).apply {
            arguments = args
        }
    }


    /**
     * Force to render the activity in fullscreen
     */
    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // New API instead of SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    /* ==========================================================================================
     * MENU MANAGEMENT
     * ========================================================================================== */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
//            ThemeUtils.tintMenuIcons(menu, ThemeUtils.getColor(this, getMenuTint()))
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed(true)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        onBackPressed(false)
    }

    private fun onBackPressed(fromToolbar: Boolean) {
        val handled = recursivelyDispatchOnBackPressed(supportFragmentManager, fromToolbar)
        if (!handled) {
            super.onBackPressed()
        }
    }

    private fun recursivelyDispatchOnBackPressed(fm: FragmentManager, fromToolbar: Boolean): Boolean {
        val reverseOrder = fm.fragments.filterIsInstance<TrackingBaseFragment<*>>().reversed()
        for (f in reverseOrder) {
            val handledByChildFragments = recursivelyDispatchOnBackPressed(f.childFragmentManager, fromToolbar)
            if (handledByChildFragments) {
                return true
            }
        }
        return false
    }

    /* ==========================================================================================
     * PROTECTED METHODS
     * ========================================================================================== */

    /**
     * Get the saved instance state.
     * Ensure {@link isFirstCreation()} returns false before calling this
     *
     * @return
     */
    protected fun getSavedInstanceState(): Bundle {
        return savedInstanceState!!
    }

    /**
     * Is first creation
     *
     * @return true if Activity is created for the first time (and not restored by the system)
     */
    protected fun isFirstCreation() = savedInstanceState == null

    /**
     * Configure the Toolbar, with default back button.
     */
    protected fun configureToolbar(toolbar: Toolbar, displayBack: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(displayBack)
            it.setDisplayHomeAsUpEnabled(displayBack)
            it.title = null
        }
    }

    // ==============================================================================================
    // Handle loading view (also called waiting view or spinner view)
    // ==============================================================================================

    var waitingView: View? = null
        set(value) {
            field = value

            // Ensure this view is clickable to catch UI events
            value?.isClickable = true
        }

    /**
     * Tells if the waiting view is currently displayed
     *
     * @return true if the waiting view is displayed
     */
    fun isWaitingViewVisible() = waitingView?.isVisible == true

    /**
     * Show the waiting view, and set text if not null.
     */
    open fun showWaitingView(text: String? = null) {
//        waitingView?.isVisible = true
//        if (text != null) {
//            waitingView?.findViewById<TextView>(R.id.waitingStatusText)?.setTextOrHide(text)
//        }
    }

    /**
     * Hide the waiting view
     */
    open fun hideWaitingView() {
        waitingView?.isVisible = false
    }

    /* ==========================================================================================
     * OPEN METHODS
     * ========================================================================================== */

    abstract fun getBinding(): VB

    open fun displayInFullscreen() = false

    open fun doBeforeSetContentView() = Unit

    open fun initUiAndData() = Unit

    @StringRes
    open fun getTitleRes() = -1

    @MenuRes
    open fun getMenuRes() = -1


}
