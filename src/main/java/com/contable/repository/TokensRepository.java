package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Token;

public interface TokensRepository extends JpaRepository<Token, Integer> {
	List<Token> findByEstado(Integer estado);
	List<Token> findByToken(String token);
}
