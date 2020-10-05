package com.contable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.ComprobanteFiscal;

public interface ComprobantesFiscalesRepository extends JpaRepository<ComprobanteFiscal, Integer> {
}
