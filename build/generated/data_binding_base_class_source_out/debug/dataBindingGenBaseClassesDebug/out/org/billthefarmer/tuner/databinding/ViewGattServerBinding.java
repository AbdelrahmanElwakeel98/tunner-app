package org.billthefarmer.tuner.databinding;

import android.databinding.Bindable;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.billthefarmer.tuner.client.GattServerViewModel;

public abstract class ViewGattServerBinding extends ViewDataBinding {
  @NonNull
  public final Button connectGattServerButton;

  @NonNull
  public final TextView gattServerNameTextView;

  @Bindable
  protected GattServerViewModel mViewModel;

  protected ViewGattServerBinding(DataBindingComponent _bindingComponent, View _root,
      int _localFieldCount, Button connectGattServerButton, TextView gattServerNameTextView) {
    super(_bindingComponent, _root, _localFieldCount);
    this.connectGattServerButton = connectGattServerButton;
    this.gattServerNameTextView = gattServerNameTextView;
  }

  public abstract void setViewModel(@Nullable GattServerViewModel viewModel);

  @Nullable
  public GattServerViewModel getViewModel() {
    return mViewModel;
  }

  @NonNull
  public static ViewGattServerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ViewGattServerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ViewGattServerBinding>inflate(inflater, org.billthefarmer.tuner.R.layout.view_gatt_server, root, attachToRoot, component);
  }

  @NonNull
  public static ViewGattServerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ViewGattServerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ViewGattServerBinding>inflate(inflater, org.billthefarmer.tuner.R.layout.view_gatt_server, null, false, component);
  }

  public static ViewGattServerBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static ViewGattServerBinding bind(@NonNull View view,
      @Nullable DataBindingComponent component) {
    return (ViewGattServerBinding)bind(component, view, org.billthefarmer.tuner.R.layout.view_gatt_server);
  }
}
