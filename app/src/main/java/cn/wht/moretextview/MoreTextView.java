
package cn.wht.moretextview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 *
 */
public class MoreTextView extends LinearLayout implements View.OnClickListener {

    /**
     * 默认显示行数
     */
    private static final int MAX_COLLAPSED_LINES = 5;

    /**
     * 默认动画执行时间
     */
    private static final int DEFAULT_ANIM_DURATION = 200;

    /**
     * 显示状态（默认收起）
     */
    private boolean isFold = true;
    /**
     * 是否有重新绘制
     */
    private boolean isChange = false;
    /**
     * 加载内容的TextView
     */
    protected TextView mContentText;

    /**
     * 展开收起TextView
     */
    protected TextView mFoldTxt;

    /***
     * 展开图片
     */
    private Drawable mFoldImage;
    /**
     * 收起图片
     */
    private Drawable mUnFoldImage;
    /**
     * 动画执行时间
     */
    private int mAnimationDuration;
    /**
     * 是否正在执行动画
     */
    private boolean mAnimating;
    /**
     * 展开收起状态回调
     */
    private OnFoldStateChangeListener mListener;
    /**
     * 列表情况下保存每个item的收起/展开状态
     */
    private SparseBooleanArray mCollapsedStatus;
    /**
     * view在列表中的位置
     */
    private int mPosition;

    /**
     * 设置内容最大行数，超过隐藏
     */
    private int mMaxFoldLines;

    /**
     * 收起时linerlayout容器的高度
     */
    private int mCollapsedHeight;

    /**
     * 完整显示内容时的真实高度（含padding）
     */
    private int mTextHeightWithMaxLines;

    /**
     * 内容tvMarginTopAmndBottom高度
     */
    private int mMarginBetweenTxtAndBottom;

    /**
     * 内容颜色
     */
    private int contentTextColor;
    /**
     * 收起展开颜色
     */
    private int expandCollapsTextColor;
    /**
     * 内容字体大小
     */
    private float contentTextSize;
    /**
     * 收起展字体大小
     */
    private float expandCollapsTextSize;
    /**
     * 收起文字
     */
    private String textFold;
    /**
     * 展开文字
     */
    private String textUnFold;

    /**
     * 收起展开位置，默认左边
     */
    private int grarity;

    /**
     * 收起展开图标位置，默认在右边
     */
    private int imageGrarity;

    public MoreTextView(Context context) {
        this(context, null);
    }

