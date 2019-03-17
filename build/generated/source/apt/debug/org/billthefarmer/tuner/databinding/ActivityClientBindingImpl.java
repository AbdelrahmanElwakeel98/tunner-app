package org.billthefarmer.tuner.databinding;
import org.billthefarmer.tuner.R;
import org.billthefarmer.tuner.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityClientBindingImpl extends ActivityClientBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(7);
        sIncludes.setIncludes(0, 
            new String[] {"view_log"},
            new int[] {1},
            new int[] {R.layout.view_log});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.client_device_info_text_view, 2);
        sViewsWithIds.put(R.id.stop_scanning_button, 3);
        sViewsWithIds.put(R.id.start_scanning_button, 4);
        sViewsWithIds.put(R.id.server_list_container, 5);
        sViewsWithIds.put(R.id.disconnect_button, 6);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityClientBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 7, sIncludes, sViewsWithIds));
    }
    private ActivityClientBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.TextView) bindings[2]
            , (android.widget.Button) bindings[6]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.Button) bindings[4]
            , (android.widget.Button) bindings[3]
            , (org.billthefarmer.tuner.databinding.ViewLogBinding) bindings[1]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        viewClientLog.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (viewClientLog.hasPendingBindings()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    public void setLifecycleOwner(@Nullable android.arch.lifecycle.LifecycleOwner lifecycleOwner) {
        super.setLifecycleOwner(lifecycleOwner);
        viewClientLog.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeViewClientLog((org.billthefarmer.tuner.databinding.ViewLogBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeViewClientLog(org.billthefarmer.tuner.databinding.ViewLogBinding ViewClientLog, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
        executeBindingsOn(viewClientLog);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): viewClientLog
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}