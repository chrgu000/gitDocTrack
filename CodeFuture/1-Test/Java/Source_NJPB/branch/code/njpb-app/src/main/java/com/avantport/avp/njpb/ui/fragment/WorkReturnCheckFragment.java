package com.avantport.avp.njpb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.avantport.avp.njpb.R;
import com.avantport.avp.njpb.ui.work.CheckDetailsActivity;
import com.avantport.avp.njpb.adapter.FinalListAdapter;
import com.avantport.avp.njpb.bean.MineTakeOutbean;
import com.avantport.avp.njpb.constant.Constant;
import com.avantport.avp.njpb.okhttp.OkHttpBaseCallback;
import com.avantport.avp.njpb.okhttp.OkHttpUtils;
import com.avantport.avp.njpb.uitls.DateUtil;
import com.avantport.avp.njpb.uitls.ToastUtil;
import com.avantport.avp.njpb.view.StateLayout;
import com.avantport.avp.njpb.view.SwipeRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by len on 2017/8/19.
 * 物料审核中的归还申请
 */

public class WorkReturnCheckFragment extends BaseFragment implements SwipeRefreshView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, StateLayout.OnReloadListener, AdapterView.OnItemClickListener {
    private StateLayout mStateLayout;
    private SwipeRefreshView mRefresh;
    private ListView mList;
    private Map<String, Object> params = new HashMap<>();
    private List<MineTakeOutbean.ResultsBean> datas = new ArrayList<>();//储存请求的数据
    private FinalListAdapter<MineTakeOutbean.ResultsBean> mFinalListAdapter;
    private boolean isRefresh;
    private boolean isLoad;

    @Override
    protected View getFragmentView(LayoutInflater inflater, ViewGroup container) {
        if (mStateLayout == null) {
            mStateLayout = new StateLayout(mContext);
        }
        return mStateLayout;
    }

    @Override
    protected void getFragmentData(View view) {
        View inflate = View.inflate(mContext, R.layout.refresh_list, null);
        mRefresh = (SwipeRefreshView) inflate.findViewById(R.id.refresh);
        mList = (ListView) inflate.findViewById(R.id.list);
        mStateLayout.bindSuccessView(inflate);
        mRefresh.setOnLoadMoreListener(this);
        mRefresh.setOnRefreshListener(this);
        mStateLayout.setOnReloadListener(this);

        mList.setOnItemClickListener(this);
    }

    @Override
    protected void initDatas() {
        params.clear();
        params.put("stockWorkOrderTypeId", "2");
        params.put("sort", "lastUpdateTime,desc");
        params.put("firstOperationTime", DateUtil.getTodayDate());
        mStateLayout.showLoadingView();
        getInDatas(Constant.WORK_CHECK_APPLY_LOADMORE, params);
    }

