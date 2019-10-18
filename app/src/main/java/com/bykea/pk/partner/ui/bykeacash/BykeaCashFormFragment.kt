package com.bykea.pk.partner.ui.bykeacash

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentBykeaCashFormBinding

class BykeaCashFormFragment : Fragment() {
    private lateinit var binding: FragmentBykeaCashFormBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bykea_cash_form, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
