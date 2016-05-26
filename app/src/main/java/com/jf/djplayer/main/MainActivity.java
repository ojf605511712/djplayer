package com.jf.djplayer.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;

import com.jf.djplayer.R;
import com.jf.djplayer.other.SongInfo;
import com.jf.djplayer.base.baseactivity.BaseNoTitleActivity;
import com.jf.djplayer.interfaces.FragmentChanger;
import com.jf.djplayer.interfaces.PlayControls;
import com.jf.djplayer.service.PlayerService;


import java.util.List;
import java.util.Stack;

/**
 * 主界面-外层容器
 * MainActivity
 * 做几件事
 * 动态加载MineFragment
 * 实现几个回调接口
 *
 */
public class MainActivity extends BaseNoTitleActivity
        implements FragmentChanger, PlayControls, ServiceConnection, ExitDialog.ExitDialogListener{

    private FragmentManager fragmentManager;//动态修改"Fragment"的管理器
    private PlayerService playerService;//后台控制播放用的服务
    private Stack<String> fragmentStacks;//记录每个添加到"Activity"里的"Fragment"类名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initExtrasBeforeView() {
//        通过两个方式启动服务确保解绑之后服务不会关闭
        Intent startService = new Intent(this,PlayerService.class);
        startService(startService);
        bindService(startService, this, BIND_AUTO_CREATE);

        //初始化堆栈
        fragmentStacks = new Stack<>();
    }

    @Override
    protected void initView() {
        //动态加载主页面的"Fragment"
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new MainFragment();
        //将加载的"Fragment"名字放进堆栈里面
        fragmentManager.beginTransaction().add(R.id.fl_activity_main_fragment_container,new MainFragment()).commit();
        fragmentStacks.push(fragment.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);//解绑
    }

    //监听手机返回按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //如果现在已经在主页面，显示退出的提示框
            if(fragmentStacks.peek().equals(MainFragment.class.getSimpleName())){
                new ExitDialog().show(fragmentManager, "ExitDialog");
                return true;
            }else{
                //否则，将堆栈里"Fragment"类名出栈，让堆栈和"FragmentManager"保持同步
                fragmentStacks.pop();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //    活动里的Fragment通过他来通知活动
//      添加新的Fragment
    @Override
    public void replaceFragments(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fl_activity_main_fragment_container,fragment).addToBackStack(null).commit();
        fragmentStacks.push(fragment.getClass().getSimpleName());
    }


//    活动里的Fragment通过他来通知活动
//    关闭当前Fragment
    @Override
    public void popFragments() {
        fragmentManager.popBackStack();
        fragmentStacks.pop();
    }


//    覆盖下面这些方法
//    Fragment通过这些方法控制服务
//    从而进行播放控制
    @Override
    public void play(List<SongInfo> songInfoList,int position) {
        playerService.play(songInfoList, position);
    }

    @Override
    public void play() {
        playerService.play();
    }

    @Override
    public boolean isPlaying() {
        return playerService.isPlaying();
    }

    @Override
    public void pause() {
        playerService.pause();
    }

    @Override
    public void nextSong() {
        playerService.nextSong();
    }

    @Override
    public void previousSong() {
        playerService.previousSong();
    }

    @Override
    public void stop() {

    }

//    两个用于绑定服务用的方法
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerServiceBinder)service).getPlayerService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    //"ExitDialog"里的接口方法重写
    @Override
    public void exitApp() {
        playerService.stopSelf();
        finish();
    }
}