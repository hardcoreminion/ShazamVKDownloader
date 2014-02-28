package tk.zabozhanov.SharamVKSharing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import tk.zabozhanov.SharamVKSharing.response.AudioItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis Zabozhanov on 28/02/14.
 */
public class ListViewAdapter extends ArrayAdapter<AudioItem> {

    public ListViewAdapterListener listener;

    public ListViewAdapter(Context context, List<AudioItem> items) {
        super(context, R.layout.audio_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.audio_item, null);
        }

        final AudioItem item = getItem(position);
        TextView txtAudio = (TextView) convertView.findViewById(R.id.txtAudio);
        txtAudio.setText(item.artist + " - " + item.title);
        Button btnAdd = (Button) convertView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.addItem(item);
                }
            }
        });

        return convertView;
    }

    public interface ListViewAdapterListener {
        public void addItem(AudioItem item);
    }
}
