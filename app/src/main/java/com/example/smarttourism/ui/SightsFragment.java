package com.example.smarttourism.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.smarttourism.R;
import com.example.smarttourism.adapter.MapAdapter;
import com.example.smarttourism.entity.AddressBean;

import java.util.ArrayList;
import java.util.List;

public class SightsFragment extends Fragment implements AMapLocationListener, PoiSearch.OnPoiSearchListener {
    private static final String EXTRA_CONTENT = "content";
    //东湖景区大致中心
    private static final LatLonPoint EAST_LAKE_CENTER = new LatLonPoint(30.55, 114.4113);
    //每页展示数量
    private static final int PAGE_SIZE = 30;
    private String surroundings;
    //当前位置
    private double currLat, currLon;
    //页码
    private int pageNum = 0;
    //判断页面，实现更多加载功能
    private boolean isLoading = false;
    private boolean isLastPage = false;
    //搜索参数缓存
    private double searchLat, searchLon;
    private String searchKeyword;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView mapRecyclerView;
    private MapAdapter mapAdapter;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    //用来存放附近地点数据
    private List<AddressBean> addressList = new ArrayList<>();
    //用于备份原始数据列表
    private List<AddressBean> originalList = new ArrayList<>();

    public static SightsFragment newInstance(String content) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_CONTENT, content);
        SightsFragment sightsFragment = new SightsFragment();
        sightsFragment.setArguments(arguments);
        return sightsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_tab, container, false);
        //获取组件
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        mapRecyclerView = (RecyclerView) view.findViewById(R.id.mapRecyclerView);
        //获取地图列表标识
        surroundings = getArguments().getString(EXTRA_CONTENT);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                } else {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
        //初始化 RecyclerView
        mapRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mapAdapter = new MapAdapter(getActivity());
        mapAdapter.setData(addressList);
        mapRecyclerView.setAdapter(mapAdapter);
        mapRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int first = lm.findFirstVisibleItemPosition();
                if (!isLoading && !isLastPage && (visible + first) >= total && first >= 0) {
                    // 到达底部，加载下一页
                    pageNum++;
                    loadPage(pageNum);
                }
            }
        });
        //实现周边信息项点击响应
        mapAdapter.setOnItemClickListener(new MapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, String content) {
                Toast.makeText(getActivity(), "您点击了: " + name, Toast.LENGTH_SHORT).show();
            }
        });
        //初始化定位功能
        initLocationClient();
        return view;
    }

    private void initLocationClient() {
        try {
            //创建定位客户端对象
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //高精度定位
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            // 单次定位
            mLocationOption.setOnceLocation(true);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null || aMapLocation.getErrorCode() != 0) {
            Toast.makeText(getActivity(), "定位失败，无法获取您当前位置：", Toast.LENGTH_SHORT).show();
            return;
        }
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
        //缓存当前坐标
        currLat = aMapLocation.getLatitude();
        currLon = aMapLocation.getLongitude();
        //Toast.makeText(getActivity(), "定位成功，当前位置纬度" + latitude + "经度" + longitude, Toast.LENGTH_SHORT).show();
        switch (surroundings) {
            case "当前周边":
                searchKeyword = "";
                //用当前位置作为搜索中心
                searchLat = currLat;
                searchLon = currLon;
                break;
            case "景区景点":
                searchKeyword = "景点";
                //用东湖中心作为搜索中心
                searchLat = EAST_LAKE_CENTER.getLatitude();
                searchLon = EAST_LAKE_CENTER.getLongitude();
                break;
            case "景区餐饮":
                searchKeyword = "餐饮";
                searchLat = EAST_LAKE_CENTER.getLatitude();
                searchLon = EAST_LAKE_CENTER.getLongitude();
                break;
            case "景区酒店":
                searchKeyword = "酒店";
                searchLat = EAST_LAKE_CENTER.getLatitude();
                searchLon = EAST_LAKE_CENTER.getLongitude();
                break;
        }
        //重置分页状态
        pageNum = 0;
        isLastPage = false;
        addressList.clear();
        mapAdapter.notifyDataSetChanged();
        //加载第一页
        loadPage(pageNum);
    }

    //在给定坐标周边按关键词搜索POI，并更新列表
    private void loadPage(int page) {
        if (isLoading || isLastPage)
            return;
        isLoading = true;
        PoiSearch.Query query = new PoiSearch.Query(searchKeyword, "", "");
        query.setPageSize(PAGE_SIZE);
        query.setPageNum(page);
        //设置搜索边界：中心点为当前位置，半径10公里
        LatLonPoint center = new LatLonPoint(searchLat, searchLon);
        PoiSearch.SearchBound bound = new PoiSearch.SearchBound(center, 10000);
        try {
            PoiSearch poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setBound(bound);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        } catch (AMapException e) {
            isLoading = false;
            e.printStackTrace();
            Toast.makeText(getActivity(), "搜索失败：" + e.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //POI搜索成功回调
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        isLoading = false;
        //解析result获取POI信息
        if (rCode == 1000 && result != null) {
            //获取POI组数列表
            List<PoiItem> items = result.getPois();
            if (pageNum == 0) {
                originalList.clear();
                if (items.isEmpty()) {
                    Toast.makeText(getActivity(), "未找到任何结果", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            List<AddressBean> pageBeans = new ArrayList<>();
            //添加新数据
            for (PoiItem it : items) {
                LatLonPoint p = it.getLatLonPoint();
                pageBeans.add(new AddressBean(p.getLongitude(), p.getLatitude(), it.getTitle(), it.getSnippet()));
            }
            if (pageNum == 0) {
                //第一页：重置并同步originalList
                originalList.addAll(pageBeans);
                addressList.clear();
                addressList.addAll(pageBeans);
            } else {
                //后续页：只追加 addressList
                addressList.addAll(pageBeans);
            }
            mapAdapter.notifyDataSetChanged();
            // 判断是否最后一页
            int totalPage = result.getPageCount();
            if (pageNum >= totalPage - 1) {
                isLastPage = true;
            }
        } else {
            Toast.makeText(getActivity(), "搜索失败，错误码：" + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    //实现查询功能
    public void doSearch(String keyword) {
        searchKeyword = keyword;
        // 根据标签决定搜索中心
        if ("当前周边".equals(surroundings)) {
            searchLat = currLat;  searchLon = currLon;
        } else {
            searchLat = EAST_LAKE_CENTER.getLatitude();
            searchLon = EAST_LAKE_CENTER.getLongitude();
        }
        // 重置分页状态
        pageNum = 0;
        isLastPage = false;
        addressList.clear();
        originalList.clear();
        mapAdapter.notifyDataSetChanged();
        loadPage(0);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {
        //此回调一般用于单个POI查询，此处无需处理
    }
}
