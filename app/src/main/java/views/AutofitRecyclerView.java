package views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AutofitRecyclerView extends RecyclerView {

    public static final int DEFAULT_COLUMN_WIDTH = -1;
    private GridLayoutManager layoutManager;
    private int columnWidth = -1;

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArr = {android.R.attr.columnWidth};
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArr);
            int columnWidth = array.getDimensionPixelSize(0, DEFAULT_COLUMN_WIDTH);
            if (columnWidth != DEFAULT_COLUMN_WIDTH) {
                this.columnWidth = columnWidth;
            }
            array.recycle();
        }

        layoutManager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(layoutManager);
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            layoutManager.setSpanCount(spanCount);
        }
    }
}
