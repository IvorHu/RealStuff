package com.example.ivor_hu.meizhi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivor_hu.meizhi.databinding.ViewerPagerLayoutBinding;
import com.example.ivor_hu.meizhi.db.entity.Image;
import com.example.ivor_hu.meizhi.ui.fragment.GirlsFragment;
import com.example.ivor_hu.meizhi.ui.fragment.ViewerFragment;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.PicUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ivor on 2016/2/15.
 */
public class ViewerActivity extends AppCompatActivity {
    public static final String TAG = "ViewerActivity";
    public static final String INDEX = "index";
    private static final int SAVE_IMG_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 111;
    private static final int SHARE_IMG_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    private static final String MSG_URL = "msg_url";
    private static final String SHARE_TITLE = "share_title";
    private static final String SHARE_TEXT = "share_text";
    private static final String SHARE_URL = "share_url";
    private static String mSavedImgUrl;
    private final Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case PicUtil.SAVE_DONE_TOAST:
                    String filepath = msg.getData().getString(PicUtil.FILEPATH);
                    CommonUtil.toast(ViewerActivity.this, getString(R.string.pic_saved) + filepath, Toast.LENGTH_LONG);
                    break;
                default:
                    break;
            }
        }
    };
    private List<Image> mImages;
    private int mPos;
    private FragmentStatePagerAdapter mAdapter;
    private HandlerThread mThread;
    private Handler mSavePicHandler;
    private Handler mShareHandler;
    private ViewerPagerLayoutBinding mBinding;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        mBinding = DataBindingUtil.setContentView(this, R.layout.viewer_pager_layout);

        mPos = getIntent().getIntExtra(GirlsFragment.POSTION, 0);
        mImages = getIntent().getParcelableArrayListExtra(GirlsFragment.IMAGES);
        mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ViewerFragment.newInstance(
                        mImages.get(position).getUrl(),
                        position == mPos);
            }

            @Override
            public int getCount() {
                return mImages.size();
            }
        };
        mBinding.viewerPager.setAdapter(mAdapter);
        mBinding.viewerPager.setCurrentItem(mPos);

        // 避免图片在进行 Shared Element Transition 时盖过 Toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementsUseOverlay(false);
        }

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                Image image = mImages.get(mBinding.viewerPager.getCurrentItem());
                sharedElements.clear();
                sharedElements.put(image.getUrl(), ((ViewerFragment) mAdapter.instantiateItem(mBinding.viewerPager, mBinding.viewerPager.getCurrentItem())).getSharedElement());
            }
        });

        mThread = new HandlerThread("save-and-share");
        mThread.start();
        mSavePicHandler = new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final String url = msg.getData().getString(MSG_URL);
                try {
                    PicUtil.saveBitmapFromUrl(ViewerActivity.this, url, mMsgHandler);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        mShareHandler = new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final String title = msg.getData().getString(SHARE_TITLE);
                final String text = msg.getData().getString(SHARE_TEXT);
                final String url = msg.getData().getString(SHARE_URL);
                shareMsg(title, text, url);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMsgHandler.removeMessages(PicUtil.SAVE_DONE_TOAST);
        mThread.quit();
    }

    @Override
    public void supportFinishAfterTransition() {
        Intent data = new Intent();
        data.putExtra(INDEX, mBinding.viewerPager.getCurrentItem());
        setResult(RESULT_OK, data);

        super.supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    public void saveImg(String url) {
        if (url == null) {
            return;
        }

        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString(MSG_URL, url);
        message.setData(bundle);
        mSavePicHandler.sendMessage(message);
    }

    public void shareImg(String url) {
        if (url == null) {
            return;
        }

        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString(SHARE_TITLE, getString(R.string.share_msg));
        bundle.putString(SHARE_TEXT, null);
        bundle.putString(SHARE_URL, url);
        message.setData(bundle);
        mShareHandler.sendMessage(message);
    }

    public void shareMsg(String msgTitle, String msgText, String url) {
        String imgPath = PicUtil.getImgPathFromUrl(url);

        Intent intent = new Intent(Intent.ACTION_SEND);
        if (TextUtils.isEmpty(imgPath)) {
            intent.setType("text/plain");
        } else {
            File file = new File(imgPath);
            if (!file.exists()) {
                try {
                    PicUtil.saveBitmapFromUrl(ViewerActivity.this, url, mMsgHandler);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (file.exists() && file.isFile()) {
                intent.setType("image/jpg");
                Uri uri = Uri.fromFile(file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            CommonUtil.toast(ViewerActivity.this, getString(R.string.save_img_failed_without_permission), Toast.LENGTH_SHORT);
            return;
        }
        if (requestCode == SAVE_IMG_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            saveImg(mSavedImgUrl);
        } else if (requestCode == SHARE_IMG_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            shareImg(mSavedImgUrl);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showImgOptDialog(String url) {
        ImageOptionDialog.newInstance(url).show(getSupportFragmentManager(), TAG);
    }

    public static class ImageOptionDialog extends DialogFragment {
        private static final String OPT_URL = "option_url";
        private String mUrl;

        public static ImageOptionDialog newInstance(String url) {
            Bundle args = new Bundle();
            args.putString(OPT_URL, url);

            ImageOptionDialog fragment = new ImageOptionDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUrl = getArguments().getString(OPT_URL);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = inflater.inflate(R.layout.dialog_image_option, container);
            TextView saveTextView = view.findViewById(R.id.save_img);
            saveTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSavedImgUrl = mUrl;
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                SAVE_IMG_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    } else {
                        ((ViewerActivity) getActivity()).saveImg(mUrl);
                    }
                    dismiss();
                }
            });
            TextView shareTextView = view.findViewById(R.id.share_img);
            shareTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSavedImgUrl = mUrl;
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                SHARE_IMG_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    } else {
                        ((ViewerActivity) getActivity()).shareImg(mUrl);
                    }
                    dismiss();
                }
            });

            return view;
        }
    }
}
