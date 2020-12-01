package com.contable.controller;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Carpeta;
import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.service.ICarpetasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.ICuentasEnlacesService;
import com.contable.service.IEntradasDiariosService;

@Controller
@RequestMapping("/contabilidad")
public class ContabilidadController {
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IEntradasDiariosService serviceEntradasDiarios;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private ICuentasEnlacesService serviceCuentasEnlaces;

	@GetMapping("/mostrarContabilidad")
	public String mostrarContabilidad(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Carpeta carpeta = null;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoDesc(empresa);
		//String codigoPadre = "";

		for (CuentaContable cuentaContable : cuentasContables) {
//			if(cuentaContable.getCuentaControl()==null && cuentaContable.getTipo().equals("C")) {
//				cuentaContable.setClase("padrecc ccpadre-"+cuentaContable.getCodigo()+" "+"cccode"+cuentaContable.getCodigo());
//				codigoPadre = cuentaContable.getCodigo();
//			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
//				cuentaContable.setClase("hijocc cchijo-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
//			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("A")) {
//				cuentaContable.setClase("nietocc ccnieto-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
//			}
			
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable, empresa, carpeta);
			for (EntradaDiario entradaDiario : entradasDiarios) {
				BigDecimal monto = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContable.setMonto(monto.doubleValue());
			}
			
			if(cuentaContable.getTipo().equals("C")) {
				List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
							buscarPorEmpresaCuentaControl(empresa, 
									cuentaContable.getCodigo());
				
				if(!cuentasContablesTemp.isEmpty()) {
					Double montoTemp = 0.0;
					for (CuentaContable cuentaContableTemp : cuentasContablesTemp) {
						List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableTemp, empresa, carpeta);
						for (EntradaDiario entradaDiarioTemp : entradasDiariosTemp) {
							montoTemp+=entradaDiarioTemp.getCredito().subtract(entradaDiarioTemp.getDebito()).doubleValue();
						}
						cuentaContable.setMonto(montoTemp);
					}
				}
			}
		}	
		
		
		for (CuentaContable cuentaContable2 : cuentasContables) {
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable2, empresa, carpeta);
			double valorInicial = 0.0;
			for (EntradaDiario entradaDiario : entradasDiarios) {
					valorInicial += entradaDiario.getCredito().doubleValue()-entradaDiario.getDebito().doubleValue();
			}
				
			cuentaContable2.setMonto(valorInicial);
		}
			
		for (CuentaContable cuentaContableTemp : cuentasContables) {

			List<CuentaContable> cuentasTemp = serviceCuentasContables.
						buscarPorEmpresaIdCuentaControl(empresa, cuentaContableTemp.getId());
				
			double valor = 0;
							
			for (CuentaContable cuentaContableTemp2 : cuentasTemp) {
					valor += cuentaContableTemp2.getMonto();
			}
			
			double valorFinal = cuentaContableTemp.getMonto() + valor;

			cuentaContableTemp.setMonto(valorFinal);
				
		}
	
		List<CuentaContable> newOrder = new LinkedList<>();
		
		for (int i = cuentasContables.size(); i > 0; i--) {
			newOrder.add(cuentasContables.get(i-1));
		}

		List<CuentaContable> cuentasContablesAuxiliaresGeneral = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 0);
		List<CuentaContable> cuentasContablesAuxiliaresIniciadas = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 1);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("empresa", empresa);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliaresGeneral);
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		model.addAttribute("cuentasContablesAuxiliaresIniciadas", cuentasContablesAuxiliaresIniciadas);
		model.addAttribute("cuentasContables", newOrder);
		model.addAttribute("cuentaContable", new CuentaContable());
		//Cuenta de enlace	
		CuentaEnlace capitalPrestamoEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "capital", "prestamos");
		if(capitalPrestamoEnlace == null) {
			capitalPrestamoEnlace = new CuentaEnlace();
			capitalPrestamoEnlace.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("capitalPrestamoEnlace", capitalPrestamoEnlace.getCuentaContable());
		CuentaEnlace interesEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "interes", "prestamos");
		if(interesEnlace == null) {
			interesEnlace = new CuentaEnlace();
			interesEnlace.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("interesPrestamoEnlace", interesEnlace.getCuentaContable());
		CuentaEnlace gastosCierreEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "gastosCierre", "prestamos");
		if(gastosCierreEnlace == null) {
			gastosCierreEnlace = new CuentaEnlace();
			gastosCierreEnlace.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("gastosCierrePrestamoEnlace", gastosCierreEnlace.getCuentaContable());
		CuentaEnlace cuentaEnlaceAdicionales = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "adicionales", "prestamos");
		if(cuentaEnlaceAdicionales == null) {
			cuentaEnlaceAdicionales = new CuentaEnlace();
			cuentaEnlaceAdicionales.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("cobrosAdicionalesPrestamoEnlace", cuentaEnlaceAdicionales.getCuentaContable());
		return "contabilidad/contabilidad :: contabilidad";
	}
	
	@GetMapping("/mostrarHerencia/{id}/{codigoPadre}")
	public String mostrarHerencia(Model model, HttpSession session, @PathVariable("id") Integer id, @PathVariable("codigoPadre") String codigoPadre) {
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(id);
		if(cuentaContable.getCuentaControl()==null && cuentaContable.getTipo().equals("C")) {
			cuentaContable.setClase("padrecc ccpadre-"+cuentaContable.getCodigo()+" "+"cccode"+cuentaContable.getCodigo());
		}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
			cuentaContable.setClase("hijocc cchijo-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
		}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("A")) {
			cuentaContable.setClase("nietocc ccnieto-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
		}
		
		String[] splitClass = cuentaContable.getClase().split(" ");
		if(splitClass.length == 4) {
			
		}else {
			
		}
		
		
		model.addAttribute("cuentaContable", new CuentaContable());
		return "contabilidad/contabilidad :: contabilidad";
	}
	
}
