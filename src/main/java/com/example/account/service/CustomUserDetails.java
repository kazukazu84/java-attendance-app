package com.example.account.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.account.entity.UserInfo;

public class CustomUserDetails implements UserDetails {
    private final UserInfo userInfo;

    public CustomUserDetails(UserInfo userInfo) { this.userInfo = userInfo; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // userInfoのgetPosition()を呼ぶのがポイントです
        String roleName = "ROLE_" + userInfo.getPosition().name();
        return AuthorityUtils.createAuthorityList(roleName);
    }
    @Override
    public String getPassword() { return userInfo.getPassword(); }

    @Override
    public String getUsername() { return userInfo.getUserId(); }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        // 在籍（1）の時だけ true を返す。
        // これだけで、休職（2）や退職（3）のユーザーは門前払い（認証NG）になります！
        return userInfo.getIsActive() == 1;
    }
}