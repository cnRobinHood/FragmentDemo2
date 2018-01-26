package com.xiezh.findlost.fragment;

/**
 * Created by xiezh on 2017/10/29.
 */

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.xiezh.findlost.adapter.MyAdapter;
import com.xiezh.findlost.service.ItemService;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

@SuppressLint({"NewApi", "ValidFragment"})
public class NewListFragment extends Fragment {
    private FragmentManager fManager;
    private ListView list_news;
    private MyAdapter myAdapter;
    private Handler myHandler;
    private ItemService.ItemBinder binder;

    public NewListFragment(FragmentManager fManager, ItemService.ItemBinder binder) {
        this.fManager = fManager;
        this.binder = binder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_newlist, container, false);
        myHandler = DataManager.myHandler;

        list_news = (ListView) view.findViewById(R.id.list_news);

        myAdapter = new MyAdapter(getActivity(), fManager, binder);

        list_news.setAdapter(myAdapter);
        /*list_news.setOnItemClickListener(this);*/

        list_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstVisibleItemView = list_news.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        myHandler.sendEmptyMessage(0x113);
                    }
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = list_news.getChildAt(list_news.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == list_news.getHeight()) {
                        myHandler.sendEmptyMessage(0x115);
                    }
                } else {
                    myHandler.sendEmptyMessage(0x114);
                }
            }
        });
        return view;
    }

    //为item添加点击事件 实现了apater.onitemClickListener
    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        FragmentTransaction fTransaction = fManager.beginTransaction();
        NewContentFragment ncFragment = new NewContentFragment();
        Bundle bd = new Bundle();
        bd.putString("content", datas.get(position).getNew_content());
        ncFragment.setArguments(bd);
        //获取Activity的控件
        TextView txt_title = (TextView) getActivity().findViewById(R.id.txt_title);
        txt_title.setText("失物详情");
        fTransaction.replace(R.id.fl_content, ncFragment);
        //调用addToBackStack将Fragment添加到栈中
        fTransaction.addToBackStack(null);
        fTransaction.commit();
    }*/

    public void reFresh() {
        if (myAdapter != null)
            myAdapter.notifyDataSetChanged();
    }
}
