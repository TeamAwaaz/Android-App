package teamawaazdtu.com.awaaz;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class WebViewFragment extends Fragment {
    private WebView view;
    ProgressBar progressBar;
    public static WebViewFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        WebViewFragment secondFragment = new WebViewFragment();
        secondFragment.setArguments(args);
        return secondFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_web_view, container, false);
        view = (WebView) rootView.findViewById(R.id.webView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new MyBrowser());
        view.loadUrl("http://www.google.com"); //try js alert
        view.setWebChromeClient(new WebChromeClient()); // adding js alert support
        return rootView;
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //ketika disentuh tombol back
        if ((keyCode == KeyEvent.KEYCODE_BACK) && view.canGoBack()) {
            view.goBack(); //method goback() dieksekusi untuk kembali pada halaman sebelumnya
            return true;
        }
        // Jika tidak ada history (Halaman yang sebelumnya dibuka)
        // maka akan keluar dari activity
        return false;
    }

}