package com.talagasoft.gojek.model;

import android.content.Context;
import android.util.Log;

import com.talagasoft.gojek.R;

/**
 * Created by compaq on 01/18/2017.
 */

public class ChatModel {
    Context _context;
    String _from,_to,_text;
    public ChatModel(Context c, String hpFrom, String hpTo) {
        this._context=c;
        this._from=hpFrom;
        this._to=hpTo;
    }

    public void send(String s) {
        if(s.isEmpty()) return;

        String mUrl=_context.getResources().getString(R.string.url_source)+"chat_push.php?from=" +
                _from + "&to=" + _to + "&msg=" + s;

        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc == null) {
            Log.d("ChatModel.send error ", s);
        } else {
            Log.d("ChatModel.send",s);
        }
    }

    public String refresh() {
        String mUrl=_context.getResources().getString(R.string.url_source)+"chat_list.php?from=" +
                _from + "&to=" + _to ;
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc == null) {
            Log.d("ChatModel.list error ", doc.toString());
        } else {
            Log.d("ChatModel.list",doc.toString());
        }
        return doc.toString();
    }

    public void clearChat() {
        String mUrl=_context.getResources().getString(R.string.url_source)+"chat_clear.php?from=" +
                _from + "&to=" + _to ;
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc == null) {
            Log.d("ChatModel.list error ", doc.toString());
        } else {
            Log.d("ChatModel.list",doc.toString());
        }
    }
}
