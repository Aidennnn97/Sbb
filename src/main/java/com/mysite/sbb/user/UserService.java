package com.mysite.sbb.user;

import com.mysite.sbb.CommonUtil;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.mail.EmailException;
import com.mysite.sbb.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final CommonUtil  commonUtil;

    public SiteUser create(String username, String email, String password){
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    @Transactional
    public void modifyPassword(String email) throws EmailException{
        String tempPassword = commonUtil.getTempPassword();
        Optional<SiteUser> siteUser = userRepository.findByEmail(email);
        if (siteUser.isPresent()){
            SiteUser user = siteUser.get();
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);
            mailService.sendSimpleMessage(email, tempPassword);
        } else {
            throw new DataNotFoundException("해당 이메일의 유저가 없습니다.");
        }
    }
}
