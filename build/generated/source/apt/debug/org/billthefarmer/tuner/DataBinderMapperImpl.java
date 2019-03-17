package org.billthefarmer.tuner;

import android.databinding.DataBinderMapper;
import android.databinding.DataBindingComponent;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.billthefarmer.tuner.databinding.ActivityClientBindingImpl;
import org.billthefarmer.tuner.databinding.ViewGattServerBindingImpl;
import org.billthefarmer.tuner.databinding.ViewLogBindingImpl;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYCLIENT = 1;

  private static final int LAYOUT_VIEWGATTSERVER = 2;

  private static final int LAYOUT_VIEWLOG = 3;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(3);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(org.billthefarmer.tuner.R.layout.activity_client, LAYOUT_ACTIVITYCLIENT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(org.billthefarmer.tuner.R.layout.view_gatt_server, LAYOUT_VIEWGATTSERVER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(org.billthefarmer.tuner.R.layout.view_log, LAYOUT_VIEWLOG);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYCLIENT: {
          if ("layout/activity_client_0".equals(tag)) {
            return new ActivityClientBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_client is invalid. Received: " + tag);
        }
        case  LAYOUT_VIEWGATTSERVER: {
          if ("layout/view_gatt_server_0".equals(tag)) {
            return new ViewGattServerBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for view_gatt_server is invalid. Received: " + tag);
        }
        case  LAYOUT_VIEWLOG: {
          if ("layout/view_log_0".equals(tag)) {
            return new ViewLogBindingImpl(component, new View[]{view});
          }
          throw new IllegalArgumentException("The tag for view_log is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case LAYOUT_VIEWLOG: {
          if("layout/view_log_0".equals(tag)) {
            return new ViewLogBindingImpl(component, views);
          }
          throw new IllegalArgumentException("The tag for view_log is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new com.android.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(4);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "viewModel");
      sKeys.put(2, "serverName");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(3);

    static {
      sKeys.put("layout/activity_client_0", org.billthefarmer.tuner.R.layout.activity_client);
      sKeys.put("layout/view_gatt_server_0", org.billthefarmer.tuner.R.layout.view_gatt_server);
      sKeys.put("layout/view_log_0", org.billthefarmer.tuner.R.layout.view_log);
    }
  }
}
