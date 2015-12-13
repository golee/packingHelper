package project.hci.packinghelper;

import android.app.Activity;
import android.content.Context;
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

    public ItemBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((Activity)getContext()).getLayoutInflater().inflate(R.layout.itemblock, this);
        imageView = (ImageView)findViewById(R.id.imageViewItemBlock);
        textView = (TextView)findViewById(R.id.textViewItemBlock);
    }
    public void setText( String text ) {
        textView.setText(text);
    }
    public void setImage( int resId ) {
        imageView.setImageResource(resId);
    }
    public void setBackground( int resId) {
        imageView.setBackground(ContextCompat.getDrawable(this.getContext(), resId));
    }
    public void setColorFilter ( int colorId ) {
        imageView.setColorFilter(ContextCompat.getColor(this.getContext(), colorId));
    }
}
