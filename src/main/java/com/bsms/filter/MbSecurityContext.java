package com.bsms.filter;

import com.bsms.domain.MbApiUser;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class MbSecurityContext implements SecurityContext {

	private MbApiUser user;
	private boolean isSecure;
	private String accessToken;
	
	@Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String roleName) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }

    public void setUserPrinsipal(MbApiUser user) {
        this.user = user;
    }

    public void setIsSecure(boolean isSecure) {
        this.isSecure = isSecure;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
