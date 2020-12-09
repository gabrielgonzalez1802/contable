package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.Pago;
import com.contable.model.Usuario;
import com.contable.repository.PagosRepository;
import com.contable.service.IPagosService;

@Service
public class PagosServiceJpa implements IPagosService {
	
	@Autowired
	private PagosRepository repo;

	@Override
	public Pago buscarPorId(Integer id) {
		Optional<Pago> optional =repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Pago> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<Pago> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public List<Pago> buscarPorEmpresaEntradaDiario(Empresa empresa, EntradaDiario entradaDiario) {
		return repo.findByEmpresaAndEntradaDiario(empresa, entradaDiario);
	}

	@Override
	public List<Pago> buscarPorEmpresaEntradaDiarioUsuario(Empresa empresa, EntradaDiario entradaDiario,
			Usuario usuario) {
		return repo.findByEmpresaAndEntradaDiarioAndUsuario(empresa, entradaDiario, usuario);
	}

	@Override
	public List<Pago> buscarPorEmpresaCompra(Empresa empresa, Compra compra) {
		return repo.findByEmpresaAndCompra(empresa, compra);
	}

	@Override
	public void guardar(Pago pago) {
		repo.save(pago);
	}

	@Override
	public void eliminar(Pago pago) {
		repo.delete(pago);
	}

}
