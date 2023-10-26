package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.exception.CondutorExistenteException;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private CondutorRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var condutor = repository.findFirstByLogin(username);
        return condutor
                .orElseThrow(() -> new CondutorExistenteException("Não há condutor com o login informado"))
                .getUsuario();
    }
}
