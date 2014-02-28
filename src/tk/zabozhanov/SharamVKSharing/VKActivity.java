package tk.zabozhanov.SharamVKSharing;

import android.app.Activity;
import android.content.Intent;
import com.vk.sdk.VKUIHelper;

/**
 * Created by Denis Zabozhanov on 28/02/14.
 */
public class VKActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }
}
