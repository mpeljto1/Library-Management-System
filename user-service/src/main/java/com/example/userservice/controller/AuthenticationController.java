package com.example.userservice.controller;



import com.example.userservice.security.JwtTokenUtil;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.model.AuthToken;
import com.example.userservice.model.EmailToken;
import com.example.userservice.model.LoginUser;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/token")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;
    

    @RequestMapping(value = "/generate-token", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final User user = userRepository.findByUsername(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new AuthToken(token));
    }
    
    @RequestMapping(value = "/generate-token-email", method = RequestMethod.POST)
    public ResponseEntity<?> registerGoogle(@RequestBody EmailToken emailToken) throws AuthenticationException {
    	
    	
        final User user = userRepository.findByEmail(emailToken.getEmail());
        if (user == null) throw new ResourceNotFoundException("User doesn't exist");
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new AuthToken(token));
    }
    
    @RequestMapping(value = "/proba", method = RequestMethod.GET)
    public ResponseEntity<?> proba() throws AuthenticationException {
    	
    	
        return ResponseEntity.ok("Ovo radi");
    }

}

