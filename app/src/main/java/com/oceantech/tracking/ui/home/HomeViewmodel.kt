package com.oceantech.tracking.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.*
import com.oceantech.tracking.data.repository.CategoryRepository
import com.oceantech.tracking.data.repository.HealthOrganizationRepository
import com.oceantech.tracking.data.repository.ReDengueRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class HomeViewmodel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    val repository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val healthOrganizationRepository: HealthOrganizationRepository,
    private val dengueRepository: ReDengueRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    private var category: Category = Category()
    private var new = News()
    var language: Int = 1
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCategorys -> handleGetCategory()
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.SaveFeedback -> handSaveFeedback(action.feedback)
            is HomeViewAction.ResetLang -> handResetLang()
        }
    }

    private fun handResetLang() {
        _viewEvents.post(HomeViewEvent.ResetLanguege)
    }

    private fun handSaveFeedback(feedback: Feedback) {
        setState {
            copy(asyncSaveFeedback = Loading())
        }
        dengueRepository.saveFeedback(feedback).execute {
            _viewEvents.post(HomeViewEvent.SaveFeedback)
            copy(asyncSaveFeedback = it)
        }
    }

    fun setCategory(category: Category) {
        this.category = category
    }
    fun getCategory() = this.category
    fun setNew(new: News) {
        this.new = new
    }
    fun getNew() = this.new

    private fun handleGetCategory() {
        setState {
            copy(asyncCategory = Loading())
        }
        categoryRepository.getCategory(language).execute {
            copy(asyncCategory = it)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        repository.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }

    fun getHealthOrgs(language: Int): Flow<PagingData<HealthOrganization>> {
        val newPageData =
            healthOrganizationRepository.getHealthOrg(language).cachedIn(viewModelScope)
        return newPageData
    }

    fun getNews(language: Int, category: Category): Flow<PagingData<News>> {
        val newPageData = categoryRepository.getNews(language, category).cachedIn(viewModelScope)
        return newPageData
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewmodel
    }

    companion object : MvRxViewModelFactory<HomeViewmodel, HomeViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: HomeViewState
        ): HomeViewmodel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}