package com.contable.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.service.ICuentasContablesService;

@Controller
@RequestMapping("/contabilidad")
public class ContabilidadController {
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;

	@GetMapping("/mostrarContabilidad")
	public String mostrarContabilidad(Model model, HttpSession session) {
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigo((Empresa) session.getAttribute("empresa"));
		model.addAttribute("cuentasContables", cuentasContables);
		model.addAttribute("cuentaContable", new CuentaContable());
		return "contabilidad/contabilidad :: contabilidad";
	}
	
}
