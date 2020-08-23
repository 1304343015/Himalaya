package com.example.himalaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.adapters.SuggestListAdapter;
import com.example.himalaya.interfaces.ISearchViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.utils.UIUtil;
import com.example.himalaya.views.FlowTextLayout;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, ISearchViewCallback, RecommendListAdapter.OnItemClickListener {
    private ImageView back_btn;
    private EditText search_et;
    private TextView search_btn;
    private FlowTextLayout hot_word_layout;
    private FrameLayout search_content;
    private RecyclerView search_list;
    private RecyclerView suggest_list;
    private UILoader mUILoader;
    private TwinklingRefreshLayout search_refresh;

    private RecommendListAdapter adapter;
    private SuggestListAdapter suggestAdapter;
    private static final String TAG = "SearchActivity";

    private SearchPresenter searchPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();


        initPresenter();
    }
    private boolean isReferesh=false;
    private void initEvent() {
        back_btn.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        mUILoader.setOnReTryListener(new UILoader.onReTryListener() {
            @Override
            public void onReTry() {
                searchPresenter.research();
            }
        });

        hot_word_layout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                search_et.setText(text);
                search_et.setSelection(text.length());
                searchPresenter.doSearch(text);
            }
        });

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(search_et.getText().toString())){
                    hideSuccessView();
                    hot_word_layout.setVisibility(View.VISIBLE);
                }else{
                    LogUtil.d(TAG,"afterTextChanged:"+search_et.getText().toString());
                    searchPresenter.getRecommendByWord(search_et.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        search_refresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                isReferesh=true;
                searchPresenter.loadMore();
            }
        });
    }

    private void initView(){
        back_btn=findViewById(R.id.back_btn);
        search_et=findViewById(R.id.search_et);
        search_btn=findViewById(R.id.search_btn);
        search_content=findViewById(R.id.search_content);
        mUILoader=new UILoader(this) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return successView();
            }
        };
        if(mUILoader.getParent() instanceof ViewGroup){
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }
        search_content.addView(mUILoader);
        mUILoader.updateState(UILoader.UIState.SUCCESS);
    }

    private View successView() {
        View view= LayoutInflater.from(this).inflate(R.layout.item_search_success,null);
        search_refresh=view.findViewById(R.id.search_refresh);
        search_refresh.setEnableRefresh(false);
        search_list=view.findViewById(R.id.search_list);
        hot_word_layout=view.findViewById(R.id.hot_word_layout);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        search_list.setLayoutManager(manager);
        search_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(SearchActivity.this,5);
                outRect.bottom= UIUtil.dip2px(SearchActivity.this,5);
                outRect.left=UIUtil.dip2px(SearchActivity.this,5);
                outRect.right=UIUtil.dip2px(SearchActivity.this,5);
            }
        });
        adapter=new RecommendListAdapter(this);
        adapter.setOnItemClickListener(this);
        search_list.setAdapter(adapter);

        suggest_list=view.findViewById(R.id.suggest_list);
        LinearLayoutManager suggestManager=new LinearLayoutManager(this);
        suggest_list.setLayoutManager(suggestManager);
        suggestAdapter=new SuggestListAdapter();
        suggestAdapter.setOnSuggestItemClickListener(new SuggestListAdapter.OnSuggestItemClickListener() {
            @Override
            public void onItemClick(String suggest) {
                searchPresenter.doSearch(suggest);
            }
        });
        suggest_list.setAdapter(suggestAdapter);

        return view;
    }

    private void hideSuccessView(){
        hot_word_layout.setVisibility(View.GONE);
        suggest_list.setVisibility(View.GONE);
        search_list.setVisibility(View.GONE);
    }

    private void initPresenter(){
        searchPresenter=SearchPresenter.getInstance();
        searchPresenter.registerViewListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.search_btn:
                String keyword=search_et.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    searchPresenter.doSearch(keyword);
                }
                break;
        }
    }


    @Override
    public void onSearchResultLoad(List<Album> result) {
        LogUtil.d(TAG,"onSearchResultLoad");
        adapter.setList(result);
        hideSuccessView();
        search_list.setVisibility(View.VISIBLE);
        mUILoader.updateState(UILoader.UIState.SUCCESS);

        InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(search_et.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public void onHotWordLoad(List<HotWord> result) {
        LogUtil.d(TAG,"onHotWordLoad");
        List<String> wordList=new ArrayList<>();
        for (HotWord hotWord : result) {
            wordList.add(hotWord.getSearchword());
        }
        hot_word_layout.setTextContents(wordList);
        hideSuccessView();
        hot_word_layout.setVisibility(View.VISIBLE);
        mUILoader.updateState(UILoader.UIState.SUCCESS);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        if(isReferesh){
            isReferesh=false;
            search_refresh.finishLoadmore();
        }
        if(isOkay){
            adapter.setList(result);
            hideSuccessView();
            search_list.setVisibility(View.VISIBLE);
            mUILoader.updateState(UILoader.UIState.SUCCESS);
            //Toast.makeText(this,"新增"+result.size()+"条记录",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"无更多记录",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRecommendResultLoad(List<QueryResult> result) {
            LogUtil.d(TAG,"onRecommendResultLoad:size--->"+result.size());
            suggestAdapter.setData(result);
            hideSuccessView();
            suggest_list.setVisibility(View.VISIBLE);
            mUILoader.updateState(UILoader.UIState.SUCCESS);
    }

    @Override
    public void onDataEmpty() {
        mUILoader.updateState(UILoader.UIState.EMPTY);
    }

    @Override
    public void onNetworkError() {
        mUILoader.updateState(UILoader.UIState.NETWORK_ERROR);
    }

    @Override
    public void onLoading() {
        mUILoader.updateState(UILoader.UIState.LOADING);
    }

    @Override
    public void onItemClick(Album album) {
        AlbumDetailPresenter presenter=AlbumDetailPresenter.getInstance();
        presenter.setTargetAlbum(album);
        startActivity(new Intent(SearchActivity.this,AlbumDetailActivity.class));
    }

    @Override
    public void onItemLongClick(Album album) {

    }
}