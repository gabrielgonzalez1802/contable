package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empresa;
import com.contable.model.ProcesoBancarioTemp;
import com.contable.model.Usuario;

public interface ProcesosBancariosTempRepository extends JpaRepository<ProcesoBancarioTemp, Integer> {
	List<ProcesoBancarioTemp> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
}
