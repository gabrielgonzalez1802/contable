package com.contable.service;

import java.util.List;

import com.contable.model.ComprobanteFiscal;

public interface IComprobantesFiscalesService {
	ComprobanteFiscal buscarPorId(Integer id);
	List<ComprobanteFiscal> buscarTodos();
	void guardar(ComprobanteFiscal comprobanteFiscal);
	void eliminar(ComprobanteFiscal comprobanteFiscal);
}
