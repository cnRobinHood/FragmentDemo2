package com.xiezh.findlost.https;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.domain.Message;
import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.utils.Constant;
import com.xiezh.findlost.utils.DataManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xiezh on 2017/11/2.
 */

public class HttpHelper {
    private final static String TAG = "HTTPHELPER";
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static String userInfo;
    OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    public String login(String username, String password) throws Exception {
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "login")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();
            str = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

    public String queryUserinfo(String id) throws Exception {
        RequestBody body = new FormBody.Builder()
                .add("id", id)
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/queryUserinfo")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();
            str = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }


    public byte[] getImagByte(String imageName) {
        RequestBody body = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "userhead/" + imageName)
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        byte[] bytes = null;
        try {
            Response response = call.execute();
            bytes = response.body().bytes();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write(bytes);
            bytes = out.toByteArray();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public Bitmap downloadfile(String filename, String filedirectory) {
        Log.i(TAG, "进入下载" + filename);
        Bitmap bitmap = null;

        RequestBody body = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOT + "/" + filedirectory + "/" + filename)
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        byte[] bytes;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                bytes = response.body().bytes();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                /*File temp = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/findlost/temp/" + filename);
                FileOutputStream outputStream = new FileOutputStream(temp);
                outputStream.write(bytes);
                outputStream.close();*/

                out.write(bytes);

                bytes = out.toByteArray();

                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                out.close();
                Log.i(TAG, "下载成功" + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public boolean uploadItem(Map<String, Object> param) {

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getValue() instanceof File) {
                File file = (File) entry.getValue();
                if (file != null)
                    Log.i(TAG, file.getName() + "存在");//打印在上传中的文件名
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                bodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\";filename=\"" + file.getName() + "\""), fileBody);//删除了multipart/
            } else if (entry.getValue() instanceof String) {
                //RequestBody jsonBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),entry.getKey()+"="+entry.getValue().toString());
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue().toString());
                //bodyBuilder.addPart(jsonBody);
                Log.i(TAG, entry.getValue().toString());//打印上传的json字符串

            }
        }
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/upload")
                .post(bodyBuilder.build())
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (response.isSuccessful()) {
            String s = response.body().toString();
            Log.i(TAG, "上传成功");//打印上传的json字符串

            if (s.equals("success")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String queryLasted() {
        Log.i(TAG, "查询最新");//打印上传的json字符串
        Item message = null;
        if (DataManager.datas != null && DataManager.datas.size() > 0) {
            message = DataManager.datas.get(0);
            Log.i(TAG, "DataManager.datas 中有数据");
        }
        String lastedId;
        if (message == null) {
            lastedId = "-1";
        } else {
            lastedId = message.getItemID() + "";
        }
        RequestBody body = new FormBody.Builder()
                .add("lastedId", lastedId)
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/queryLasted")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                str = response.body().string();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String queryNext() {
        Log.i(TAG, "查询NextItem");//打印上传的json字符串
        Item message = null;
        if (DataManager.datas != null) {
            message = DataManager.datas.get(DataManager.datas.size() - 1);
        }
        String lessId;
        if (message == null) {
            lessId = "-1";
        } else {
            lessId = message.getItemID() + "";
        }
        RequestBody body = new FormBody.Builder()
                .add("lessId", lessId)
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/queryNext")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                str = response.body().string();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String queryMessage() {
        Log.i(TAG, "查询Message");//打印上传的json字符串

        RequestBody body = new FormBody.Builder()
                .add("fromId", DataManager.userInfo.getUserID())
                .add("type", "get")
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/message")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                str = response.body().string();
            } else {
                Log.i(TAG, "连接失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, str);
        return str;

    }


    public void sendMessage(Message message) {
        Log.i(TAG, "sendMessage");//打印上传的json字符串

        RequestBody body = new FormBody.Builder()
                .add("fromId", DataManager.userInfo.getUserID())
                .add("toId", message.getToId())
                .add("type", "put")
                .add("itemId", message.getItemId() + "")
                .add("date", message.getDate())
                .add("txt", message.getMessageList().get(0))
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/message")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                str = response.body().string();
            } else {
                Log.i(TAG, "连接失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, str);
    }

    /**
     * 更新信息
     *
     * @param userInfo
     */
    public boolean updateUserinfo(UserInfo userInfo) {

        RequestBody body = new FormBody.Builder()
                .add("userinfo", JSON.toJSONString(userInfo))
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/update")
                .post(body)
                .build();


        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                str = response.body().string();
                if ("success".equals(str)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String downloadItemById(String createByID) {
        Log.i(TAG, "查询发布的信息");//打印上传的json字符串
        RequestBody body = new FormBody.Builder()
                .add("createByID", createByID)
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/queryItemByID")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String downloadItemByKey(String key) {
        RequestBody body = new FormBody.Builder()
                .add("key", key)
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/queryItemByKey")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String remarkItem(int itemID) {
        RequestBody body = new FormBody.Builder()
                .add("itemID", itemID + "")
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/remarkItem")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String deleteItem(int itemID) {
        RequestBody body = new FormBody.Builder()
                .add("itemID", itemID + "")
                .build();

        Request request = new Request.Builder()
                .url(Constant.ROOTURL + "/deleteItem")
                .post(body)
                .build();

        Call call = client.newCall(request);
        String str = null;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