    public MoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * 初始化属性
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        mCollapsedStatus = new SparseBooleanArray();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MoreTextView);
        mMaxFoldLines = typedArray.getInt(R.styleable.MoreTextView_maxUnFoldLines, MAX_COLLAPSED_LINES);
        mAnimationDuration = typedArray.getInt(R.styleable.MoreTextView_animDuration, DEFAULT_ANIM_DURATION);
        mUnFoldImage = typedArray.getDrawable(R.styleable.MoreTextView_unFoldImage);
        mFoldImage = typedArray.getDrawable(R.styleable.MoreTextView_foldImage);

        textFold = typedArray.getString(R.styleable.MoreTextView_foldValue);
        textUnFold = typedArray.getString(R.styleable.MoreTextView_unFoldValue);

        if (mUnFoldImage == null) {
            mUnFoldImage = ContextCompat.getDrawable(getContext(), R.mipmap.icon_up);
        }
        if (mFoldImage == null) {
            mFoldImage = ContextCompat.getDrawable(getContext(), R.mipmap.icon_down);
        }

        if (TextUtils.isEmpty(textFold)) {
            textFold = "收起";
        }
        if (TextUtils.isEmpty(textUnFold)) {
            textUnFold = "展开";
        }
        contentTextColor = typedArray.getColor(R.styleable.MoreTextView_contentTextColor, ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        contentTextSize = typedArray.getDimension(R.styleable.MoreTextView_contentTextSize, sp2px(getContext(), 14));

        expandCollapsTextColor = typedArray.getColor(R.styleable.MoreTextView_expandCollapsTextColor, ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        expandCollapsTextSize = typedArray.getDimension(R.styleable.MoreTextView_expandCollapsTextSize, sp2px(getContext(), 14));

        grarity = typedArray.getInt(R.styleable.MoreTextView_expandCollapsTextGravity, Gravity.LEFT);
        imageGrarity = typedArray.getInt(R.styleable.MoreTextView_imageGrarity, Gravity.RIGHT);
        typedArray.recycle();
        setOrientation(LinearLayout.VERTICAL);
        setVisibility(GONE);
        initViews();
    }


    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.item_expand_collapse, this);
        mContentText = (TextView) findViewById(R.id.expandable_text);
        mContentText.setOnClickListener(this);
        mFoldTxt = (TextView) findViewById(R.id.expand_collapse);
        setDrawbleAndText();
        mFoldTxt.setOnClickListener(this);

        mContentText.setTextColor(contentTextColor);
        mContentText.getPaint().setTextSize(contentTextSize);

        mFoldTxt.setTextColor(expandCollapsTextColor);
        mFoldTxt.getPaint().setTextSize(expandCollapsTextSize);

        // 设置收起展开位置：左或者右
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = grarity;
        mFoldTxt.setLayoutParams(lp);
    }

    /**
     * 点击事件
     *
     * @param view
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
            // 展开动画 getHeight 此时获取的是收起时的总高度 ->展开的最终位置((收起时的总高度+完全显示内容的总高度)-(收起时的总高度-收取展开控件的高度))
            // (收起时的总高度+完全显示内容的总高度) 多加了收起时纯文本高度。 最终位置也可以是写成（getHeight()+mTextHeightWithMaxLines-mContentText.getHeight()）
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 当动画还在执行状态时，拦截事件，不让child处理
        return mAnimating;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("onMeasure","onMeasure");
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

    /**
     * 获取内容tv真实高度（含padding）
     *
     * @param textView
     * @return
     */
    private static int getRealTextViewHeight(TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    /**
     * 设置收起展开图标和文字
     */
    private void setDrawbleAndText() {
        if (Gravity.START == imageGrarity) {
            mFoldTxt.setCompoundDrawablesWithIntrinsicBounds(isFold ? mFoldImage : mUnFoldImage, null, null, null);
        } else {
            mFoldTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, isFold ? mFoldImage : mUnFoldImage, null);
        }
        mFoldTxt.setText(isFold ? textUnFold : textFold);
    }


    /*********暴露给外部调用方法***********/

    /**
     * 设置收起/展开监听
     *
     * @param listener
     */
    public void setOnFoldStateChangeListener(OnFoldStateChangeListener listener) {
        mListener = listener;
    }

    /**
     * 设置内容
     *
     * @param text
     */
    public void setText(CharSequence text) {
        isChange = true;
        mContentText.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置内容的颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mContentText.setTextColor(color);
    }

    /**
     * 设置内容，列表情况下，带有保存位置收起/展开状态
     *
     * @param text
     * @param position
     */
    public void setText(CharSequence text, int position) {
        mPosition = position;
        // 获取收起/展开状态，如没有，默认是true:收起
        isFold = mCollapsedStatus.get(position, true);
        clearAnimation();
        // 设置收起/展开图标和文字
        setDrawbleAndText();
        mFoldTxt.setText(isFold ? textUnFold : textFold);

        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    /**
     * 获取内容
     *
     * @return
     */
    public CharSequence getText() {
        if (mContentText == null) {
            return "";
        }
        return mContentText.getText();
    }

    /**
     * 定义状态改变接口
     */
    public interface OnFoldStateChangeListener {

        void onFoldStateChangeListener(TextView textView, boolean isFold);
    }

    /**
     * 将sp值转换为px值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}