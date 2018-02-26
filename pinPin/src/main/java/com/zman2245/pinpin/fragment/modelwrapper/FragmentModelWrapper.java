package com.zman2245.pinpin.fragment.modelwrapper;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * A fragment whose sole purpose is to wrap a model for a parent fragment
 * or activity
 *
 * @author zack
 */
public class FragmentModelWrapper<T> extends Fragment
{
	T mWrappedModel;

	/**
	 * FragmentModelWrapper construction
	 *
	 * @param model The model to store
	 * @return A new instance of FragmentModelWrapper
	 */
	public static <T> FragmentModelWrapper<T> newInstance(T model)
	{
		FragmentModelWrapper<T> frag = new FragmentModelWrapper<T>();

		frag.setModel(model);

		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	/**
	 * Set the model
	 *
	 * @param model
	 */
	public void setModel(T model)
	{
		mWrappedModel = model;
	}

	/**
	 * Get the model
	 *
	 * @return
	 */
	public T getModel()
	{
		return mWrappedModel;
	}
}