    public void getInDatas(String url, Map<String, Object> params) {
        OkHttpUtils.getInstance().get(url, params, null, new OkHttpBaseCallback<MineTakeOutbean>() {

            @Override
            public void onSuccess(Response response, MineTakeOutbean mineTakeOutbean) {
                List<MineTakeOutbean.ResultsBean> results = mineTakeOutbean.getResults();
                if (results != null && results.size() == 0) {
                    if (isRefresh) {
                        ToastUtil.show(mContext, "没有更新的数据了");
                        mRefresh.setRefreshing(false);
                        isRefresh = false;
                    } else if (isLoad) {
                        ToastUtil.show(mContext, "没有更多的数据了");
                        mRefresh.setLoading(false);
                        isLoad = false;
                    } else {
                        mStateLayout.showEmptyView();
                    }
                } else {
                    mStateLayout.showSuccessView();
                    if (isRefresh) {
                        datas.addAll(0, results);
                        mRefresh.setRefreshing(false);
                        isRefresh = false;
                        mFinalListAdapter.notifyDataSetChanged();
                    } else if (isLoad) {
                        datas.addAll(results);
                        mRefresh.setLoading(false);
                        isLoad = false;
                        mFinalListAdapter.notifyDataSetChanged();
                    } else {
                        datas.addAll(results);
                        setIntDatas();
                    }
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                if (isRefresh) {
                    ToastUtil.show(mContext, "网络异常");
                    mRefresh.setRefreshing(false);
                    isRefresh = false;
                } else if (isLoad) {
                    ToastUtil.show(mContext, "网络异常");
                    mRefresh.setLoading(false);
                    isLoad = false;
                } else {
                    mStateLayout.showErrorView();
                }
            }
        });
    }


    private void setIntDatas() {
        mFinalListAdapter = new FinalListAdapter<>(datas, R.layout.item_mine_takeout_apply, new FinalListAdapter.AdapterListener<MineTakeOutbean.ResultsBean>() {
            @Override
            public void bindView(FinalListAdapter.FinalViewHolder finalViewHolder, MineTakeOutbean.ResultsBean resultsBean) {
                TextView order_code = (TextView) finalViewHolder.getViewById(R.id.order_code);
                TextView apply_name = (TextView) finalViewHolder.getViewById(R.id.apply_name);
                TextView check_state = (TextView) finalViewHolder.getViewById(R.id.check_state);
                TextView apply_company = (TextView) finalViewHolder.getViewById(R.id.conmpany);
                TextView apply_time = (TextView) finalViewHolder.getViewById(R.id.apple_time);
                TextView character = (TextView) finalViewHolder.getViewById(R.id.character);
                character.setText("申请人 :");
                check_state.setVisibility(View.VISIBLE);
                check_state.setText(resultsBean.getStockWorkOrderStatusNameCn());
                order_code.setText(resultsBean.getSerialNo());
                apply_name.setText(resultsBean.getProcessUserName());
                apply_company.setText(resultsBean.getCorpName());
                apply_time.setText(DateUtil.formatDate(resultsBean.getApplyTime()));
            }
        });

        mList.setAdapter(mFinalListAdapter);
    }

    @Override
    public void onLoadMore() {
        if (datas != null && datas.size() > 0) {
            long createTime = datas.get(datas.size() - 1).getLastUpdateTime();
            params.clear();
            params.put("stockWorkOrderTypeId", "2");
            params.put("sort", "lastUpdateTime,desc");
            params.put("firstOperationTime", DateUtil.formatLastDate(createTime));
            isLoad = true;
            getInDatas(Constant.WORK_CHECK_APPLY_LOADMORE, params);
        } else {
            return;
        }
    }

    @Override
    public void onRefresh() {
        //获取列表的第一条信息的时间

        if (datas != null && datas.size() > 0) {
            long createTime = datas.get(0).getLastUpdateTime();
            params.clear();
            params.put("stockWorkOrderTypeId", "2");
//            params.put("sort", "lastUpdateTime,desc");
            params.put("firstOperationTime", DateUtil.formatDate(createTime));
            isRefresh = true;
            getInDatas(Constant.WORK_CHECK_APPLY_REFRESH, params);
        } else {
            onReload();
        }
    }


    @Override
    public void onReload() {
        //获取网络数据
        params.clear();
        params.put("stockWorkOrderTypeId", "2");
        params.put("sort", "lastUpdateTime,desc");
        params.put("firstOperationTime", DateUtil.getTodayDate());
        getInDatas(Constant.WORK_CHECK_APPLY_LOADMORE, params);
    }


    @Override
    public void onDestroy() {
        datas.clear();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //条目点击
        MineTakeOutbean.ResultsBean resultsBean = datas.get(position);

        Intent intent = new Intent(mContext, CheckDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("title", "归还审核");
        bundle.putSerializable("resultsBean", resultsBean);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivityForResult(intent, 300);
        activityJumpAnim();
    }


    @Override
    public void onActivityResultNestedCompat(int requestCode, int resultCode, Intent data) {
        if (requestCode == 300 && resultCode == 200) {
            int position = data.getIntExtra("position", 0);
            //拿到审核过了的信息，删除列表对应的信息，刷新列表
            datas.remove(position);
            mFinalListAdapter.notifyDataSetChanged();
            onRefresh();
            //全部审核完了，进行提示
            if (datas.size() == 0) {
                ToastUtil.show(mContext, "全部审核完了！");
            }
        }
    }
}
