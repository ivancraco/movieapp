package com.ivandev.movieapp.core.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ivandev.movieapp.domain.model.ResultModel
import com.ivandev.movieapp.ui.main.fragment.CoruselFragment

class FragmentAdapter(
    private val ml: List<ResultModel>,
    fm: FragmentManager,
    lc: Lifecycle
) : FragmentStateAdapter(fm, lc) {
    override fun getItemCount(): Int = ml.size

    override fun createFragment(position: Int): Fragment {
        return CoruselFragment.newInstance(ml[position])
    }
}