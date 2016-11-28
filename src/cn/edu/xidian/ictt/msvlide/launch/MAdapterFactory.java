package cn.edu.xidian.ictt.msvlide.launch;


import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.ILaunchable;

public class MAdapterFactory implements IAdapterFactory{

	private static final Class<?>[] TYPES = { ILaunchable.class };
    
	@Override
	public <T> T getAdapter(Object arg0, Class<T> arg1) {
		return null;
	}

    
	@Override
	public Class<?>[] getAdapterList() {
		return TYPES;
	}

}
