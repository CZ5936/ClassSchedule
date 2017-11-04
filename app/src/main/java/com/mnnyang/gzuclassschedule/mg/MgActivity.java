package com.mnnyang.gzuclassschedule.mg;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.mnnyang.gzuclassschedule.BaseActivity;
import com.mnnyang.gzuclassschedule.R;
import com.mnnyang.gzuclassschedule.course.CourseActivity;
import com.mnnyang.gzuclassschedule.data.bean.CsItem;
import com.mnnyang.gzuclassschedule.utils.DialogHelper;
import com.mnnyang.gzuclassschedule.utils.DialogListener;
import com.mnnyang.gzuclassschedule.utils.ToastUtils;
import com.mnnyang.gzuclassschedule.utils.spec.RecyclerBaseAdapter;

import java.util.ArrayList;

public class MgActivity extends BaseActivity implements MgContract.View {

    private MgPresenter mPresenter;
    private MgAdapter mAdapter;
    ArrayList<CsItem> csItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initBackToolbar("课表管理");
        initRecyclerView();
        mPresenter = new MgPresenter(this, csItems);
        mPresenter.start();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mAdapter = new MgAdapter(R.layout.layout_item_cs, csItems);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getBaseContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setItemClickListener(new RecyclerBaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerBaseAdapter.ViewHolder holder) {
                switchDialog((Integer) view.getTag());
            }

            @Override
            public void onItemLongClick(View view, RecyclerBaseAdapter.ViewHolder holder) {
                deleteDialog((Integer) view.getTag());
            }
        });
    }

    private void deleteDialog(final int tag) {
        DialogHelper dh = new DialogHelper();
        dh.showNormalDialog(this, "警告", "确认要删除该课表吗?",
                new DialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialog, int which) {
                        super.onPositive(dialog, which);
                        mPresenter.deleteCsName(tag);
                        notifiUpdate();
                    }
                });
    }

    private void switchDialog(final int tag) {
        DialogHelper dh = new DialogHelper();
        dh.showNormalDialog(this, "警告", "确认要切换到该课表吗?",
                new DialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialog, int which) {
                        super.onPositive(dialog, which);
                        mPresenter.switchCsName(tag);
                        notifiUpdate();
                    }
                });
    }

    @Override
    public void showList(ArrayList<CsItem> csNames) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNotice(String notice) {
        ToastUtils.show(notice);
    }

    @Override
    public void gotoCourseActivity() {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通知更新
     */
    private void notifiUpdate() {
        Intent intent = new Intent();
        intent.setAction("com.mnnyang.update");
        sendBroadcast(intent);
    }
}
