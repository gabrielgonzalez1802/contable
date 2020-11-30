package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.ComprobanteFiscal;
import com.contable.model.Empresa;

public interface ComprobantesFiscalesRepository extends JpaRepository<ComprobanteFiscal, Integer> {
	List<ComprobanteFiscal> findByEmpresa(Empresa empresa);
}
