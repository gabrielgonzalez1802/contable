package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Cliente;

public interface ClientesRepository extends JpaRepository<Cliente, Integer> {
	List<Cliente> findByEstado(Integer estado);
}
