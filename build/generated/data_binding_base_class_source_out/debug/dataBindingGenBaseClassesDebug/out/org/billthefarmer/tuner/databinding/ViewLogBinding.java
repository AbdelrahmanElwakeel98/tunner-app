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
import android.widget.ScrollView;
import android.widget.TextView;

public abstract class ViewLogBinding extends ViewDataBinding {
  @NonNull
  public final Button clearLogButton;

  @NonNull
  public final ScrollView logScrollView;

  @NonNull
  public final TextView logTextView;

  protected ViewLogBinding(DataBindingComponent _bindingComponent, View _root, int _localFieldCount,
      Button clearLogButton, ScrollView logScrollView, TextView logTextView) {
    super(_bindingComponent, _root, _localFieldCount);
    this.clearLogButton = clearLogButton;
    this.logScrollView = logScrollView;
    this.logTextView = logTextView;
  }

  @NonNull
  public static ViewLogBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root,
      boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ViewLogBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root,
      boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ViewLogBinding>inflate(inflater, org.billthefarmer.tuner.R.layout.view_log, root, attachToRoot, component);
  }

  @NonNull
  public static ViewLogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ViewLogBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ViewLogBinding>inflate(inflater, org.billthefarmer.tuner.R.layout.view_log, null, false, component);
  }

  public static ViewLogBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static ViewLogBinding bind(@NonNull View view, @Nullable DataBindingComponent component) {
    return (ViewLogBinding)bind(component, view, org.billthefarmer.tuner.R.layout.view_log);
  }
}
