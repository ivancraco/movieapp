package com.ivandev.movieapp.ui.main.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivandev.movieapp.R
import com.ivandev.movieapp.core.common.CheckNetwork
import com.ivandev.movieapp.core.paging.LoaderAdapter
import com.ivandev.movieapp.databinding.FragmentSearchBinding
import com.ivandev.movieapp.ui.main.MainViewModel
import com.ivandev.movieapp.ui.main.fragment.common.SeeDetail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var pagingSearchAdapter: SearchAdapter
    private lateinit var paginedRecyclerView: RecyclerView
    private lateinit var headerAdapter: LoaderAdapter
    private lateinit var footerAdapter: LoaderAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var textViewNoResults: TextView
    private val searchViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        inflateView(inflater, container)
        setUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            searchViewModel.pager.collectLatest {
                pagingSearchAdapter.submitData(it)
            }
        }
    }

    override fun onStart() {
        val targetPosition = searchViewModel.searchAdapterPosition
        paginedRecyclerView.scrollToPosition(targetPosition)
        if (!CheckNetwork.isConected(requireContext())) {
            searchViewModel.searchByQuery("")
        } else {
            if (searchViewModel.fetchAgain) {
                searchViewModel.searchByQuery(searchViewModel.query)
                searchViewModel.fetchAgain = false
            }
        }
        super.onStart()
    }

    private fun setUI() {
        setBinding()
        setPagingSearchAdapter()
        setPagingSearchStateListener()
        setLayoutManager()
        getHeaderAdapter()
        getFooterAdapter()
        setPagindeRecyclerView()
        setSearchViewListener()
        spanSizeLookUp()
    }

    private fun setSearchViewListener() {
        searchView.setOnQueryTextListener(this@SearchFragment)
    }

    private fun setPagindeRecyclerView() {
        paginedRecyclerView.itemAnimator = null
        paginedRecyclerView.adapter = pagingSearchAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter
        )
        paginedRecyclerView.layoutManager = gridLayoutManager
    }

    private fun setLayoutManager() {
        gridLayoutManager = GridLayoutManager(context, 2)
    }

    private fun getFooterAdapter() {
        footerAdapter = LoaderAdapter(pagingSearchAdapter::retry)
    }

    private fun getHeaderAdapter() {
        headerAdapter = LoaderAdapter(pagingSearchAdapter::retry)
    }

    private fun setBinding() {
        paginedRecyclerView = binding.rvSearch
        searchView = binding.searchView
        textViewNoResults = binding.tvEmpty
        progressBar = binding.progressBar
    }

    private fun setPagingSearchStateListener() {
        pagingSearchAdapter.addLoadStateListener {
            progressBar.isVisible = it.source.refresh is LoadState.Loading
            paginedRecyclerView.isVisible = it.source.refresh is LoadState.NotLoading
            if (it.source.refresh is LoadState.NotLoading
                && it.append.endOfPaginationReached
                && pagingSearchAdapter.itemCount < 1
            ) {
                textViewNoResults.isVisible = true
                paginedRecyclerView.isVisible = false
            } else {
                textViewNoResults.isVisible = false
            }
        }
    }

    private fun setPagingSearchAdapter() {
        pagingSearchAdapter = SearchAdapter {
            SeeDetail.startDetailActivity(requireContext(), it)
        }
    }

    /** Allows the LoadStateAdapter to be centered in the middle
     * of the GridLayoutmanager. */
    private fun spanSizeLookUp() {
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0 && headerAdapter.itemCount > 0) {
                    2
                } else if (position == pagingSearchAdapter.itemCount
                    && footerAdapter.itemCount > 0
                ) {
                    2
                } else {
                    1
                }
            }
        }
    }

    private fun inflateView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (CheckNetwork.isConected(requireContext())) {
            searchViewModel.cancelPagingSource()
            paginedRecyclerView.scrollToPosition(0)
            val searchQuery = query ?: ""
            searchViewModel.searchByQuery(searchQuery)
        } else {
            searchViewModel.searchByQuery("")
            Toast.makeText(
                requireContext(),
                activity?.getString(R.string.no_connection_message),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    override fun onStop() {
        val targetPosition = gridLayoutManager.findFirstVisibleItemPosition()
        searchViewModel.searchAdapterPosition = targetPosition
        super.onStop()
    }

    override fun onDestroy() {
        if (!CheckNetwork.isConected(requireContext())) {
            searchViewModel.cancelPagingSource()
            searchViewModel.searchAdapterPosition = 0
            searchViewModel.fetchAgain = true
        }
        super.onDestroy()
    }

}