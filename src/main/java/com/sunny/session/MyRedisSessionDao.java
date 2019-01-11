package com.sunny.session;

import com.sunny.utils.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MyRedisSessionDao extends AbstractSessionDAO {

    @Autowired
    private JedisUtil jedisUtil;

    private final String SHIRO_SESSION_PREFIX = "shiro_session_";

    /**
     * 给键拼接上SHIRO_SESSION_PREFIX前缀
     * @param key 键
     * @return 拼接后的键
     */
    private byte[] getKey(String key){
        return (SHIRO_SESSION_PREFIX+key).getBytes();
    }

    /**
     * 保存session，新增、更新共用
     * @param session
     */
    private void saveSession(Session session){
        if (session!=null && session.getId()!=null){
            byte[] key = getKey(session.getId().toString());
            byte[] value = SerializationUtils.serialize(session);
            jedisUtil.set(key,value);
            jedisUtil.expire(key,600);
        }
    }

    /**
     * 新增session
     * @param session
     * @return sessionId
     */
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session,sessionId);//将生成sessionId与session进行绑定
        saveSession(session);
        return sessionId;
    }

    /**
     * 根据sessionId读取session
     * @param sessionId
     * @return session
     */
    protected Session doReadSession(Serializable sessionId) {
        System.out.println("从Redis中获取session");
        if(sessionId==null){
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        return (Session) SerializationUtils.deserialize(value);
    }

    /**
     * 更新session
     * @param session
     */
    public void update(Session session)  {
        if (session!=null && session.getId()!=null){
            saveSession(session);
        }
    }

    /**
     * 删除session
     * @param session
     */
    public void delete(Session session) {
        if (session!=null || session.getId()!=null){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.del(key);
    }

    /**
     * 获取所有活动的session
     * @return 所有活动的session集合
     */
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PREFIX);
        Set<Session> sessions = new HashSet<Session>();
        if (CollectionUtils.isEmpty(keys)){
            return sessions;
        }
        for(byte[] key:keys){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
