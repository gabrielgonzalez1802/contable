package com.contable.controller;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.service.ICuentasContablesService;
import com.contable.service.IEntradasDiariosService;

@Controller
@RequestMapping("/contabilidad")
public class ContabilidadController {
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IEntradasDiariosService serviceEntradasDiarios;

	@GetMapping("/mostrarContabilidad")
	public String mostrarContabilidad(Model model, HttpSession session) {
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoDesc((Empresa) session.getAttribute("empresa"));
//		String codigoPadre = "";

		for (CuentaContable cuentaContable : cuentasContables) {
//			if(cuentaContable.getCuentaControl()==null && cuentaContable.getTipo().equals("C")) {
//				cuentaContable.setClase("padrecc ccpadre-"+cuentaContable.getCodigo()+" "+"cccode"+cuentaContable.getCodigo());
//				codigoPadre = cuentaContable.getCodigo();
//			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
//				cuentaContable.setClase("hijocc cchijo-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
//			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("A")) {
//				cuentaContable.setClase("nietocc ccnieto-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
//			}
			
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContable(cuentaContable);
			for (EntradaDiario entradaDiario : entradasDiarios) {
				BigDecimal monto = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContable.setMonto(monto.doubleValue());
			}
			
			if(cuentaContable.getTipo().equals("C")) {
				List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
							buscarPorEmpresaCuentaControl((Empresa) session.getAttribute("empresa"), 
									cuentaContable.getCodigo());
				
				if(!cuentasContablesTemp.isEmpty()) {
					Double montoTemp = 0.0;
					for (CuentaContable cuentaContableTemp : cuentasContablesTemp) {
						List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContable(cuentaContableTemp);
						for (EntradaDiario entradaDiarioTemp : entradasDiariosTemp) {
							montoTemp+=entradaDiarioTemp.getCredito().subtract(entradaDiarioTemp.getDebito()).doubleValue();
						}
						cuentaContable.setMonto(montoTemp);
					}
				}
			}
		}	
		
		
		for (CuentaContable cuentaContable2 : cuentasContables) {
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContable(cuentaContable2);
			double valorInicial = 0.0;
			for (EntradaDiario entradaDiario : entradasDiarios) {
					valorInicial += entradaDiario.getCredito().doubleValue()-entradaDiario.getDebito().doubleValue();
			}
				
			cuentaContable2.setMonto(valorInicial);
		}
			
		for (CuentaContable cuentaContableTemp : cuentasContables) {

			List<CuentaContable> cuentasTemp = serviceCuentasContables.
						buscarPorEmpresaIdCuentaControl((Empresa) session.getAttribute("empresa"), cuentaContableTemp.getId());
				
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

		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipoEstado((Empresa) session.getAttribute("empresa"), "A", 0);
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		model.addAttribute("cuentasContables", newOrder);
		model.addAttribute("cuentaContable", new CuentaContable());
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
