package com.contable.service;

import java.util.List;

import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.Usuario;

public interface ISuplidoresService {
	Suplidor buscarPorId(Integer id);
	List<Suplidor> buscarPorEmpresa(Empresa empresa);
	List<Suplidor> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	void guardar(Suplidor suplidor);
	void eliminar(Suplidor suplidor);
}
