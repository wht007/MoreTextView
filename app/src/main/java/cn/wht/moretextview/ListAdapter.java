package cn.wht.moretextview;

import android.content.Context;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class ListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private List<String> mData;

    public ListAdapter(List<String> data) {

        super(R.layout.item_list,data);
        mData=data;

    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

        MoreTextView moreTextView=helper.getView(R.id.item_txt);
        moreTextView.setText(mData.get(helper.getLayoutPosition()), helper.getLayoutPosition());

    }
}
