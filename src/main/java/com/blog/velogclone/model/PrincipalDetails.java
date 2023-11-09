package com.blog.velogclone.model;

import com.blog.velogclone.entity.User;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
@ToString
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes;

    // 일반 로그인 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // oAuth 로그인 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // OAuth2User 인터페이스 메소드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // UserDetails 인터페이스 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList();

        /*
         * 회원정보에는 role 번호가 들어있기 때문에 roleName을 가져오려면 여기서
         * user.getRoleNo로 roleName을 조회하여 roleName 변수에 넣어주자.
         */
        String roleName = "";

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return roleName;
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getUserPw();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        boolean status;

        // 계정 활성화 여부를 넣어주자
        if("N".equals(user.getUserStatus())) {
            status = true;
        } else {
            status = false;
        }

        return status;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public Long getUserNo() {
        return user.getUserNo();
    }
}
