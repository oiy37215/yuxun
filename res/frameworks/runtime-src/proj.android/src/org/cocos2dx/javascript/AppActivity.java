/****************************************************************************
Copyright (c) 2008-2010 Ricardo Quesada
Copyright (c) 2010-2012 cocos2d-x.org
Copyright (c) 2011      Zynga Inc.
Copyright (c) 2013-2014 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.javascript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHelper;

import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


// The name of .so is specified in AndroidMenifest.xml. NativityActivity will load it automatically for you.
// You can use "System.loadLibrary()" to load other .so files.

public class AppActivity extends Cocos2dxActivity implements OnClickListener, SpeechSynthesizerListener{

    static String hostIPAdress = "0.0.0.0";
    static int isSpeakFinish = 0;
    private static SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
	protected static final int PRINT = 0;
	private static final String TAG = "MainActivity";
	private TextView mShowText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        if(nativeIsLandScape()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        if(nativeIsDebug()){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        hostIPAdress = getHostIpAddress();
        initialEnv();
     // 获取 tts 实例
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
     // 设置 app 上下文(必需参数)
        this.mSpeechSynthesizer.setContext(this);
     // 设置 tts 监听器
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
     // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
     // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
     // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
     // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("8016818");
     // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("Mn7ja04aulxVSFsHrytlrubp", "a2ebcc1a1798c8d8cad49bf1d3a6c170");
     // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer. PARAM_VOCODER_OPTIM_LEVEL, "2");
        this.mSpeechSynthesizer.initTts(TtsMode.MIX);
//        if (authInfo.isSuccess()) {
//        	this.mSpeechSynthesizer.speak("欢迎使用");
//        } else {
////            String errorMsg = authInfo.getTtsError().getDetailMessage();
//            this.mSpeechSynthesizer.speak("百度一下");
//        }
//        
//        this.mSpeechSynthesizer.speak("百度一下");
    }
    private Handler mHandler = new Handler() {

        /*
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case PRINT:
                    print(msg);
                    break;

                default:
                    break;
            }
        }

    };
    private void toPrint(String str) {
        Message msg = Message.obtain();
        msg.obj = str;
        this.mHandler.sendMessage(msg);
    }

    private void print(Message msg) {
        String message = (String) msg.obj;
        if (message != null) {
            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            scrollLog(message);
        }
    }
    private void scrollLog(String message) {
        Spannable colorMessage = new SpannableString(message + "\n");
        colorMessage.setSpan(new ForegroundColorSpan(0xff0000ff), 0, message.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mShowText.append(colorMessage);
        Layout layout = mShowText.getLayout();
        if (layout != null) {
            int scrollAmount = layout.getLineTop(mShowText.getLineCount()) - mShowText.getHeight();
            if (scrollAmount > 0) {
                mShowText.scrollTo(0, scrollAmount + mShowText.getCompoundPaddingBottom());
            } else {
                mShowText.scrollTo(0, 0);
            }
        }
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     * 
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);

        return glSurfaceView;
    }

    public String getHostIpAddress() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return ((ip & 0xFF) + "." + ((ip >>>= 8) & 0xFF) + "." + ((ip >>>= 8) & 0xFF) + "." + ((ip >>>= 8) & 0xFF));
    }
    
    public static String getLocalIpAddress() {
        return hostIPAdress;
    }
    
    private static native boolean nativeIsLandScape();
    private static native boolean nativeIsDebug();

	@Override
	public void onError(String arg0, SpeechError arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechFinish(String arg0) {
		// TODO Auto-generated method stub
		isSpeakFinish = 1;
	}

	@Override
	public void onSpeechProgressChanged(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechStart(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSynthesizeFinish(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSynthesizeStart(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
   
	 //speaking
    public static void speaking(String msg){
    	System.out.println("speaking");
    	mSpeechSynthesizer.speak(msg);
    	
    }
    public static void speakingHeCheng(String msg){
    	mSpeechSynthesizer.synthesize(msg);
    }
    public static void pauseSpeaking(){
    	mSpeechSynthesizer.pause();
    }
    public static void resumeSpeaking(){
    	mSpeechSynthesizer.resume();
    }
    public static int IsSpeaking(){
    	
    	return isSpeakFinish;
    }
    public static void ceshi(String str){
    	System.out.println("kkkkkkk:"+str);
    }
    public static void setSpeaking(){
    	
    	isSpeakFinish = 0;
    	System.out.println("jjjjjjjjjjj:"+isSpeakFinish);
    }
    public static void StopSpeaking(){
    	mSpeechSynthesizer.stop();
    }
    public static void setSpeakingSpeed(String i){
    	mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, i);
    	System.out.println("sddsdsdssdsds");
    }
    public static void writeText(String txtName,String context){
    	String filePath = txtName+".txt";
    	String content = context;
    	//如果filePath是传递过来的参数，可以做一个后缀名称判断； 没有指定的文件名没有后缀，则自动保存为.txt格式
    	if(!filePath.endsWith(".txt") && !filePath.endsWith(".log")) 
    		filePath += ".txt";
    	//保存文件
    	File file = new File(filePath);
    	try {
	    	    OutputStream outstream = new FileOutputStream(file);
	    	    OutputStreamWriter out = new OutputStreamWriter(outstream);
	    	    out.write(content);
	    	    out.close();
    	    } catch (java.io.IOException e) {
    	    	e.printStackTrace();
    	    }
    	
    }
    public static String readText(String txtName){
    	String path = txtName+".txt";
   
    	System.out.println("hhhhhhhhhh=:"+path);
    	String content = ""; //文件内容字符串
    	    //打开文件
    	    File file = new File(path);
    	    //如果path是传递过来的参数，可以做一个非目录的判断
    	    if (file.isDirectory()){
//    	    	Toast.makeText(EasyNote.this, "没有指定文本文件！", 1000).show();
    	    	
    	    }
    	    else{
    	    	
	    	    try {
		    	    InputStream instream = new FileInputStream(file);
		    	    if (instream != null) {
		    	    	InputStreamReader inputreader = new InputStreamReader(instream);
		    	    	BufferedReader buffreader = new BufferedReader(inputreader);
		    	    	String line;
		    	    	//分行读取
		    	    	while (( line = buffreader.readLine()) != null) {
		    	    		content += line + "\n";
		    	    	}
		    	    	instream.close();
		    	    }
	    	    } catch (java.io.FileNotFoundException e) {
	    	    	//Toast.makeText(EasyNote.this, "文件不存在", Toast.LENGTH_SHORT).show();
	    	    } catch (IOException e) {
	    	    	e.printStackTrace();
	    	    }
    	    }
    	    return content;
    }
    

}
