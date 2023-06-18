package dev.slimevr.ios;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.uikit.*;
import org.robovm.apple.webkit.*;


public class WebviewController extends UIViewController {
	private WKWebView webView;

	public WebviewController() {
		var config = new WKWebViewConfiguration();
		config.setApplicationNameForUserAgent("Version/1.0 SlimeVR");
		webView = new WKWebView(CGRect.Zero(), config);
	}

	@Override
	public void viewDidLoad() {
		super.viewDidLoad();

		var index = NSBundle.getMainBundle().findResourceURL("index", "html", "dist");
		webView.loadFileURL(index, NSBundle.getMainBundle().getBundleURL());
	}
}
