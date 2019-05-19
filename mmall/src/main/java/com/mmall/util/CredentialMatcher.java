package com.mmall.util;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * @Auther: Administrator
 * @Date: 2019/5/18 20:54
 * @Description:
 */
public class CredentialMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo info) {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String password = new String(token.getPassword()) ;
        String dbPassword = info.getCredentials().toString();
        if(this.equals(MD5Util.MD5EncodeUtf8(password),dbPassword)){
            return true;
        }
        return false;
    }
}