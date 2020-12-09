package com.contable.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.Usuario;
import com.contable.service.ISuplidoresService;

@Controller
@RequestMapping("/suplidores")
public class SuplidoresController {
	
	@Autowired
	private ISuplidoresService serviceSuplidores;

	@GetMapping("/listaSuplidores")
	public String listaSuplidores(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Suplidor> suplidores = serviceSuplidores.buscarPorEmpresa(empresa);
		model.addAttribute("suplidores", suplidores);
		return "suplidores/listaSuplidores :: suplidores";
	}
	
	@PostMapping("/agregarSuplidorForCompra")
	public String agregarSuplidorForCompra(Model model, HttpSession session, 
			String nombre, String telefono, String direccion, String rnc) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Suplidor suplidor = new Suplidor();
		suplidor.setDireccion(direccion);
		suplidor.setEmpresa(empresa);
		suplidor.setFecha(new Date());
		suplidor.setNombre(nombre);
		suplidor.setRnc(rnc);
		suplidor.setTelefono(telefono);
		suplidor.setUsuario(usuario);
		serviceSuplidores.guardar(suplidor);
		List<Suplidor> suplidores = serviceSuplidores.buscarPorEmpresa(empresa);
		model.addAttribute("suplidores", suplidores);
		return "contabilidad/compras :: #selectSuplidorForProduct";
	}
	
}
