package cn.wht.moretextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        RecyclerView  recycleView = findViewById(R.id.recycleView);
        List<String> newData = Arrays.asList(getResources().getStringArray(R.array.moreText));
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        ListAdapter listAdapter = new ListAdapter(newData);
        recycleView.setAdapter(listAdapter);
    }

}
