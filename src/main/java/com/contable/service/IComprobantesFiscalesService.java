package com.contable.service;

import java.util.List;

import com.contable.model.ComprobanteFiscal;
import com.contable.model.Empresa;

public interface IComprobantesFiscalesService {
	ComprobanteFiscal buscarPorId(Integer id);
	List<ComprobanteFiscal> buscarTodos();
	List<ComprobanteFiscal> buscarPorEmpresa(Empresa empresa);
	void guardar(ComprobanteFiscal comprobanteFiscal);
	void eliminar(ComprobanteFiscal comprobanteFiscal);
}
