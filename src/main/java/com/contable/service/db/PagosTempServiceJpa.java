package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;
import com.contable.repository.PagosTempRepository;
import com.contable.service.IPagosTempService;

@Service
public class PagosTempServiceJpa implements IPagosTempService{
	
	@Autowired
	private PagosTempRepository repo;

	@Override
	public PagoTemp buscarPorId(Integer id) {
		Optional<PagoTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<PagoTemp> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}
	
	@Override
	public List<PagoTemp> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<PagoTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public List<PagoTemp> buscarPorEmpresaEntradaDiario(Empresa empresa, EntradaDiario entradaDiario) {
		return repo.findByEmpresaAndEntradaDiario(empresa, entradaDiario);
	}

	@Override
	public List<PagoTemp> buscarPorEmpresaEntradaDiarioUsuario(Empresa empresa, EntradaDiario entradaDiario,
			Usuario usuario) {
		return repo.findByEmpresaAndEntradaDiarioAndUsuario(empresa, entradaDiario, usuario);
	}

	@Override
	public List<PagoTemp> buscarPorEmpresaCompra(Empresa empresa, Compra compra) {
		return repo.findByEmpresaAndCompra(empresa, compra);
	}

	@Override
	public void guardar(PagoTemp pagoTemp) {
		repo.save(pagoTemp);
	}

	@Override
	public void eliminar(PagoTemp pagoTemp) {
		repo.delete(pagoTemp);
	}

	@Override
	public void eliminar(List<PagoTemp> pagosTemp) {
		repo.deleteAll(pagosTemp);
	}
	
}
