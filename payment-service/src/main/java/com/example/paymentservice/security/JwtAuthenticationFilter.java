package com.example.paymentservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.paymentservice.UserServiceProxy;
import com.example.paymentservice.model.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.example.paymentservice.model.Constants.HEADER_STRING;
import static com.example.paymentservice.model.Constants.TOKEN_PREFIX;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
	UserServiceProxy userServiceProxy;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX,"");
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("an error occured during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("the token is expired and not valid anymore", e);
            } catch(SignatureException e){
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

           //UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        	System.out.println("Ovo je test sta je u headeru: " + header);
        	System.out.println("Ovo je test sta je u username: " + username);
        	User u = userServiceProxy.getUserByUsername(username, header);
        	if(u == null){
    			throw new UsernameNotFoundException("Invalid username or password.");
    		}
        	UserDetails userDetails = new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), 
        			Arrays.asList(new SimpleGrantedAuthority(u.getRole().toUpperCase())));
        	
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
            	Claims claims = jwtTokenUtil.getAllClaimsFromToken(authToken);
                String user = claims.getSubject();
                String role = claims.get("role").toString();
                System.out.println(role);
                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                
                grantedAuthorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                		userDetails, null, grantedAuthorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}
