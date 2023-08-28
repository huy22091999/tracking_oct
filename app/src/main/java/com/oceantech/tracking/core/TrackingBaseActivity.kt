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

abstract class TrackingBaseActivity<VB : ViewBinding> : AppCompatActivity(), HasScreenInjector {
    //khai báo
    //đối tượng viewBinding (class con chỉ việc dùng luôn kk)
    protected lateinit var views: VB

    //----------------------------------------------------------------------------------------------

    //đối tượng Factory của viewModel (factory để tạo thg viewModel tương ứng)
    private lateinit var viewModelFactory: ViewModelProvider.Factory
    //khởi tạo đối tượng provider của viewModel để lấy giá trị tùy chỉnh this ở đây là activity
    protected val viewModelProvider
        get() = ViewModelProvider(this, viewModelFactory)

    //----------------------------------------------------------------------------------------------
    //đối tượng Factory của Fragment (factory để tạo thg fragment tương ứng)
    private lateinit var fragmentFactory: FragmentFactory

    // Filter for multiple invalid token error
    //biến theo dõi xem Activity chính đã được bắt đầu hay chưa
    private var mainActivityStarted = false
    //Biến này sẽ chứa trạng thái của activity khi nó được hủy và khôi phục lại
    private var savedInstanceState: Bundle? = null

    //----------------------------------------------------------------------------------------------
    //đối tượng di
    private lateinit var nimpeComponent: TrackingComponent

