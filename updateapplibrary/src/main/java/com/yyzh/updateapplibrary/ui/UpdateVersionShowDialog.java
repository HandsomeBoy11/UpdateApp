package com.yyzh.updateapplibrary.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import com.yyzh.updateapplibrary.AppUpdater;
import com.yyzh.updateapplibrary.R;
import com.yyzh.updateapplibrary.bean.DownloadBean;
import com.yyzh.updateapplibrary.net.INetDownloadCallBack;
import com.yyzh.updateapplibrary.utils.AppUtils;

import java.io.File;

public class UpdateVersionShowDialog extends DialogFragment {
    private static final String KEY_DOANLOW_BEAN = "download_bean";

    private DownloadBean mDownLoadBean;

    private TextView tvUpdate;

    public static void show(FragmentActivity activity, DownloadBean bean){
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DOANLOW_BEAN, bean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "updateVersionShowDialog");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownLoadBean = (DownloadBean) getArguments().getSerializable(KEY_DOANLOW_BEAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_updater, container, false);
        bindEvents(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void bindEvents(View view){
       TextView tvTitle = view.findViewById(R.id.tv_title);
       TextView tvContent = view.findViewById(R.id.tv_content);
       tvUpdate = view.findViewById(R.id.tv_update);

       tvTitle.setText(mDownLoadBean.title);
       tvContent.setText(mDownLoadBean.content);

       tvUpdate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               tvUpdate.setEnabled(false);
               startDownload(mDownLoadBean.url);
           }
       });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUpdater.getInstance().getNetManager().cancel(UpdateVersionShowDialog.this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        AppUpdater.getInstance().getNetManager().cancel(UpdateVersionShowDialog.this);
    }

    /**
     * 开始下载
     * @param url
     */
    public void startDownload(String url){
        final File targetFile = new File(getActivity().getCacheDir(), "target.apk");
        AppUpdater.getInstance().getNetManager().download(url, targetFile, new INetDownloadCallBack() {
            @Override
            public void success(File apkFile) {
                // 安装app
                Log.d("download" , "success");
                tvUpdate.setEnabled(true);
                dismiss();
                String fileMd5 = AppUtils.getFileMd5(targetFile);
//                Log.d("startDownload", "md5 = " + fileMd5);
//                if(fileMd5 != null && fileMd5.equals(mDownLoadBean.md5)){
                    AppUtils.installApk(getActivity(), apkFile);
//                } else {
//                    Toast.makeText(getContext(),"Md5检测失败", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void progress(int progress) {
                Log.d("download" , "progress " + progress);
                tvUpdate.setText(progress + "%");
            }

            @Override
            public void failed(Throwable throwable) {
                tvUpdate.setEnabled(true);
                Log.d("download" , "falied " + throwable.getMessage());
            }
        },UpdateVersionShowDialog.this);
    }
}
