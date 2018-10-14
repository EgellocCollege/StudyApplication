package com.sxt.chat.ws;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sxt.chat.R;
import com.sxt.chat.db.User;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.RoomInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public final class BmobRequest {

    private Context context;
    private static BmobRequest instance;
    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create();

    private BmobRequest(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized BmobRequest getInstance(Context context) {
        if (instance == null) {
            instance = new BmobRequest(context);
        }
        return instance;
    }

    /**
     * 注册
     */
    public void register(String userName, String passwd, final String cmd) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwd);
        user.setUserName(userName);
        user.setName(userName);
        user.setUserPwd(passwd);
        user.setPhone(userName);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {//注册成功
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.OK);
                    resp.setCmd(cmd);
                    resp.setUser(user);
                    EventBus.getDefault().post(resp);
                } else {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.ERROR);
                    resp.setCmd(cmd);
                    resp.setError("errorCode = " + e.getErrorCode() + "\r\n message : " + e.getMessage());
                    EventBus.getDefault().post(resp);
                }
            }
        });
    }

    /**
     * 登录
     */
    public void login(String userName, String passwd, final String cmd) {
        BmobUser.loginByAccount(userName, passwd, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.OK);
                    resp.setCmd(cmd);
                    resp.setUser(user);
                    EventBus.getDefault().post(resp);
                } else {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.ERROR);
                    resp.setCmd(cmd);
                    resp.setError("errorCode = " + e.getErrorCode() + "\r\n message : " + e.getMessage());
                    EventBus.getDefault().post(resp);
                }
            }
        });
    }

    /**
     * 更新本地User信息
     */
    public void updateUserInfo(final String cmd) {
        //更新本地用户信息
        //注意：需要先登录，否则会报9024错误
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.OK);
                    resp.setCmd(cmd);
                    EventBus.getDefault().post(resp);
                } else {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.ERROR);
                    resp.setCmd(cmd);
                    resp.setError("errorCode = " + e.getErrorCode() + "\r\n message : " + e.getMessage());
                    EventBus.getDefault().post(resp);
                }
            }
        });
    }

    /**
     * 获取Banner
     */
    public void getBanner(int newLimit, int newSkip, final String cmd) {
        BmobQuery<Banner> query = new BmobQuery<>();
        query.setLimit(newLimit);
        query.setSkip(newSkip);
        query.findObjects(new FindListener<Banner>() {
            @Override
            public void done(List<Banner> list, BmobException e) {
                if (e == null) {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.OK);
                    resp.setCmd(cmd);
                    resp.setBannerInfos(list);
                    EventBus.getDefault().post(resp);
                } else {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.ERROR);
                    resp.setCmd(cmd);
                    resp.setError("errorCode = " + e.getErrorCode() + "\r\n message : " + e.getMessage());
                    EventBus.getDefault().post(resp);
                }
            }
        });
    }


    /**
     * 获取房间
     */
    public void getRoomList(int newLimit, int newSkip, final String cmd) {
        BmobQuery<RoomInfo> query = new BmobQuery<>();
        query.setLimit(newLimit);
        query.setSkip(newSkip);
        query.findObjects(new FindListener<RoomInfo>() {
            @Override
            public void done(List<RoomInfo> list, BmobException e) {
                if (e == null) {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.OK);
                    resp.setCmd(cmd);
                    resp.setRoomInfoList(list);
                    EventBus.getDefault().post(resp);
                } else {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.ERROR);
                    resp.setCmd(cmd);
                    resp.setError("errorCode = " + e.getErrorCode() + "\r\n message : " + e.getMessage());
                    EventBus.getDefault().post(resp);
                }
            }
        });
    }

    /**
     * 上传头像
     */
    public void uploadFile(String filePath, final String cmd) {

        final BmobFile bmobFile = new BmobFile(new File(filePath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.OK);
                    resp.setCmd(cmd);
                    resp.setImgUrl(bmobFile.getUrl());//bmobFile.getFileUrl()--返回的上传文件的完整地址
                    EventBus.getDefault().post(resp);
                } else {
                    ResponseInfo resp = new ResponseInfo(ResponseInfo.ERROR);
                    resp.setCmd(cmd);
                    resp.setError(context.getString(R.string.upload_img_failed));
                    EventBus.getDefault().post(resp);
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

    public void onDestroy() {
        instance = null;
    }

}
