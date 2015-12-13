package project.hci.packinghelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by JAYBEE on 2015-12-12.
 */
public class ItemBlock extends LinearLayout {
    private ImageView imageView;
    private TextView textView;

    public void setText( String text ) {
        textView.setText(text);
    }
    public String getText() {
        return textView.getText().toString();
    }
    public void setImage( int resId ) {
        imageView.setImageResource(resId);
    }
    public void setBackground( int resId) {
        imageView.setBackground(ContextCompat.getDrawable(getContext(), resId));
    }
    public void setColorFilter ( int colorId ) {
        imageView.setColorFilter(ContextCompat.getColor(getContext(), colorId), PorterDuff.Mode.SRC_ATOP);
    }

    public void setColorFilter ( String colorString ) { // colorString:#RRGGBB
        imageView.setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_ATOP);
    }

    public ItemBlock(Context context) {
        super(context);
        ((Activity)getContext()).getLayoutInflater().inflate(R.layout.itemblock, this);
        imageView = (ImageView)findViewById(R.id.imageViewItemBlock);
        textView = (TextView)findViewById(R.id.textViewItemBlock);
    }

    public ItemBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

}
