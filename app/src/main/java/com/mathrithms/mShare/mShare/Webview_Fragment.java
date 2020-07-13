package com.mathrithms.mShare.mShare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.mathrithms.mShare.R;

public class Webview_Fragment extends Fragment {

    public Webview_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView w = (WebView)v.findViewById(R.id.webview);
        w.getSettings().getJavaScriptEnabled();
        w.setWebViewClient(new WebViewClient());
        w.loadUrl("http://opensource.mathrithms.com/");
        return v;
    }
}