package com.jf.djplayer.localmusic;

import android.widget.BaseAdapter;

import com.jf.djplayer.base.basefragment.BaseListFragment;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jf on 2016/6/15.
 * 本地音乐_ListView界面
 * 在"BaseListFragment"的基础上，添加根据标题排序方式，以及根据内容排序方式
 */
abstract public class LocalMusicListFragment extends BaseListFragment{

    protected final String title = "title";//"ListView"标题
    protected final String content = "content";//"ListView"内容

    protected void sortAccordingTitle(){
        final Collator collator = Collator.getInstance(Locale.CHINA);
        Collections.sort(dataList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> lhs, Map<String, String> rhs) {
                CollationKey key1 = collator
                        .getCollationKey(lhs.get(title));
                CollationKey key2 = collator
                        .getCollationKey(rhs.get(title));
                return key1.compareTo(key2);
            }
        });
    }

    protected void sortAccordingContent(){
        Collections.sort(dataList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> lhs, Map<String, String> rhs) {
                return lhs.get(content).compareTo(rhs.get(content));
            }
        });
    }
}
