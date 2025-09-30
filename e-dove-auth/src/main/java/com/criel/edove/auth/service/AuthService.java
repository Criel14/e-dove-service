package com.criel.edove.auth.service;

import com.criel.edove.auth.dto.OtpDTO;
import com.criel.edove.auth.dto.RegisterDTO;
import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.dto.UpdateUserAuthDTO;
import com.criel.edove.auth.vo.SignInVO;

/**
 * 用户认证
 *
 * @author Criel
 * @since 2025-09-22
 */
public interface AuthService {

    SignInVO signIn(SignInDTO signInDTO);

    SignInVO register(RegisterDTO registerDTO);

    void getOtp(OtpDTO otpDTO);

    void updateUserAuth(UpdateUserAuthDTO updateUserAuthDTO);
}
