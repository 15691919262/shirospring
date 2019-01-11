package com.sunny.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import javax.servlet.ServletRequest;
import java.io.Serializable;

public class MySessionManager extends DefaultWebSessionManager {

    //重写retrieveSession方法，将session存放进request中，避免每次都从Redis中获取
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Session session = null;

        Serializable sessionId = getSessionId(sessionKey);//调用父类方法，获取到sessionId
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey){
            request = ((WebSessionKey) sessionKey).getServletRequest();
            if (request!=null && sessionId!=null){
                Object sessionObject = request.getAttribute(sessionId.toString());
                if (sessionObject!=null){
                    session = (Session)sessionObject;
                    return session;
                }
            }
        }
        session = super.retrieveSession(sessionKey);//调用父类方法，从Redis中获取session
        if (request!=null && sessionId!=null){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
