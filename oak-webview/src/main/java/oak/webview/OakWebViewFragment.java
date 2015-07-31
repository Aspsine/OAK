package oak.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class OakWebViewFragment extends Fragment {

    public static final int PROVIDED_LAYOUT = 0;
    private String url;
    private WebView webView;
    private View refresh, progress, back, fwd, container;
    private boolean hidden, openInBrowserEnabled = false, showControls = true, refreshInMenuEnabled = false;
    private boolean fadeControls = true;
    private long fadeTimeout = 1500;
    private int layoutId = R.layout.webview;
    private float fadeoutMinimum = 0.2f;
    private float fadeoutMaximum = 1.0f;


    public static class BundleBuilder {
        private Bundle bundle;

        public BundleBuilder(Bundle bundle) {
            this.bundle = bundle;
        }

        public BundleBuilder(String url) {
            bundle = new Bundle();
            bundle.putString(Extras.EXTRA_URL, url);
        }

        public BundleBuilder showControls(boolean showControls) {
            bundle.putBoolean(Extras.EXTRA_SHOW_CONTROLS, showControls);
            return this;
        }

        public BundleBuilder fadeControls(boolean fadeControls) {
            bundle.putBoolean(Extras.EXTRA_CONTROL_FADE, fadeControls);
            return this;
        }

        public BundleBuilder layoutId(int layoutId) {
            bundle.putInt(Extras.EXTRA_LAYOUT, layoutId);
            return this;
        }

        public BundleBuilder fadeTimeout(long milliseconds) {
            bundle.putLong(Extras.EXTRA_FADE_TIMEOUT, milliseconds);
            return this;
        }

        public BundleBuilder maxControlAlpha(float maxAlpha) {
            bundle.putFloat(Extras.EXTRA_FADE_MAX, maxAlpha);
            return this;
        }

        public BundleBuilder minControlAlpha(float minAlpha) {
            bundle.putFloat(Extras.EXTRA_FADE_MIN, minAlpha);
            return this;
        }

        public BundleBuilder openInBrowserEnabled(boolean enabled) {
            bundle.putBoolean(Extras.EXTRA_OPEN_IN_BROWSER, enabled);
            return this;
        }

        public BundleBuilder refreshInMenuEnabled(boolean enabled) {
            bundle.putBoolean(Extras.EXTRA_REFRESH_IN_MENU, enabled);
            return this;
        }

        public Bundle build() {
            return bundle;
        }
    }

    /**
     * Sets whether buttons fade out after touch
     *
     * @param fadeControls whether to fade controls after inactivity
     */
    public void setFadeControls(boolean fadeControls) {
        this.fadeControls = fadeControls;
    }

    /**
     * Sets whether WebView should show controls
     *
     * @param showControls whether to show controls
     */
    public void setShowControls(boolean showControls) {
        this.showControls = showControls;
    }

    /**
     * Sets time in seconds for how long after touch buttons fade out
     *
     * @param fadeTimeout timeout for fade in seconds
     */
    public void setFadeTimeout(long fadeTimeout) {
        this.fadeTimeout = fadeTimeout;
    }

    /**
     * Sets maximum alpha when buttons fade in
     *
     * @param maxAlpha maximum alpha for controls.
     */
    public void setMaximumAlpha(float maxAlpha) {
        this.fadeoutMaximum = maxAlpha;
    }

    /**
     * Set's minimum alpha when buttons fade out
     *
     * @param minAlpha minimum alpha for controls
     */
    public void setMinimumAlpha(float minAlpha) {
        this.fadeoutMinimum = minAlpha;
    }

    /**
     * Set Whether a "open in browser" option is shown in menu.
     *
     * @param openInBrowserEnabled whether to open links in device default browser
     */
    public void setOpenInBrowserEnabled(boolean openInBrowserEnabled) {
        this.openInBrowserEnabled = openInBrowserEnabled;
    }

    public void setRefreshInMenuEnabled(boolean refreshInMenuEnabled) {
        this.refreshInMenuEnabled = refreshInMenuEnabled;
    }

    public void setWebViewClient(WebViewClient client) {
        webView.setWebViewClient(client);
    }

    public void setwebChromeClient(WebChromeClient client) {
        webView.setWebChromeClient(client);
    }

    public void back() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        }
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public boolean canGoForward() {
        return webView.canGoForward();
    }

    public void forward() {
        if (webView != null && webView.canGoForward()) {
            webView.goForward();
        }
    }

    public void refresh() {
        if (webView != null) {
            webView.reload();
        }
    }

    public static OakWebViewFragment getInstance(String url) {
        return getInstance(new BundleBuilder(url).build());
    }

    public static OakWebViewFragment getInstance(Bundle arguments) {
        OakWebViewFragment fragment = new OakWebViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(Extras.EXTRA_URL);
        setOpenInBrowserEnabled(getArguments().getBoolean(Extras.EXTRA_OPEN_IN_BROWSER, true));
        setFadeControls(getArguments().getBoolean(Extras.EXTRA_CONTROL_FADE, true));
        setShowControls(getArguments().getBoolean(Extras.EXTRA_SHOW_CONTROLS, true));
        setFadeTimeout(getArguments().getLong(Extras.EXTRA_FADE_TIMEOUT, 1500l));
        setMaximumAlpha(getArguments().getFloat(Extras.EXTRA_FADE_MAX, 1.0f));
        setMinimumAlpha(getArguments().getFloat(Extras.EXTRA_FADE_MIN, 0.2f));
        setRefreshInMenuEnabled(getArguments().getBoolean(Extras.EXTRA_REFRESH_IN_MENU, false));
        layoutId = getArguments().getInt(Extras.EXTRA_LAYOUT, R.layout.webview);
        setHasOptionsMenu(refreshInMenuEnabled || openInBrowserEnabled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView) view.findViewById(R.id.webview);
        if (webView == null) {
            throw new IllegalStateException("Layout used with this webview must contain a WebView with the ID R.id.webview");
        }
        if (showControls) {
            back = view.findViewById(R.id.back);
            fwd = view.findViewById(R.id.forward);
            refresh = view.findViewById(R.id.refresh);
            progress = view.findViewById(R.id.progress);
            if (back != null) {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                    }
                });
            }
            if (fwd != null) {
                fwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (webView.canGoForward()) {
                            webView.goForward();
                        }
                    }
                });
            }
            if (refresh != null) {
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        webView.reload();
                        progress.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.INVISIBLE);
                    }
                });
            }
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (hidden) {
                        hidden = false;
                        unHide();
                    } else {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hide();
                                hidden = true;
                            }
                        }, fadeTimeout);
                    }
                    return false;
                }
            });
        } else {
            view.findViewById(R.id.button_container).setVisibility(View.GONE);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setSupportZoom(!fadeControls);
        webView.getSettings().setBuiltInZoomControls(!fadeControls);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (showControls) {
                    refresh.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (showControls) {
                    refresh.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                    configureButtons(view);
                }
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NewApi")
    private void unHide() {
        configureButtons(webView);
        if (fadeControls) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                if (refresh != null) {
                    refresh.animate().alpha(fadeoutMaximum);
                }
                if (progress != null) {
                    progress.animate().alpha(fadeoutMaximum);
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private void hide() {
        configureButtons(webView);

        if (fadeControls) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                if (refresh != null) {
                    refresh.animate().alpha(fadeoutMinimum);
                }
                if (progress != null) {
                    progress.animate().alpha(fadeoutMinimum);
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private void configureButtons(WebView webView) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (webView.canGoBack()) {
                back.animate().alpha(hidden && fadeControls ? fadeoutMinimum : fadeoutMaximum);
                back.setEnabled(true);
            } else {
                back.animate().alpha(fadeoutMinimum);
                back.setEnabled(false);
            }
            if (webView.canGoForward()) {
                fwd.animate().alpha(hidden && fadeControls ? fadeoutMinimum : fadeoutMaximum);
                fwd.setEnabled(true);
            } else {
                fwd.animate().alpha(fadeoutMinimum);
                fwd.setEnabled(false);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (openInBrowserEnabled) {
            menu.add(Menu.NONE, R.id.oak_menu_open_in_broswer, Menu.NONE, R.string.open_in_browser);
        }
        if (refreshInMenuEnabled) {
            menu.add(Menu.NONE, R.id.oak_menu_refresh, Menu.NONE, R.string.refresh_in_menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.oak_menu_open_in_broswer) {
            Intent toBroswer = new Intent(Intent.ACTION_VIEW);
            toBroswer.setData(Uri.parse(webView.getUrl()));
            startActivity(toBroswer);
        }
        if (item.getItemId() == R.id.oak_menu_refresh) {
            webView.reload();
        }
        return super.onOptionsItemSelected(item);
    }
}
