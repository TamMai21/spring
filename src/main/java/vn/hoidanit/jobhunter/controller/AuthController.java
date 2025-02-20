package vn.hoidanit.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO.UserLogin;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.utils.SecurityUtils;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtils securityUtils;
        private final UserService userService;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtils securityUtils,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtils = securityUtils;
                this.userService = userService;
        }

        @PostMapping("/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {

                // nap input vao Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xac thuc nguoi dung, can override lai ham loadUserByUsername
                org.springframework.security.core.Authentication authentication = authenticationManagerBuilder
                                .getObject()
                                .authenticate(authenticationToken);

                // create token
                String access_token = this.securityUtils.createAccessToken(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication); // nap security context

                ResLoginDTO resLoginDTO = new ResLoginDTO();
                User currentUserLogin = this.userService.handleGetUserByUsername(loginDTO.getUsername());

                if (currentUserLogin != null) {
                        ResLoginDTO.UserLogin userLogin = resLoginDTO.new UserLogin(currentUserLogin.getId(),
                                        currentUserLogin.getEmail(), currentUserLogin.getName());
                        resLoginDTO.setUser(userLogin);
                }

                resLoginDTO.setAccessToken(access_token);

                String refreshToken = this.securityUtils.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
                this.userService.updateUserToken(access_token, loginDTO.getUsername());

                return ResponseEntity.ok().body(resLoginDTO);
        }
}
