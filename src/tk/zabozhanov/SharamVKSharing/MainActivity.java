package tk.zabozhanov.SharamVKSharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tk.zabozhanov.SharamVKSharing.response.AudioItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis Zabozhanov on 28/02/14.
 */
public class MainActivity extends VKActivity implements ListViewAdapter.ListViewAdapterListener, App.AuthorizeListener {

    private ListView mListView;
    private EditText mTxtSearch;

    protected List<AudioItem> items = new ArrayList<AudioItem>();
    private ListViewAdapter mAdapter;
    private String search;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

	    Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
		    if ("text/plain".equals(type)) {
			    handleSendText(intent);
		    }
	    }

        mListView = (ListView) findViewById(R.id.listview);
        mTxtSearch = (EditText) findViewById(R.id.txtSearchString);
        Button searchButton = (Button) findViewById(R.id.btnSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = mTxtSearch.getText().toString();
                requestList();
            }
        });
        mAdapter = new ListViewAdapter(this, items);
        mAdapter.listener = this;
        mListView.setAdapter(mAdapter);
        mTxtSearch.setText(search);

	    App.getInstance().mListener = this;
    }

	void handleSendText(Intent intent) {
		this.search = intent.getStringExtra(Intent.EXTRA_SUBJECT);
		if (this.search == null) {
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (VKSdk.getAccessToken() == null) {
			VKSdk.authorize(App.sMyScope, true, true);
		} else {
			requestList();
		}
	}

	protected void requestList() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("q", search);
        params.put("auto_complete", 1);
        params.put("sort", 2);
        VKRequest vkRequest = new VKRequest("audio.search", new VKParameters(params));
        vkRequest.executeWithListener(mListener);
    }

    protected VKRequest.VKRequestListener mListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);

            try {
                JSONObject jsonObject = response.json;
                JSONArray array = jsonObject.getJSONObject("response").getJSONArray("items");
                if (array.length() > 0) {
                    items.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        int audioId = object.getInt("id");
                        int owner_id = object.getInt("owner_id");
                        String artist = object.getString("artist");
                        String title = object.getString("title");

                        AudioItem item = new AudioItem(audioId, owner_id, artist, title);
                        items.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Не найдено", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException ex) {

            }
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
	        Toast.makeText(MainActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.logout) {
			App.getInstance().logout();
			//finish();
		}
		return super.onOptionsItemSelected(item);
	}

	protected VKRequest.VKRequestListener mAddListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            try {
                if (response.json.getString("response") != null) {

                    success();
                } else {
                    failure();
                }
            } catch (JSONException ex) {
                failure();
            }
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
            failure();
        }
    };

    protected void success() {
        Toast.makeText(MainActivity.this, "Успешно добавлено", Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void failure() {
        Toast.makeText(MainActivity.this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addItem(AudioItem item) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("audio_id", item.id);
        params.put("owner_id", item.ownerId);
        VKRequest addRequest = new VKRequest("audio.add", new VKParameters(params));
        addRequest.executeWithListener(mAddListener);
    }

	@Override
	public void doSearch() {
		requestList();
	}
}