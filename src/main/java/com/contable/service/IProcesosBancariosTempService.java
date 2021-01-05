package com.contable.service;

import java.util.List;

import com.contable.model.Empresa;
import com.contable.model.ProcesoBancarioTemp;
import com.contable.model.Usuario;

public interface IProcesosBancariosTempService {
	ProcesoBancarioTemp buscarPorId(Integer id);
	List<ProcesoBancarioTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	void guardar(ProcesoBancarioTemp procesoBancarioTemp);
	void eliminar(List<ProcesoBancarioTemp> procesosBancariosTemp);
	void eliminar(ProcesoBancarioTemp procesoBancarioTemp);
}
