package com.paar.ch8;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Instructions extends Activity {
	
	private WebView mWebView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instructions_layout);
		mWebView = (WebView) findViewById(R.id.instructions_webview);
		
		WebSettings webSettings = mWebView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        
        WebChromeClient client = new WebChromeClient();
        mWebView.setWebChromeClient(client);
                
		mWebView.loadUrl("file:///android_asset/help/"+getResources().getString(R.string.help_file));
	}
}
