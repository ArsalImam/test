package com.bykea.pk.partner.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentWebViewBinding
import com.bykea.pk.partner.ui.activities.HomeActivity
import com.bykea.pk.partner.utils.Constants.DIMISS_DIALOG_WEBVIEW_LOADING
import com.bykea.pk.partner.utils.Dialogs
import kotlinx.android.synthetic.main.fragment_web_view.*


class WebViewFragment : Fragment() {
    private var mFragmentHeaderEN: String? = null
    private var mFragmentHeaderUR: String? = null
    private var mWebURL: String? = null
    private var mCurrentActivity: HomeActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mFragmentHeaderEN = arguments!!.getString(FRAGMENT_HEADER_EN)
            mFragmentHeaderUR = arguments!!.getString(FRAGMENT_HEADER_UR)
            mWebURL = arguments!!.getString(WEB_URL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentWebViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrentActivity = activity as HomeActivity?
        mCurrentActivity?.setToolbarTitle(mFragmentHeaderEN, mFragmentHeaderUR)
        mCurrentActivity?.hideToolbarLogo()
        mCurrentActivity?.findViewById<View>(R.id.toolbarLine)?.visibility = View.VISIBLE
        mCurrentActivity?.findViewById<View>(R.id.statusLayout)?.visibility = View.VISIBLE
        mCurrentActivity?.hideStatusCompletely()

        Dialogs.INSTANCE.showLoader(mCurrentActivity)

        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.loadUrl(mWebURL)

        Handler().postDelayed({ Dialogs.INSTANCE.dismissDialog() }, DIMISS_DIALOG_WEBVIEW_LOADING)
    }

    companion object {
        private val WEB_URL = "WEB_URL"
        private val FRAGMENT_HEADER_EN = "FRAGMENT_HEADER_EN"
        private val FRAGMENT_HEADER_UR = "FRAGMENT_HEADER_UR"
        fun newInstance(param1: String, param2: String, param3: String): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putString(FRAGMENT_HEADER_EN, param1)
            args.putString(FRAGMENT_HEADER_UR, param2)
            args.putString(WEB_URL, param3)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        mCurrentActivity?.showToolbar()
        mCurrentActivity?.hideUrduTitle()
        super.onDestroyView()
    }
}
