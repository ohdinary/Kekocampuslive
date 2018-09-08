package com.example.dinary.kekocampuslive.search;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;

import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private String[] mStr = {"abc","bc3","ddd","aaa","aedasf","dcy","dengcaoyu"};
    private List<String> stringList = Arrays.asList(mStr);  //缓存

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPreferences = getSharedPreferences("search", MODE_PRIVATE); //默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
        editor = sharedPreferences.edit();
        if (editor != null)
            stringList = Arrays.asList(sharedPreferences.getString("stringlist",stringList.toString()));

        findAllViews();
    }

    private void findAllViews() {
        searchView = findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String s) {
//                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                MyApplication.hostId = s;
//                startActivity(intent);
                stringList.add(s);
                editor.putString("stringlist",stringList.toString());
                editor.commit();
                finish();
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String searchContent) {
                if (!TextUtils.isEmpty(searchContent))
                    listView.setFilterText(searchContent);
                else
                    listView.clearTextFilter();
                return false;
            }
        });

        listView = this.findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mStr));
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = listView.getItemAtPosition(i)+"";
                MyApplication.hostId = str;
                finish();
            }
        });
    }

}
