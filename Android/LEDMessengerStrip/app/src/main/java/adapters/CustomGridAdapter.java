package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.kalandlabor.ledmessengerstrip.MainActivity;
import com.kalandlabor.ledmessengerstrip.R;

public class CustomGridAdapter extends BaseAdapter {

    MainActivity mainActivity;
    String[] items;
    LayoutInflater inflater;

    public CustomGridAdapter(MainActivity mainActivity, String[] items) {
        this.mainActivity = mainActivity;
        this.items = items;
        inflater = (LayoutInflater) this.mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cell, null);
        }
        Button button = (Button) convertView.findViewById(R.id.grid_item);

        button.setText(items[position]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.itemClicked(position);
            }
        });

        return convertView;
    }

}
