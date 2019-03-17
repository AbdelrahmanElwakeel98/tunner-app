package org.billthefarmer.tuner.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ActivityClientBinding extends ViewDataBinding {
  @NonNull
  public final TextView clientDeviceInfoTextView;

  @NonNull
  public final Button disconnectButton;

  @NonNull
  public final LinearLayout serverListContainer;

  @NonNull
  public final Button startScanningButton;

  @NonNull
  public final Button stopScanningButton;

  @NonNull
  public final ViewLogBinding viewClientLog;

  protected ActivityClientBinding(DataBindingComponent _bindingComponent, View _root,
      int _localFieldCount, TextView clientDeviceInfoTextView, Button disconnectButton,
      LinearLayout serverListContainer, Button startScanningButton, Button stopScanningButton,
      ViewLogBinding viewClientLog) {
    super(_bindingComponent, _root, _localFieldCount);
    this.clientDeviceInfoTextView = clientDeviceInfoTextView;
    this.disconnectButton = disconnectButton;
    this.serverListContainer = serverListContainer;
    this.startScanningButton = startScanningButton;
    this.stopScanningButton = stopScanningButton;
    this.viewClientLog = viewClientLog;
    setContainedBinding(this.viewClientLog);;
  }

  @NonNull
  public static ActivityClientBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ActivityClientBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ActivityClientBinding>inflate(inflater, org.billthefarmer.tuner.R.layout.activity_client, root, attachToRoot, component);
  }

  @NonNull
  public static ActivityClientBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ActivityClientBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ActivityClientBinding>inflate(inflater, org.billthefarmer.tuner.R.layout.activity_client, null, false, component);
  }

  public static ActivityClientBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static ActivityClientBinding bind(@NonNull View view,
      @Nullable DataBindingComponent component) {
    return (ActivityClientBinding)bind(component, view, org.billthefarmer.tuner.R.layout.activity_client);
  }
}
