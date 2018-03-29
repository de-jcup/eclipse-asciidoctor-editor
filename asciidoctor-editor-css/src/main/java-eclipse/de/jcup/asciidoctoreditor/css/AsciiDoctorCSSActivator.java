package de.jcup.asciidoctoreditor.css;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AsciiDoctorCSSActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		AsciiDoctorCSSActivator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		AsciiDoctorCSSActivator.context = null;
	}

}
