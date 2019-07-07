package cn.wht.moretextview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoreTextView moreTextView = findViewById(R.id.more_txt);
        moreTextView.setText("The Lion, The Bear And The Fox（狮子、熊与狐狸）Long ago a lion and a bear saw a kid. They sprang upon it at the same time. The lion said to the bear, “I caught this kid first, and so this is mine.No, no,\" said the bear.“I found it earlier than you, so this is mine.” And they fought long and fiercely. The Lion, The Bear And The Fox（狮子、熊与狐狸）Long ago a lion and a bear saw a kid. They sprang upon it at the same time. The lion said to the bear, “I caught this kid first, and so this is mine.No, no,\" said the bear.“I found it earlier than you, so this is mine.” And they fought long and fiercely. ");
        Button listBtn=findViewById(R.id.listBtn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }
        });
    }
}
