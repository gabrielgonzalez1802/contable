package com.contable.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Cliente;
import com.contable.model.Usuario;
import com.contable.service.IClientesService;

@Controller
@RequestMapping("/clientes")
public class ClientesController {
	
	@Autowired
	private IClientesService serviceClientes;
	
	@GetMapping("/")
	public String getListaClientes(Model model) {
		List<Cliente> listaClientes = serviceClientes.buscarTodos().
				stream().filter(e -> e.getEstado() == 1).collect(Collectors.toList());
		model.addAttribute("listaClientes", listaClientes);
		return "clientes/listaClientes :: listaCliente";
	}
	
	@GetMapping("/agregar")
	public String formularioCliente(Model model) {
		Cliente cliente = new Cliente();
		model.addAttribute("cliente", cliente);
		return "clientes/form :: form";
	}
	
	@GetMapping("/modificar/{id}")
	public String formularioModificarCliente(Model model, @PathVariable(name = "id") Integer id) {
		Cliente cliente = serviceClientes.buscarPorId(id);
		if(cliente!=null) {
			model.addAttribute("cliente", cliente);
		}else {
			model.addAttribute("cliente", new Cliente());
		}
		return "clientes/form :: form";
	} 
	
	@GetMapping("/eliminar/{id}")
	public String eliminarCliente(Model model, @PathVariable(name = "id") Integer id, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Cliente cliente = serviceClientes.buscarPorId(id);
		cliente.setEliminado(new Date());
		cliente.setEstado(0);
		cliente.setUsuarioEliminado(usuario);
		serviceClientes.guardar(cliente);
		List<Cliente> listaClientes = serviceClientes.buscarTodos().
				stream().filter(e -> e.getEstado() == 1).collect(Collectors.toList());
		model.addAttribute("listaClientes", listaClientes);
		return "clientes/listaClientes :: listaCliente";
	} 
	
	@PostMapping("/guardar")
	public String guardar(Model model, @ModelAttribute("cliente") Cliente cliente, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String response = "INSERT";
		if(cliente.getId()!=null) {
			response = "UPDATE";
			Cliente originalCliente = serviceClientes.buscarPorId(cliente.getId());
			cliente.setModificado(new Date());
			cliente.setUsuario(originalCliente.getUsuario());
			cliente.setUsuario_modificado(usuario);
			cliente.setCreado(originalCliente.getCreado());
		}else {
			cliente.setUsuario(usuario);
		}
			
		serviceClientes.guardar(cliente);
		model.addAttribute("responseCliente", response);
		model.addAttribute("cliente", cliente);
		return "home :: #grupoResponseCliente";
	}
	
}