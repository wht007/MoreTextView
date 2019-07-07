## MoreTextView
可展开和收缩的TextView，列表同样适用  
###### 属性
```
<!--能显示的最大行数-->
<attr name="maxUnFoldLines" format="integer" />
<!--动画执行时间-->
<attr name="animDuration" format="integer" />
<!--内容文字颜色-->
<attr name="contentTextColor" format="color" />
<!--内容文字大小-->
<attr name="contentTextSize" format="dimension" />
<!--收起图片-->
<attr name="foldImage" format="reference" />
<!--收起的文字描述-->
<attr name="foldValue" format="string" />
<!--展开图片-->
<attr name="unFoldImage" format="reference" />
<!--展开文字描述-->
<attr name="unFoldValue" format="string" />
<!--收起/展开文字大小-->
<attr name="expandCollapsTextSize" format="dimension" />
<!--收起/展开文字颜色-->
<attr name="expandCollapsTextColor" format="color" />
<!--收起/展开文字居左/居右-->
<attr name="expandCollapsTextGravity">
    <flag name="left" value="3" />
    <flag name="right" value="5" />
</attr>
<!--上下箭头图片居左/居右-->
<attr name="imageGrarity">
    <flag name="left" value="3" />
    <flag name="right" value="5" />
</attr>	
```  
###### 布局文件
```
<cn.wht.moretextview.MoreTextView
android:id="@+id/item_txt"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:padding="8dp"
app:animDuration="1000"
app:expandCollapsTextColor="@color/colorAccent"
app:contentTextColor="@color/colorPrimaryDark"
app:contentTextSize="14sp"
app:expandCollapsTextGravity="right"
app:foldImage="@mipmap/icon_up"
app:foldValue="收起"
app:imageGrarity="left"
app:layout_constraintTop_toTopOf="parent"
app:maxUnFoldLines="5"
app:unFoldImage="@mipmap/icon_down"
app:unFoldValue="展开" />
```
###### 用法
- 普通使用 expandableTextView.setText("content");
- 列表使用 expandableTextView.setText("content"，item的下标);

