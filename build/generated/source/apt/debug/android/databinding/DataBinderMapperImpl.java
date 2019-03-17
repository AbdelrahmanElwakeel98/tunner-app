package android.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new org.billthefarmer.tuner.DataBinderMapperImpl());
  }
}
