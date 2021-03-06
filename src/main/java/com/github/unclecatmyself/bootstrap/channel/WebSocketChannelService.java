package com.github.unclecatmyself.bootstrap.channel;

import com.google.gson.Gson;
import com.github.unclecatmyself.bootstrap.WsChannelService;
import com.github.unclecatmyself.bootstrap.channel.cache.WsCacheMap;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by MySelf on 2018/11/26.
 */
@Component
public class WebSocketChannelService implements WsChannelService {

    @Autowired
    WsCacheMap wsCacheMap;

    private final Gson gson;

    public WebSocketChannelService(Gson gson){
        this.gson = gson;
    }

    @Override
    public void loginWsSuccess(Channel channel, String token) {
        wsCacheMap.saveWs(token,channel);
        wsCacheMap.saveAd(channel.remoteAddress().toString(),token);
    }

    @Override
    public boolean hasOther(String otherOne) {
        return wsCacheMap.hasToken(otherOne);
    }

    @Override
    public Channel getChannel(String otherOne) {
        return wsCacheMap.getByToken(otherOne);
    }

    @Override
    public void close(Channel channel) {
        String token = wsCacheMap.getByAddress(channel.remoteAddress().toString());
        wsCacheMap.deleteAd(channel.remoteAddress().toString());
        wsCacheMap.deleteWs(token);
        channel.close();
    }

    @Override
    public boolean sendFromServer(Channel channel, Map<String, String> map) {
        try {
            channel.writeAndFlush(new TextWebSocketFrame(gson.toJson(map)));
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
