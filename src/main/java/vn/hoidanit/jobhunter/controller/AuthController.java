package vn.hoidanit.jobhunter.controller;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
import vn.hoidanit.jobhunter.utils.annotation.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtils securityUtils;
        private final UserService userService;

        @Value("${me.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtils securityUtils,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtils = securityUtils;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {

                // nap input vao Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xac thuc nguoi dung, can override lai ham loadUserByUsername
                org.springframework.security.core.Authentication authentication = authenticationManagerBuilder
                                .getObject()
                                .authenticate(authenticationToken);

                // nap thong tin nguoi dung dang nhap vao security context (de sau nay su dung)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO resLoginDTO = new ResLoginDTO();
                User currentUserLogin = this.userService.handleGetUserByUsername(loginDTO.getUsername());

                if (currentUserLogin != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserLogin.getId(),
                                        currentUserLogin.getEmail(), currentUserLogin.getName());
                        resLoginDTO.setUser(userLogin);
                }

                String access_token = this.securityUtils.createAccessToken(authentication.getName(),
                                resLoginDTO.getUser());// sau nay neu co them quyen han, authority thi them 1 tham so
                                                       // nua
                resLoginDTO.setAccessToken(access_token);

                String refreshToken = this.securityUtils.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
                this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

                // set cookie
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(resLoginDTO);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Fetch account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
                String email = SecurityUtils.getCurrentUserLogin().isPresent()
                                ? SecurityUtils.getCurrentUserLogin().get()
                                : "";

                User currentUserLogin = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

                if (currentUserLogin != null) {
                        userLogin.setId(currentUserLogin.getId());
                        userLogin.setEmail(currentUserLogin.getEmail());
                        userLogin.setName(currentUserLogin.getName());
                        userGetAccount.setUser(userLogin);
                }
                return ResponseEntity.ok().body(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get user by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "notfound") String refresh_token)
                        throws IdInvalidException {

                if (refresh_token.equals("notfound")) {
                        throw new IdInvalidException("Session has expired, please login again...");
                }

                // check valid token
                Jwt decodedToken = this.securityUtils.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check by token + email
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("Invalid refresh token");
                }

                //

                ResLoginDTO resLoginDTO = new ResLoginDTO();
                User currentUserLogin = this.userService.handleGetUserByUsername(email);

                if (currentUserLogin != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserLogin.getId(),
                                        currentUserLogin.getEmail(), currentUserLogin.getName());
                        resLoginDTO.setUser(userLogin);
                }

                String access_token = this.securityUtils.createAccessToken(email, resLoginDTO.getUser());
                resLoginDTO.setAccessToken(access_token);

                String newRefreshToken = this.securityUtils.createRefreshToken(email, resLoginDTO);
                this.userService.updateUserToken(newRefreshToken, email);

                // set cookie
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", newRefreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(resLoginDTO);
        }

        @PostMapping("/auth/logout")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtils.getCurrentUserLogin().isPresent()
                                ? securityUtils.getCurrentUserLogin().get()
                                : "";
                if (email.equals("")) {
                        throw new IdInvalidException("Invalid access token");
                }

                this.userService.updateUserToken(null, email);

                // xoa cookie
                ResponseCookie deletResponseCookie = ResponseCookie.from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deletResponseCookie.toString()).body(null);
        }

}
