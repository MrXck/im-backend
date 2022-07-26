package com.im.netty;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.im.entity.Message;
import com.im.entity.User;
import com.im.mapper.MessageMapper;
import com.im.mapper.UserMapper;
import com.im.service.AddressBookService;
import com.im.service.HistoricalChatService;
import com.im.service.UserService;
import com.im.service.impl.AddressBookServiceImpl;
import com.im.service.impl.HistoricalChatServiceImpl;
import com.im.utils.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    private static ChannelGroup clients =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<Integer, Channel> USER_CHANNEL = new ConcurrentHashMap<>();
    private static Map<Channel, Integer> CHANNEL_USER = new ConcurrentHashMap<>();


    //客户端创建的时候触发，当客户端连接上服务端之后，就可以获取该channel，然后放到channelGroup中进行统一管理
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    //客户端销毁的时候触发，
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //客户端传递过来的消息
        String content = msg.text();
        Map map = JSON.parseObject(content, HashMap.class);
        System.out.println(map);
        Message message = JSON.parseObject(content, Message.class);
        Integer type = message.getType();
        Integer id = message.getId();
        message.setTime(LocalDateTime.now());
        Channel channel = ctx.channel();
        if(type != null){
            if(type == 1 && id != null){
                USER_CHANNEL.put(id, channel);
                CHANNEL_USER.put(channel, id);
            }else if(type == 5){
                UserMapper userMapper = SpringUtils.getBean(UserMapper.class);
                User user = userMapper.selectById(message.getFromId());
                map.put("avatar", user.getAvatar());
                System.out.println(map);
                for(Channel channel1 : clients){
                    if(channel1.equals(channel)){
                        continue;
                    }
                    channel1.writeAndFlush(
                            new TextWebSocketFrame(JSON.toJSONString(map)));
                }
            }else if(type == 2){
                MessageMapper messageMapper = SpringUtils.getBean(MessageMapper.class);
                AddressBookService addressBookService = SpringUtils.getBean(AddressBookServiceImpl.class);
                HistoricalChatService historicalChatService = SpringUtils.getBean(HistoricalChatServiceImpl.class);
                UserService userService = SpringUtils.getBean(UserService.class);
                String token = (String) map.get("Authorization");
                if(!StringUtils.isEmpty(token)){
                    Integer userId = userService.checkToken(token);
                    if(null != userId){
                        Boolean isFriend = addressBookService.isFriend(message.getToId(), userId);
                        if(isFriend){
                            message.setFromId(userId);
                            messageMapper.insert(message);
                            historicalChatService.add(message.getToId(), userId);
                            historicalChatService.add(userId, message.getToId());
                            Channel channel1 = USER_CHANNEL.get(message.getToId());
                            if(channel1 != null){
                                channel1.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                            }
                        }else {
                            Channel channel1 = USER_CHANNEL.get(userId);
                            if(channel1 != null){
                                Map<String, Object> map1 = new HashMap<>();
                                map1.put("type", 6);
                                map1.put("message", "你们不是好友无法发送消息");
                                channel1.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(map1)));
                            }
                        }
                    }
                }
            }
        }
    }
}

