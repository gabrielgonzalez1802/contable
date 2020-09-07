package com.contable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Cliente;
import com.contable.model.Prestamo;
import com.contable.service.IClientesService;
import com.contable.service.IPrestamosService;

@Controller
@RequestMapping("/prestamos")
public class PrestamosController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@GetMapping("/")
	public String listaPrestamos(Model model) {
		return "prestamos/listaPrestamos :: listaPrestamo";
	}
	
	@GetMapping("/agregar")
	public String agregarPrestamos(Model model) {
		Prestamo prestamo = new Prestamo();
		List<Cliente> clientes = serviceClientes.buscarPorEstado(1);
		model.addAttribute("clientes", clientes);
		model.addAttribute("prestamo", prestamo);
		return "prestamos/form :: form";
	}
}