    //----------------------------------------------------------------------------------------------
    //hàm onCreate()
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate Activity ${javaClass.simpleName}")
        //khởi tạo thg di
        nimpeComponent = DaggerTrackingComponent.factory().create(this)
        //? cái này chưa bt dùng làm gì nè
        val timeForInjection = measureTimeMillis {
            injectWith(nimpeComponent)
        }
        Timber.v("Injecting dependencies into ${javaClass.simpleName} took $timeForInjection ms")
        //lấy ra thằng factory cho fragment và viewModel
        fragmentFactory = nimpeComponent.fragmentFactory()
        viewModelFactory = nimpeComponent.viewModelFactory()
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)

        //cái hàm này làm trước khi set contentView
        doBeforeSetContentView()

        // Hack for font size
        applyFontSize()
        // config viewbinding
        views = getBinding()
        setContentView(views.root)
        //gán giá trị cho thằng state
        this.savedInstanceState = savedInstanceState
        // cái này còn ko dùng
        initUiAndData()
        //chưa biết sao bằng -1 nữa
        val titleRes = getTitleRes()
        if (titleRes != -1) {
            supportActionBar?.let {
                it.setTitle(titleRes)
            } ?: run {
                setTitle(titleRes)
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    //hàm mở rộng cho viewModel, thêm chức năng theo dõi sự kiện để dùng cho Activity
    //T phải là NimpeViewEvents,
    protected fun <T : NimpeViewEvents> TrackingViewModel<*, *, T>.observeViewEvents(observer: (T?) -> Unit) {
        viewEvents
            .observe()//phát ra sự kiện đó
            .observeOn(AndroidSchedulers.mainThread())//nhận tại luồng giao diện
            .subscribe {//thực hiện
                hideWaitingView()//ẩn progessbar
                observer(it)//trả lại giá trị T
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
    /**
     * Lên lịch thực hiện một hành động trong lần gọi hàm onPostResume() tiếp theo.
     * Điều này giúp khắc phục lỗi quan sát trên Android 6 (API 23).
     */
    protected fun doOnPostResume(action: () -> Unit) {
        synchronized(postResumeScheduledActions) {
            postResumeScheduledActions.add(action)
        }
    }

    /**
     * Được gọi khi activity không còn hiển thị cho người dùng.
     */
    override fun onPause() {
        super.onPause()
        Timber.i("onPause Activity ${javaClass.simpleName}")

    }
    /**
     * Được gọi khi trạng thái của cửa sổ của activity thay đổi.
     * Nếu activity đang ở chế độ toàn màn hình và nhận được focus, nó sẽ được đặt lại ở chế độ toàn màn hình.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && displayInFullscreen()) {
            setFullScreen()
        }
    }
    /**
     * Được gọi khi chế độ multi-window của activity thay đổi.
     * Phương thức này ghi log thay đổi trong chế độ multi-window nếu cần.
     */
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration?) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)

        Timber.w("onMultiWindowModeChanged. isInMultiWindowMode: $isInMultiWindowMode")
//        bugReporter.inMultiWindowMode = isInMultiWindowMode
    }

    override fun injector(): TrackingComponent {
        return nimpeComponent
    }
    /**
     * Phương thức này có thể được ghi đè trong các lớp con để thực hiện injection sử dụng injector được cung cấp.
     * Phương thức này được gọi trong quá trình tạo activity.
     */
    protected open fun injectWith(injector: TrackingComponent) = Unit
    /**
     * Tạo fragment từ lớp fragmentClass cung cấp với các đối số args.
     */
    protected fun createFragment(fragmentClass: Class<out Fragment>, args: Bundle?): Fragment {
        return fragmentFactory.instantiate(classLoader, fragmentClass.name).apply {
            arguments = args
        }
    }


    /**
     * Force to render the activity in fullscreen
     */
    /**
     * Buộc activity hiển thị trong chế độ toàn màn hình.
     * Phương thức này sẽ đặt các cờ phù hợp để đạt được chế độ toàn màn hình.
     * Lưu ý: Một số giá trị cờ có thể đã bị loại bỏ đối với các phiên bản API mới hơn.
     * Nó kiểm tra phiên bản API hiện tại và đặt các cờ tùy thích.
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

    /**
     * Khởi tạo menu dựa trên menuRes được chỉ định.
     * Nó cũng áp dụng tô màu tùy chỉnh cho các biểu tượng menu nếu getMenuTint() được triển khai và ThemeUtils có sẵn.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
//            ThemeUtils.tintMenuIcons(menu, ThemeUtils.getColor(this, getMenuTint()))
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }
    /**
     * Xử lý sự kiện khi người dùng click vào một item trong options menu.
     * Nếu nút home (nút quay lại) được click, gọi onBackPressed(true) để xử lý sự kiện.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed(true)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút back.
     * Nếu fromToolbar là true, gọi onBackPressed(true) để xử lý sự kiện.
     * Nếu fromToolbar là false, gọi super.onBackPressed() để xử lý sự kiện.
     */
    override fun onBackPressed() {
        onBackPressed(false)
    }
    /**
     * Xử lý sự kiện khi người dùng nhấn nút back với một cờ chỉ định liệu có từ toolbar hay không.
     * Nếu fromToolbar là true, gọi đệ quy onBackPressed để xử lý sự kiện cho các fragment con.
     * Nếu fromToolbar là false, gọi super.onBackPressed() để xử lý sự kiện.
     */
    private fun onBackPressed(fromToolbar: Boolean) {
        val handled = recursivelyDispatchOnBackPressed(supportFragmentManager, fromToolbar)
        if (!handled) {
            super.onBackPressed()
        }
    }
    /**
     * Đệ quy xử lý sự kiện khi người dùng nhấn nút back cho các fragment con.
     */
    private fun recursivelyDispatchOnBackPressed(
        fm: FragmentManager,
        fromToolbar: Boolean
    ): Boolean {
        val reverseOrder = fm.fragments.filterIsInstance<TrackingBaseFragment<*>>().reversed()
        for (f in reverseOrder) {
            val handledByChildFragments =
                recursivelyDispatchOnBackPressed(f.childFragmentManager, fromToolbar)
            if (handledByChildFragments) {
                return true
            }
        }
        return false
    }

    /* ==========================================================================================
     * PROTECTED METHODS
     * ========================================================================================== */
    /* ==========================================================================================
     * CÁC PHƯƠNG THỨC BẢO VỆ
     * ========================================================================================== */

    /**
     * Lấy trạng thái đã lưu.
     * Đảm bảo `isFirstCreation()` trả về false trước khi gọi phương thức này.
     *
     * @return savedInstanceState
     */
    protected fun getSavedInstanceState(): Bundle {
        return savedInstanceState!!
    }

    /**
     * Kiểm tra xem activity có được tạo lần đầu hay không (không phục hồi bởi hệ thống).
     *
     * @return true nếu Activity được tạo lần đầu
     */
    protected fun isFirstCreation() = savedInstanceState == null

    /**
     * Cấu hình Toolbar với nút quay lại mặc định.
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
