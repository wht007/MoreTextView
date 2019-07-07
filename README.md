![releases](https://jitpack.io/v/wht0707/MoreTextView.svg)
## MoreTextView
可展开和收缩的 TextView，列表也可以使用  
###### 使用方法
先在根目录的 build.gradle 下的 repositories 添加:
```
allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```  
然后在 app 目录下的 build.gradle 的 dependencies 添加:
```
 dependencies {
    implementation 'com.github.wht0707:MoreTextView:1.0'
    }
```


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
###### 核心逻辑
```
/**
 * 显示状态（默认收起）
 */
private boolean isFold = true;
/**
* 是否有重新绘制（在setText时设置true）
*/
 private boolean isChange = false;
    
 @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isChange || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        isChange = false;
        mFoldTxt.setVisibility(View.GONE);
        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果内容真实行数小于等于最大行数，不处理
        if (mContentText.getLineCount() <= mMaxFoldLines) {
            return;
        }
        // 完整显示内容时的真实高度
        mTextHeightWithMaxLines = getRealTextViewHeight(mContentText);

        // 如果是收起状态，重新设置最大行数
        if (isFold) {
            mContentText.setMaxLines(mMaxFoldLines);
        }
        mFoldTxt.setVisibility(View.VISIBLE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isFold) {
            // 收起时获取除文本之外的高度
            mContentText.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mContentText.getHeight();
                }
            });
            // 保存收起时的控件总高度
            mCollapsedHeight = getMeasuredHeight();
        }
    }
```
通过调用setText()方法触发onMeasure方法，然后根据设置文本的实际内容占据的行数，来决定是否显示展开/收起，以及是否记录一些相应的高度方便展开/收起动画使用
```
/**
 * 点击事件
 */
@Override
public void onClick(View view) {
    if (mFoldTxt.getVisibility() != View.VISIBLE) {
        return;
    }
    isFold = !isFold;
    // 修改收起/展开图标、文字
    setDrawbleAndText();
    // 保存位置状态
    if (mCollapsedStatus != null) {
        mCollapsedStatus.put(mPosition, isFold);
    }
    // 执行展开/收起动画
    mAnimating = true;
    ValueAnimator valueAnimator;
    if (isFold) {
        // 收缩动画， getHeight 获取的是容器总高度   mCollapsedHeight 时最大显示n行时的高度
        valueAnimator = new ValueAnimator().ofInt(getHeight(), mCollapsedHeight);
    } else {
            // 展开动画 getHeight 此时获取的是收起时的总高度 ->展开的最终位置
            ((收起时的总高度+完全显示内容的总高度)-  (收起时的总高度-收取展开控件的高度))
            // (收起时的总高度+完全显示内容的总高度) 多加了收起时纯文本高度。   
            最终位置也可以是写成（getHeight()+mTextHeightWithMaxLines-mContentText.getHeight()）
        valueAnimator = new ValueAnimator().ofInt(getHeight(), getHeight() +
                mTextHeightWithMaxLines - (getHeight() - mFoldTxt.getHeight()));
    }
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int animatedValue = (int) valueAnimator.getAnimatedValue();
            mContentText.setMaxHeight(animatedValue - mMarginBetweenTxtAndBottom);
            getLayoutParams().height = animatedValue;
            requestLayout();
            Log.d("addUpdateListener","addUpdateListener");
        }
    });
    valueAnimator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            // 动画结束后发送结束的信号
            mAnimating = false;
            if (mListener != null) {
                mListener.onFoldStateChangeListener(mContentText, !isFold);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    });
    valueAnimator.setDuration(mAnimationDuration);
    valueAnimator.start();
}
```
点击事件中有两点需要注意：  
1.`mCollapsedStatus.put(mPosition, isFold);` 此处是当在列表使用时用于记录展开收起的状态  
2.展开/收起的高度计算(代码中有详细注释)

###### 用法
- 普通使用`expandableTextView.setText("content");`
- 列表使用`expandableTextView.setText("content"，item的下标);`   
###### [github地址](https://github.com/wht0707/MoreTextView)  
###### 参考[ExpandableTextView ](https://github.com/Manabu-GT/ExpandableTextView)











